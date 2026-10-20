package com.campus.startup.features.startupprofiles.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.campus.startup.features.startupprofiles.data.model.StartupEntity
import com.campus.startup.features.startupprofiles.data.repository.StartupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StartupProfilesViewModel(
    private val repository: StartupRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _domainFilter = MutableStateFlow<String?>(null)
    val domainFilter: StateFlow<String?> = _domainFilter.asStateFlow()

    private val _stageFilter = MutableStateFlow<String?>(null)
    val stageFilter: StateFlow<String?> = _stageFilter.asStateFlow()

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
                            s.collaborationTypes.contains(needle, ignoreCase = true))
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
