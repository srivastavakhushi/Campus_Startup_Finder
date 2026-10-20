package com.campus.startup.core.navigation

sealed class Screen(val route: String) {
    // Unified Profile Hub (Applications, Inbox, Saved Roles, Connections)
    object ProfileHub : Screen("profile_hub")

    // Startup Recruitment
    object Recruitment : Screen("startup_recruitment")
    object RecruitmentDetail : Screen("opening_detail/{openingId}") {
        fun createRoute(openingId: String): String = "opening_detail/$openingId"
    }
    object ApplicationsAndFounder : Screen("applications_and_founder")

    // Co-founder & Team Matching
    object Cofounders : Screen("cofounder_matching")
    object CofounderDetail : Screen("cofounder_detail/{profileId}") {
        fun createRoute(profileId: String): String = "cofounder_detail/$profileId"
    }
    object EditMatchProfile : Screen("edit_match_profile")
    object CofounderConnections : Screen("cofounder_connections")

    // Campus Opportunities Feed (Hackathons, Grants, Competitions)
    object Feed : Screen("opportunities_feed")
    object Detail : Screen("opportunity_detail/{opportunityId}") {
        fun createRoute(opportunityId: String): String = "opportunity_detail/$opportunityId"
    }
    object Saved : Screen("saved_opportunities")
}
