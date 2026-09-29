package com.campus.startup.features.recruitment.presentation

import com.campus.startup.features.recruitment.data.model.StartupOpening

sealed interface RecruitmentUiState {
    object Loading : RecruitmentUiState
    data class Success(
        val openings: List<StartupOpening>,
        val totalCount: Int,
        val highMatchCount: Int,
        val savedCount: Int
    ) : RecruitmentUiState
    data class Error(val message: String) : RecruitmentUiState
}
