package com.campus.startup.features.matching.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.campus.startup.features.matching.data.model.DiscoverableProfile
import com.campus.startup.features.matching.data.model.MatchCatalog
import com.campus.startup.features.matching.data.model.MatchingFilters
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.matching.data.repository.MatchingRepository
import com.campus.startup.features.matching.data.model.MatchingDirectory
import com.campus.startup.features.matching.domain.CofounderMatcher
import com.campus.startup.features.matching.domain.ProfileFormErrors
import com.campus.startup.features.matching.domain.ProfileValidator
import com.campus.startup.features.matching.data.model.resolveConnectionStatus
import com.campus.startup.features.recruitment.data.model.StartupRoleCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MatchingViewModel(
    private val repository: MatchingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRole = MutableStateFlow(StartupRoleCategory.ALL)
    val selectedRole: StateFlow<StartupRoleCategory> = _selectedRole.asStateFlow()

    private val _skillFilters = MutableStateFlow<Set<String>>(emptySet())
    val skillFilters: StateFlow<Set<String>> = _skillFilters.asStateFlow()

    private val _interestFilters = MutableStateFlow<Set<String>>(emptySet())
    val interestFilters: StateFlow<Set<String>> = _interestFilters.asStateFlow()

    private val _experienceFilters = MutableStateFlow<Set<String>>(emptySet())
    val experienceFilters: StateFlow<Set<String>> = _experienceFilters.asStateFlow()

    private val _complementaryOnly = MutableStateFlow(false)
    val complementaryOnly: StateFlow<Boolean> = _complementaryOnly.asStateFlow()

    private val _formErrors = MutableStateFlow(ProfileFormErrors())
    val formErrors: StateFlow<ProfileFormErrors> = _formErrors.asStateFlow()

    private val _ready = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val filtersFlow: Flow<MatchingFilters> = combine(
        _searchQuery,
        _selectedRole,
        _skillFilters,
        combine(_interestFilters, _experienceFilters, _complementaryOnly) { interests, experience, complementary ->
            Triple(interests, experience, complementary)
        }
    ) { query, role, skills, rest ->
        MatchingFilters(
            query = query,
            role = role,
            skills = skills,
            interests = rest.first,
            experienceTags = rest.second,
            complementaryOnly = rest.third
        )
    }

    val uiState: StateFlow<MatchingUiState> = combine(
        repository.observeProfiles(),
        repository.observeConnections(),
        filtersFlow,
        combine(_ready, _error) { ready, error -> ready to error }
    ) { profiles, connections, filters, readyAndError ->
        val (ready, error) = readyAndError
        when {
            !ready -> MatchingUiState.Loading
            error != null -> MatchingUiState.Error(error)
            else -> {
                val me = profiles.find { it.isCurrentUser }
                val profileReady = me?.let(CofounderMatcher::isReady) == true
                val directory = profiles.filter { !it.isCurrentUser }.map { other ->
                    DiscoverableProfile(
                        profile = other,
                        match = if (me == null) CofounderMatcher.incomplete() else CofounderMatcher.evaluate(me, other),
                        connectionStatus = resolveConnectionStatus(
                            myId = MatchCatalog.CURRENT_USER_ID,
                            otherId = other.id,
                            connections = connections
                        )
                    )
                }
                MatchingUiState.Success(
                    me = me,
                    students = MatchingDirectory.filter(directory, filters, profileReady),
                    directory = directory.sortedByDescending { it.match.compatibility },
                    connections = connections,
                    pendingIncoming = connections.count { request ->
                        request.status.equals("PENDING", ignoreCase = true) &&
                            request.toProfileId == MatchCatalog.CURRENT_USER_ID
                    },
                    complementaryCount = directory.count { it.match.complementarySkills.isNotEmpty() }
                )
            }
        }
    }.catch { emit(MatchingUiState.Error(it.message ?: "Couldn't load co-founders.")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MatchingUiState.Loading
        )

    init {
        prepareDirectory()
    }

    fun retry() {
        _error.value = null
        _ready.value = false
        prepareDirectory()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onRoleSelected(role: StartupRoleCategory) {
        _selectedRole.value = role
    }

    fun toggleSkillFilter(skill: String) {
        _skillFilters.value = _skillFilters.value.toggle(skill)
    }

    fun toggleInterestFilter(interest: String) {
        _interestFilters.value = _interestFilters.value.toggle(interest)
    }

    fun toggleExperienceFilter(tag: String) {
        _experienceFilters.value = _experienceFilters.value.toggle(tag)
    }

    fun onComplementaryOnlyChanged(enabled: Boolean) {
        _complementaryOnly.value = enabled
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedRole.value = StartupRoleCategory.ALL
        _skillFilters.value = emptySet()
        _interestFilters.value = emptySet()
        _experienceFilters.value = emptySet()
        _complementaryOnly.value = false
    }

    fun clearFormErrors() {
        _formErrors.value = ProfileFormErrors()
    }

    fun consumeStatusMessage() {
        _statusMessage.value = null
    }

    fun saveProfile(profile: StudentMatchProfile): Boolean {
        val errors = ProfileValidator.validate(profile)
        _formErrors.value = errors
        if (!errors.isValid) return false
        viewModelScope.launch {
            repository.saveCurrentProfile(
                profile.copy(
                    id = MatchCatalog.CURRENT_USER_ID,
                    isCurrentUser = true,
                    profileComplete = true
                )
            )
            _statusMessage.value = "Profile saved. Recommendations now use your skills and goals."
        }
        return true
    }

    fun connect(profileId: String) {
        viewModelScope.launch {
            _statusMessage.value = repository.sendConnection(profileId)
        }
    }

    fun acceptFrom(profileId: String) {
        val success = uiState.value as? MatchingUiState.Success ?: return
        val request = success.connections.find { item ->
            item.status.equals("PENDING", ignoreCase = true) &&
                item.fromProfileId == profileId &&
                item.toProfileId == MatchCatalog.CURRENT_USER_ID
        } ?: return
        acceptRequest(request.id)
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            _statusMessage.value = repository.acceptConnection(requestId)
        }
    }

    private fun prepareDirectory() {
        viewModelScope.launch {
            try {
                repository.checkAndSeed()
            } catch (error: Exception) {
                _error.value = error.message ?: "Couldn't prepare co-founder profiles."
            } finally {
                _ready.value = true
            }
        }
    }

    private fun Set<String>.toggle(value: String): Set<String> {
        return if (any { it.equals(value, ignoreCase = true) }) {
            filterNot { it.equals(value, ignoreCase = true) }.toSet()
        } else {
            this + value
        }
    }
}

class MatchingViewModelFactory(
    private val repository: MatchingRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchingViewModel::class.java)) {
            return MatchingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
