package com.campus.startup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.campus.startup.core.theme.PrimaryGreen
import com.campus.startup.core.theme.PrimaryGreenContainer
import com.campus.startup.features.auth.LoginScreen
import com.campus.startup.features.auth.UserSession
import com.campus.startup.features.matching.data.repository.MatchingRepository
import com.campus.startup.features.matching.presentation.CofounderDetailScreen
import com.campus.startup.features.matching.presentation.CofounderMatchingScreen
import com.campus.startup.features.matching.presentation.EditMatchProfileScreen
import com.campus.startup.features.matching.presentation.MatchingViewModel
import com.campus.startup.features.matching.presentation.MatchingViewModelFactory
import com.campus.startup.features.matching.presentation.ProfileHubScreen
import com.campus.startup.features.opportunities.data.repository.OpportunityRepository
import com.campus.startup.features.opportunities.presentation.*
import com.campus.startup.features.recruitment.data.repository.RecruitmentRepository
import com.campus.startup.features.recruitment.presentation.*
import com.campus.startup.features.startupprofiles.data.repository.StartupRepository
import com.campus.startup.features.startupprofiles.presentation.StartupProfilesViewModel
import com.campus.startup.features.startupprofiles.presentation.StartupProfilesViewModelFactory
import com.campus.startup.features.startupprofiles.presentation.StartupRoutes
import com.campus.startup.features.startupprofiles.presentation.startupProfilesGraph

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

        val startupRepository = StartupRepository(database.startupDao())
        val startupViewModelFactory = StartupProfilesViewModelFactory(startupRepository)

        setContent {
            val oppViewModel: OpportunitiesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = oppViewModelFactory
            )
            val recViewModel: RecruitmentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = recViewModelFactory
            )
            val startupViewModel: StartupProfilesViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = startupViewModelFactory
            )
            val matchingViewModel: MatchingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = matchingViewModelFactory
            )

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val bottomNavItems = listOf(
                BottomNavItem(
                    title = "Home",
                    route = StartupRoutes.LIST,
                    selectedIcon = Icons.Filled.Business,
                    unselectedIcon = Icons.Outlined.Business
                ),
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
                )
            )

            val showBottomBar = currentRoute in listOf(
                Screen.Recruitment.route,
                Screen.Feed.route,
                Screen.Cofounders.route,
                StartupRoutes.LIST
            )

            val navigateToProfile: () -> Unit = {
                if (navController.currentDestination?.route != Screen.ProfileHub.route) {
                    navController.navigate(Screen.ProfileHub.route) {
                        launchSingleTop = true
                    }
                }
            }

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
                                                    popUpTo(StartupRoutes.LIST) {
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
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = PrimaryGreen,
                                            selectedTextColor = PrimaryGreen,
                                            indicatorColor = PrimaryGreenContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 0. Login Screen (Student or Professor with predefined credentials)
                        composable(Screen.Login.route) {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate(StartupRoutes.LIST) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }

                        startupProfilesGraph(
                            navController = navController,
                            viewModel = startupViewModel,
                            matchingViewModel = matchingViewModel,
                            recruitmentViewModel = recViewModel,
                            onOpenProfile = navigateToProfile
                        )

                        // Unified Profile Section (Saved Startups/Opportunities/Roles, Connections, Inbox, Applications)
                        composable(Screen.ProfileHub.route) {
                            ProfileHubScreen(
                                matchingViewModel = matchingViewModel,
                                recruitmentViewModel = recViewModel,
                                startupViewModel = startupViewModel,
                                opportunitiesViewModel = oppViewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onEditProfile = { navController.navigate(Screen.EditMatchProfile.route) },
                                onOpeningClick = { openingId ->
                                    navController.navigate(Screen.RecruitmentDetail.createRoute(openingId))
                                },
                                onStartupClick = { startupId ->
                                    navController.navigate(StartupRoutes.detailRoute(startupId))
                                },
                                onOpportunityClick = { oppId ->
                                    navController.navigate(Screen.Detail.createRoute(oppId))
                                },
                                onStudentClick = { profileId ->
                                    navController.navigate(Screen.CofounderDetail.createRoute(profileId))
                                },
                                onLogout = {
                                    UserSession.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 1. Startup Recruitment Screen
                        composable(Screen.Recruitment.route) {
                            StartupRecruitmentScreen(
                                viewModel = recViewModel,
                                onOpeningClick = { openingId ->
                                    navController.navigate(Screen.RecruitmentDetail.createRoute(openingId))
                                },
                                onOpenProfile = navigateToProfile
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

                        // 3. Co-founder & Team Matching
                        composable(Screen.Cofounders.route) {
                            CofounderMatchingScreen(
                                viewModel = matchingViewModel,
                                onStudentClick = { profileId ->
                                    navController.navigate(Screen.CofounderDetail.createRoute(profileId))
                                },
                                onOpenProfile = navigateToProfile
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
                            val allStartups by startupViewModel.allStartups.collectAsState()
                            EditMatchProfileScreen(
                                viewModel = matchingViewModel,
                                availableStartups = allStartups,
                                onNavigateBack = { navController.popBackStack() },
                                onSaved = { navController.popBackStack() }
                            )
                        }

                        // 4. Opportunities Feed (Hackathons, Grants & Competitions)
                        composable(Screen.Feed.route) {
                            OpportunitiesFeedScreen(
                                viewModel = oppViewModel,
                                onOpportunityClick = { oppId ->
                                    navController.navigate(Screen.Detail.createRoute(oppId))
                                },
                                onOpenProfile = navigateToProfile
                            )
                        }

                        // 5. Opportunity Detail Screen
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
