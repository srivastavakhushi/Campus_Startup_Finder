package com.campus.startup.core.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("opportunities_feed")
    object Detail : Screen("opportunity_detail/{opportunityId}") {
        fun createRoute(opportunityId: String): String = "opportunity_detail/$opportunityId"
    }
    object Saved : Screen("saved_opportunities")
}
