package com.campus.startup.features.opportunities.presentation

import com.campus.startup.features.opportunities.data.model.Opportunity

sealed interface OpportunitiesUiState {
    object Loading : OpportunitiesUiState
    data class Success(
        val opportunities: List<Opportunity>,
        val totalCount: Int,
        val bookmarkedCount: Int
    ) : OpportunitiesUiState
    data class Error(val message: String) : OpportunitiesUiState
}
