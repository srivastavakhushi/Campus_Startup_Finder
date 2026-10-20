package com.campus.startup.features.startupprofiles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.campus.startup.features.matching.data.model.StudentMatchProfile
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import com.campus.startup.features.startupprofiles.data.model.toCsvList
import com.campus.startup.features.startupprofiles.data.repository.StartupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class StartupRecommendation(
    val startup: StartupEntity,
    val matchedInterests: List<String>,
    val matchScore: Int,
    val isRecruited: Boolean
)

class StartupProfilesViewModel(
    private val repository: StartupRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.checkAndSeedStartups()
        }
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _domainFilter = MutableStateFlow<String?>(null)
    val domainFilter: StateFlow<String?> = _domainFilter.asStateFlow()

    private val _stageFilter = MutableStateFlow<String?>(null)
    val stageFilter: StateFlow<String?> = _stageFilter.asStateFlow()

    val allStartups: StateFlow<List<StartupEntity>> = repository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val savedStartups: StateFlow<List<StartupEntity>> = repository.observeSavedStartups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val startups: StateFlow<List<StartupEntity>> = combine(
        repository.observeAll(), _query, _domainFilter, _stageFilter
    ) { all, q, domain, stage ->
        val needle = q.trim()
        all.filter { s ->
            (domain == null || s.domain == domain) &&
                (stage == null || s.stage == stage) &&
                (needle.isEmpty() ||
                    s.name.contains(needle, ignoreCase = true) ||
                    s.domain.contains(needle, ignoreCase = true) ||
                    s.description.contains(needle, ignoreCase = true) ||
                    s.requirements.contains(needle, ignoreCase = true) ||
                    s.collaborationTypes.contains(needle, ignoreCase = true) ||
                    s.tags.contains(needle, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(value: String) { _query.value = value }
    fun onDomainFilterChange(value: String?) { _domainFilter.value = value }
    fun onStageFilterChange(value: String?) { _stageFilter.value = value }

    fun observeStartup(id: String): Flow<StartupEntity?> = repository.observeById(id)
    suspend fun getStartup(id: String): StartupEntity? = repository.getById(id)

    fun save(startup: StartupEntity) {
        viewModelScope.launch { repository.save(startup) }
    }

    fun delete(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun toggleInterested(startup: StartupEntity) {
        viewModelScope.launch {
            repository.toggleInterested(startup)
        }
    }

    fun toggleSaved(startup: StartupEntity) {
        viewModelScope.launch {
            repository.toggleSaved(startup)
        }
    }

    fun toggleRecruited(startup: StartupEntity, role: String = "Core Team Member") {
        viewModelScope.launch {
            repository.setRecruited(startup.id, !startup.isRecruited, role)
        }
    }

    fun evaluateStartupForProfile(
        startup: StartupEntity,
        profile: StudentMatchProfile?,
        recruitedStartupNames: Set<String> = emptySet()
    ): StartupRecommendation {
        val isRecruitedInThis = startup.isRecruited ||
            (profile?.recruitedStartupIds?.contains(startup.id) == true) ||
            recruitedStartupNames.any { it.equals(startup.name, ignoreCase = true) }

        if (profile == null || !profile.profileComplete) {
            return StartupRecommendation(
                startup = startup,
                matchedInterests = emptyList(),
                matchScore = 0,
                isRecruited = isRecruitedInThis
            )
        }

        val userTopics = (profile.areasOfInterest + profile.interests + profile.skills)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        val startupKeywords = buildList {
            add(startup.domain)
            addAll(startup.tags.toCsvList())
            addAll(startup.collaborationTypes.toCsvList())
        }

        val matched = userTopics.filter { userTopic ->
            startupKeywords.any { kw ->
                kw.equals(userTopic, ignoreCase = true) ||
                    kw.contains(userTopic, ignoreCase = true) ||
                    userTopic.contains(kw, ignoreCase = true)
            } || startup.description.contains(userTopic, ignoreCase = true) ||
                startup.requirements.contains(userTopic, ignoreCase = true)
        }.distinct()

        val domainDirectMatch = profile.areasOfInterest.any {
            it.equals(startup.domain, ignoreCase = true) ||
                startup.domain.contains(it, ignoreCase = true) ||
                it.contains(startup.domain, ignoreCase = true)
        } || profile.interests.any {
            it.equals(startup.domain, ignoreCase = true)
        }

        val baseScore = when {
            domainDirectMatch && matched.size >= 2 -> 94
            domainDirectMatch -> 85
            matched.size >= 3 -> 88
            matched.size == 2 -> 78
            matched.size == 1 -> 65
            startup.isInterested || startup.isSaved -> 60
            else -> 0
        }

        return StartupRecommendation(
            startup = startup,
            matchedInterests = matched,
            matchScore = baseScore,
            isRecruited = isRecruitedInThis
        )
    }
}

class StartupProfilesViewModelFactory(
    private val repository: StartupRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(StartupProfilesViewModel::class.java))
        return StartupProfilesViewModel(repository) as T
    }
}
