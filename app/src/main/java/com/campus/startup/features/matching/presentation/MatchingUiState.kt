package com.campus.startup.features.matching.presentation

import com.campus.startup.features.matching.data.model.ConnectionRequest
import com.campus.startup.features.matching.data.model.DiscoverableProfile
import com.campus.startup.features.matching.data.model.StudentMatchProfile

sealed interface MatchingUiState {
    object Loading : MatchingUiState
    data class Success(
        val me: StudentMatchProfile?,
        val students: List<DiscoverableProfile>,
        val directory: List<DiscoverableProfile>,
        val connections: List<ConnectionRequest>,
        val pendingIncoming: Int,
        val complementaryCount: Int
    ) : MatchingUiState
    data class Error(val message: String) : MatchingUiState
}
