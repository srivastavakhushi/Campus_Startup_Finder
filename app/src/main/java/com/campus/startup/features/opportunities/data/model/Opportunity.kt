package com.campus.startup.features.opportunities.data.model

data class Opportunity(
    val id: String,
    val title: String,
    val organization: String,
    val type: OpportunityType,
    val domain: String,
    val stipendOrPrize: String,
    val location: String,
    val deadlineDays: Int,
    val description: String,
    val requirements: List<String> = emptyList(),
    val perks: List<String> = emptyList(),
    val applyUrl: String = "https://campus-startup.edu/apply",
    val isBookmarked: Boolean = false
)
