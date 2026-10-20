package com.campus.startup.features.matching.data.model

data class MatchResult(
    val compatibility: Int,
    val reasons: List<String>,
    val complementarySkills: List<String>,
    val sharedInterests: List<String>,
    val sharedExperience: List<String>,
    val profileReady: Boolean
)

data class DiscoverableProfile(
    val profile: StudentMatchProfile,
    val match: MatchResult,
    val connectionStatus: ConnectionStatus
)
