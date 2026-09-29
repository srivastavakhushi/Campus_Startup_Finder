package com.campus.startup.core.navigation

sealed class Screen(val route: String) {
    // Startup Recruitment (Primary Feature)
    object Recruitment : Screen("startup_recruitment")
    object RecruitmentDetail : Screen("opening_detail/{openingId}") {
        fun createRoute(openingId: String): String = "opening_detail/$openingId"
    }
    object ApplicationsAndFounder : Screen("applications_and_founder")

    // Campus Opportunities Feed (Hackathons, Grants, Competitions)
    object Feed : Screen("opportunities_feed")
    object Detail : Screen("opportunity_detail/{opportunityId}") {
        fun createRoute(opportunityId: String): String = "opportunity_detail/$opportunityId"
    }
    object Saved : Screen("saved_opportunities")
}
