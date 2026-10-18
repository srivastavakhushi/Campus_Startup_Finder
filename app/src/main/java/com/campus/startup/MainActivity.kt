package com.campus.startup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campus.startup.core.database.AppDatabase
import com.campus.startup.core.navigation.Screen
import com.campus.startup.core.theme.CampusStartupFinderTheme
import com.campus.startup.features.matching.data.repository.MatchingRepository
import com.campus.startup.features.matching.presentation.CofounderDetailScreen
import com.campus.startup.features.matching.presentation.CofounderMatchingScreen
import com.campus.startup.features.matching.presentation.ConnectionsScreen
import com.campus.startup.features.matching.presentation.EditMatchProfileScreen
import com.campus.startup.features.matching.presentation.MatchingViewModel
import com.campus.startup.features.matching.presentation.MatchingViewModelFactory
import com.campus.startup.features.opportunities.data.repository.OpportunityRepository
import com.campus.startup.features.opportunities.presentation.*
import com.campus.startup.features.recruitment.data.repository.RecruitmentRepository
import com.campus.startup.features.recruitment.presentation.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room Database & Repositories
        val database = AppDatabase.getInstance(applicationContext)
        val opportunityRepository = OpportunityRepository(database.opportunityDao())
        val recruitmentRepository = RecruitmentRepository(database.recruitmentDao())
        val matchingRepository = MatchingRepository(database.matchingDao())

        val oppViewModelFactory = OpportunitiesViewModelFactory(opportunityRepository)
        val recViewModelFactory = RecruitmentViewModelFactory(recruitmentRepository)
        val matchingViewModelFactory = MatchingViewModelFactory(matchingRepository)

        setContent {
            val oppViewModel: OpportunitiesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = oppViewModelFactory
            )
            val recViewModel: RecruitmentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = recViewModelFactory
            )
            val matchingViewModel: MatchingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = matchingViewModelFactory
            )

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomNavItems = listOf(
                BottomNavItem(
                    title = "Recruitment",
                    route = Screen.Recruitment.route,
                    selectedIcon = Icons.Filled.RocketLaunch,
                    unselectedIcon = Icons.Outlined.RocketLaunch
                ),
                BottomNavItem(
                    title = "Hackathons",
                    route = Screen.Feed.route,
                    selectedIcon = Icons.Filled.School,
                    unselectedIcon = Icons.Outlined.School
                ),
                BottomNavItem(
                    title = "Co-founders",
                    route = Screen.Cofounders.route,
                    selectedIcon = Icons.Filled.Groups,
                    unselectedIcon = Icons.Outlined.Groups
                ),
                BottomNavItem(
                    title = "Applications",
                    route = Screen.ApplicationsAndFounder.route,
                    selectedIcon = Icons.Filled.Assignment,
                    unselectedIcon = Icons.Outlined.Assignment
                )
            )

            val showBottomBar = currentRoute in listOf(
                Screen.Recruitment.route,
                Screen.Feed.route,
                Screen.Cofounders.route,
                Screen.ApplicationsAndFounder.route
            )

            CampusStartupFinderTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = NavigationBarDefaults.Elevation
                            ) {
                                bottomNavItems.forEach { item ->
                                    val isSelected = currentRoute == item.route
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.title,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Recruitment.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Startup Recruitment Screen (Primary Landing)
                        composable(Screen.Recruitment.route) {
                            StartupRecruitmentScreen(
                                viewModel = recViewModel,
                                onOpeningClick = { openingId ->
                                    navController.navigate(Screen.RecruitmentDetail.createRoute(openingId))
                                },
                                onNavigateToApplications = {
                                    navController.navigate(Screen.ApplicationsAndFounder.route)
                                },
                                onNavigateToSaved = {
                                    navController.navigate(Screen.ApplicationsAndFounder.route)
                                }
                            )
                        }

                        // 2. Startup Opening Detail Screen
                        composable(
                            route = Screen.RecruitmentDetail.route,
                            arguments = listOf(
                                navArgument("openingId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val openingId = backStackEntry.arguments?.getString("openingId") ?: ""
                            OpeningDetailScreen(
                                openingId = openingId,
                                viewModel = recViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 3. Applications, Saved Roles & Founder Hub Screen
                        composable(Screen.ApplicationsAndFounder.route) {
                            ApplicationsAndFounderScreen(
                                viewModel = recViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onOpeningClick = { openingId ->
                                    navController.navigate(Screen.RecruitmentDetail.createRoute(openingId))
                                }
                            )
                        }

                        // 4. Co-founder & Team Matching
                        composable(Screen.Cofounders.route) {
                            CofounderMatchingScreen(
                                viewModel = matchingViewModel,
                                onStudentClick = { profileId ->
                                    navController.navigate(Screen.CofounderDetail.createRoute(profileId))
                                },
                                onEditProfile = {
                                    navController.navigate(Screen.EditMatchProfile.route)
                                },
                                onOpenConnections = {
                                    navController.navigate(Screen.CofounderConnections.route)
                                }
                            )
                        }

                        composable(
                            route = Screen.CofounderDetail.route,
                            arguments = listOf(
                                navArgument("profileId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
                            CofounderDetailScreen(
                                profileId = profileId,
                                viewModel = matchingViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.EditMatchProfile.route) {
                            EditMatchProfileScreen(
                                viewModel = matchingViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onSaved = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.CofounderConnections.route) {
                            ConnectionsScreen(
                                viewModel = matchingViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onStudentClick = { profileId ->
                                    navController.navigate(Screen.CofounderDetail.createRoute(profileId))
                                }
                            )
                        }

                        // 5. Opportunities Feed (Hackathons, Grants & Competitions)
                        composable(Screen.Feed.route) {
                            OpportunitiesFeedScreen(
                                viewModel = oppViewModel,
                                onOpportunityClick = { oppId ->
                                    navController.navigate(Screen.Detail.createRoute(oppId))
                                },
                                onNavigateToSaved = {
                                    navController.navigate(Screen.Saved.route)
                                }
                            )
                        }

                        // 6. Opportunity Detail Screen
                        composable(
                            route = Screen.Detail.route,
                            arguments = listOf(
                                navArgument("opportunityId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val opportunityId = backStackEntry.arguments?.getString("opportunityId") ?: ""
                            OpportunityDetailScreen(
                                opportunityId = opportunityId,
                                viewModel = oppViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 7. Saved Opportunities Screen
                        composable(Screen.Saved.route) {
                            SavedOpportunitiesScreen(
                                viewModel = oppViewModel,
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

data class BottomNavItem(
    val title: String,
    val route: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
