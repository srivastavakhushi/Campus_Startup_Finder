package com.campus.startup.features.startupprofiles.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object StartupRoutes {
    const val LIST = "startup_list"
    const val DETAIL = "startup_detail/{startupId}"
    const val FORM = "startup_form?startupId={startupId}"

    fun detail(id: String) = "startup_detail/$id"
    fun form(id: String? = null) = if (id == null) "startup_form" else "startup_form?startupId=$id"
}

/** One call from MainActivity's NavHost registers every Startup Profiles screen. */
fun NavGraphBuilder.startupProfilesGraph(
    navController: NavHostController,
    viewModel: StartupProfilesViewModel
) {
    composable(StartupRoutes.LIST) {
        StartupListScreen(
            viewModel = viewModel,
            onStartupClick = { navController.navigate(StartupRoutes.detail(it)) },
            onAddClick = { navController.navigate(StartupRoutes.form()) }
        )
    }

    composable(
        route = StartupRoutes.DETAIL,
        arguments = listOf(navArgument("startupId") { type = NavType.StringType })
    ) { entry ->
        val id = entry.arguments?.getString("startupId") ?: ""
        StartupDetailScreen(
            startupId = id,
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() },
            onEdit = { navController.navigate(StartupRoutes.form(it)) }
        )
    }

    composable(
        route = StartupRoutes.FORM,
        arguments = listOf(navArgument("startupId") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) { entry ->
        StartupFormScreen(
            startupId = entry.arguments?.getString("startupId"),
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
