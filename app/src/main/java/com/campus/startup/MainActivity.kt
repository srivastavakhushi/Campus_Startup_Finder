package com.campus.startup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campus.startup.core.database.AppDatabase
import com.campus.startup.core.navigation.Screen
import com.campus.startup.core.theme.CampusStartupFinderTheme
import com.campus.startup.features.opportunities.data.repository.OpportunityRepository
import com.campus.startup.features.opportunities.presentation.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room Database & Repository
        val database = AppDatabase.getInstance(applicationContext)
        val repository = OpportunityRepository(database.opportunityDao())
        val viewModelFactory = OpportunitiesViewModelFactory(repository)

        setContent {
            val viewModel: OpportunitiesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = viewModelFactory
            )
            val navController = rememberNavController()

            CampusStartupFinderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Feed.route
                    ) {
                        // 1. Opportunities Feed Screen
                        composable(Screen.Feed.route) {
                            OpportunitiesFeedScreen(
                                viewModel = viewModel,
                                onOpportunityClick = { oppId ->
                                    navController.navigate(Screen.Detail.createRoute(oppId))
                                },
                                onNavigateToSaved = {
                                    navController.navigate(Screen.Saved.route)
                                }
                            )
                        }

                        // 2. Opportunity Detail Screen
                        composable(
                            route = Screen.Detail.route,
                            arguments = listOf(
                                navArgument("opportunityId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val opportunityId = backStackEntry.arguments?.getString("opportunityId") ?: ""
                            OpportunityDetailScreen(
                                opportunityId = opportunityId,
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 3. Saved / Bookmarked Screen (Room Database)
                        composable(Screen.Saved.route) {
                            SavedOpportunitiesScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onOpportunityClick = { oppId ->
                                    navController.navigate(Screen.Detail.createRoute(oppId))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
