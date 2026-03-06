package com.example.duodating.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.duodating.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // List of screens that should show the bottom bar
    val bottomBarScreens = listOf(
        BottomBarScreen.Discover,
        BottomBarScreen.Matches,
        BottomBarScreen.Chats,
        BottomBarScreen.Profile
    )

    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController, screens = bottomBarScreens)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Flow
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onCreateAccountClick = { navController.navigate(Screen.CreateAccount.route) },
                    onLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.CreateAccount.route) {
                CreateAccountScreen(
                    onAccountCreated = { navController.navigate(Screen.BuildSelfProfile.route) }
                )
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Screen.Discover.route) }
                )
            }

            // Onboarding Flow
            composable(Screen.BuildSelfProfile.route) {
                BuildSelfProfileScreen(
                    onNext = { navController.navigate(Screen.ChoosePrompt.route) }
                )
            }
            composable(Screen.ChoosePrompt.route) {
                ChoosePromptScreen(
                    onNext = { navController.navigate(Screen.FindDuoPartner.route) }
                )
            }
            composable(Screen.FindDuoPartner.route) {
                FindDuoPartnerScreen(
                    onNext = { navController.navigate(Screen.CreateDuoProfile.route) }
                )
            }
            composable(Screen.CreateDuoProfile.route) {
                CreateDuoProfileScreen(
                    onFinish = {
                        navController.navigate(Screen.Discover.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }

            // Main App Flow
            composable(Screen.Discover.route) { DiscoverScreen() }
            composable(Screen.Matches.route) { MatchesScreen() }
            composable(Screen.Chats.route) { ChatsScreen() }
            composable(Screen.ViewProfile.route) { ViewProfileScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    screens: List<BottomBarScreen>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                label = { Text(text = screen.title) },
                icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
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
}
