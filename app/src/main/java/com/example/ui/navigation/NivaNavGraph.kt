package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.NivaBottomNavigation
import com.example.ui.components.NivaTab
import com.example.ui.screens.*
import com.example.ui.viewmodel.NivaViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object MyDay : Screen("my_day")
    object HandleIt : Screen("handle_it")
    object Family : Screen("family")
    object Me : Screen("me")
    object EmptyMyHead : Screen("empty_my_head")
    object GuestsComing : Screen("guests_coming")
    object WhatToCook : Screen("what_to_cook")
    object PlanMoney : Screen("plan_money")
    object HelpNow : Screen("help_now")
    object Occasions : Screen("occasions")
    object SavedPlans : Screen("saved_plans")
    object Memory : Screen("memory")
}

@Composable
fun NivaApp(
    viewModel: NivaViewModel,
    navController: NavHostController = rememberNavController()
) {
    val hasCompletedOnboarding by viewModel.hasCompletedOnboarding.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Screen.Home.route -> NivaTab.HOME
        Screen.MyDay.route -> NivaTab.MY_DAY
        Screen.HandleIt.route -> NivaTab.HANDLE_IT
        Screen.Family.route -> NivaTab.FAMILY
        Screen.Me.route -> NivaTab.ME
        else -> null
    }

    val showBottomBar = currentTab != null

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (showBottomBar && currentTab != null) {
                NivaBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (hasCompletedOnboarding) Screen.Home.route else Screen.Onboarding.route,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onGetStarted = {
                        viewModel.completeOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToHandleIt = { navController.navigate(Screen.HandleIt.route) },
                    onNavigateToEmptyMyHead = { navController.navigate(Screen.EmptyMyHead.route) },
                    onNavigateToGuestsComing = { navController.navigate(Screen.GuestsComing.route) },
                    onNavigateToWhatToCook = { navController.navigate(Screen.WhatToCook.route) },
                    onNavigateToPlanMoney = { navController.navigate(Screen.PlanMoney.route) },
                    onNavigateToHelpNow = { navController.navigate(Screen.HelpNow.route) },
                    onNavigateToOccasions = { navController.navigate(Screen.Occasions.route) },
                    onNavigateToMyDay = { navController.navigate(Screen.MyDay.route) },
                    onNavigateToSavedPlans = { navController.navigate(Screen.SavedPlans.route) }
                )
            }

            composable(Screen.MyDay.route) {
                MyDayScreen(viewModel = viewModel)
            }

            composable(Screen.HandleIt.route) {
                HandleItScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Family.route) {
                FamilyScreen(viewModel = viewModel)
            }

            composable(Screen.Me.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToMemory = { navController.navigate(Screen.Memory.route) },
                    onNavigateToFamily = { navController.navigate(Screen.Family.route) },
                    onNavigateToSavedPlans = { navController.navigate(Screen.SavedPlans.route) },
                    onNavigateToBudget = { navController.navigate(Screen.PlanMoney.route) }
                )
            }

            composable(Screen.EmptyMyHead.route) {
                EmptyMyHeadScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPlanGenerated = { navController.navigate(Screen.HandleIt.route) }
                )
            }

            composable(Screen.GuestsComing.route) {
                GuestsComingScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPlanGenerated = { navController.navigate(Screen.HandleIt.route) }
                )
            }

            composable(Screen.WhatToCook.route) {
                WhatToCookScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPlanGenerated = { navController.navigate(Screen.HandleIt.route) }
                )
            }

            composable(Screen.PlanMoney.route) {
                PlanMoneyScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.HelpNow.route) {
                HelpNowScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPlanGenerated = { navController.navigate(Screen.HandleIt.route) }
                )
            }

            composable(Screen.Occasions.route) {
                OccasionsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onPlanGenerated = { navController.navigate(Screen.HandleIt.route) }
                )
            }

            composable(Screen.SavedPlans.route) {
                SavedPlansScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onOpenPlan = { navController.navigate(Screen.HandleIt.route) }
                )
            }

            composable(Screen.Memory.route) {
                MemoryScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
