package com.campus.startup.features.opportunities.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.campus.startup.features.opportunities.data.local.OpportunityEntity
import com.campus.startup.features.opportunities.data.model.Opportunity
import com.campus.startup.features.opportunities.data.model.OpportunityType
import com.campus.startup.features.opportunities.data.repository.OpportunityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class OpportunitiesViewModel(
    private val repository: OpportunityRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow(OpportunityType.ALL)
    val selectedType: StateFlow<OpportunityType> = _selectedType.asStateFlow()

    val uiState: StateFlow<OpportunitiesUiState> = combine(
        repository.getOpportunities(),
        _selectedType,
        _searchQuery
    ) { opps, typeFilter, query ->
        val filtered = opps.filter { opp ->
            val matchesType = (typeFilter == OpportunityType.ALL || opp.type == typeFilter)
            val matchesQuery = query.isBlank() ||
                opp.title.contains(query, ignoreCase = true) ||
                opp.organization.contains(query, ignoreCase = true) ||
                opp.domain.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
        val bookmarkedCount = opps.count { it.isBookmarked }
        OpportunitiesUiState.Success(
            opportunities = filtered,
            totalCount = opps.size,
            bookmarkedCount = bookmarkedCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OpportunitiesUiState.Loading
    )

    val savedOpportunities: StateFlow<List<OpportunityEntity>> = repository.getSavedOpportunities()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTypeSelected(type: OpportunityType) {
        _selectedType.value = type
    }

    fun toggleBookmark(opportunity: Opportunity) {
        viewModelScope.launch {
            repository.toggleBookmark(opportunity)
        }
    }

    fun getOpportunityById(id: String): Opportunity? {
        return repository.getOpportunityById(id)
    }

    fun postOpportunity(
        title: String,
        organization: String,
        type: OpportunityType,
        domain: String,
        stipendOrPrize: String,
        location: String,
        deadlineDays: Int,
        description: String
    ) {
        val newOpp = Opportunity(
            id = "opp_" + UUID.randomUUID().toString().take(8),
            title = title,
            organization = organization,
            type = type,
            domain = domain,
            stipendOrPrize = stipendOrPrize,
            location = location,
            deadlineDays = deadlineDays,
            description = description,
            requirements = listOf("Active student enrollment", "Willingness to learn and collaborate"),
            perks = listOf("Networking with founders", "Hands-on experience", "Certificate / Recommendation"),
            applyUrl = "https://campus-startup.edu/apply"
        )
        repository.postOpportunity(newOpp)
    }
}

class OpportunitiesViewModelFactory(
    private val repository: OpportunityRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OpportunitiesViewModel::class.java)) {
            return OpportunitiesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
