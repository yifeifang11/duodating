package es.uc3m.duodating.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.uc3m.duodating.ui.screens.*
import es.uc3m.duodating.ui.viewmodels.DuoViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    duoViewModel: DuoViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val user = duoViewModel.currentUser

    // List of screens that should show the bottom bar
    val bottomBarScreens = listOf(
        BottomBarScreen.Discover,
        BottomBarScreen.Matches,
        BottomBarScreen.Chats,
        BottomBarScreen.Profile
    )

    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    // Reactive Navigation Observer
    LaunchedEffect(user?.status) {
        when (user?.status) {
            "WAITING" -> {
                if (currentDestination?.route != "waiting") {
                    navController.navigate("waiting") {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
            "RECEIVED" -> {
                if (currentDestination?.route != "accept_decline") {
                    navController.navigate("accept_decline") {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
            "DUO_ONBOARDING" -> {
                if (currentDestination?.route != Screen.CreateDuoProfile.route) {
                    navController.navigate(Screen.CreateDuoProfile.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            "LINKED" -> {
                if (currentDestination?.route != Screen.Discover.route && 
                    currentDestination?.route != Screen.Matches.route &&
                    currentDestination?.route != Screen.Chats.route &&
                    currentDestination?.route != Screen.ViewProfile.route &&
                    currentDestination?.route != Screen.Conversation.route) {
                    navController.navigate(Screen.Discover.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            "READY_TO_LINK" -> {
                // If they were waiting/receiving and it got cancelled/declined
                if (currentDestination?.route == "waiting" || currentDestination?.route == "accept_decline") {
                    navController.navigate(Screen.FindDuoPartner.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            }
        }
    }

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
            // --- Auth Flow ---
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onCreateAccountClick = { navController.navigate(Screen.CreateAccount.route) },
                    onLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }
            
            composable(Screen.CreateAccount.route) {
                CreateAccountScreen(
                    onAccountCreated = {
                        navController.navigate(Screen.BuildSelfProfile.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { status ->
                        when (status) {
                            "ONBOARDING" -> navController.navigate(Screen.BuildSelfProfile.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                            "READY_TO_LINK" -> navController.navigate(Screen.FindDuoPartner.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                            "WAITING" -> navController.navigate("waiting") {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                            "RECEIVED" -> navController.navigate("accept_decline") {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                            "DUO_ONBOARDING" -> navController.navigate(Screen.CreateDuoProfile.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                            "LINKED" -> navController.navigate(Screen.Discover.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                            else -> navController.navigate(Screen.Discover.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // --- Onboarding Flow (User Profile) ---
            composable(Screen.BuildSelfProfile.route) {
                BuildSelfProfileScreen(
                    onNext = { 
                        navController.navigate(Screen.FindDuoPartner.route)
                    }
                )
            }

            // --- Linking Flow ---
            composable(Screen.FindDuoPartner.route) {
                FindPartnerScreen(
                    viewModel = duoViewModel,
                    onInviteSent = { /* Handled reactively */ }
                )
            }
            
            composable("waiting") {
                WaitingScreen(viewModel = duoViewModel)
            }
            
            composable("accept_decline") {
                AcceptDeclineScreen(
                    viewModel = duoViewModel,
                    onAccepted = {
                        // Handled reactively
                    }
                )
            }

            composable(Screen.CreateDuoProfile.route) {
                CreateDuoProfileScreen(
                    onFinish = {
                        navController.navigate(Screen.Discover.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // --- Main App Flow ---
            composable(Screen.Discover.route) { DiscoverScreen() }
            composable(Screen.Matches.route) { 
                MatchesScreen(
                    onProfileClick = {
                        navController.navigate(Screen.Discover.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) 
            }
            composable(Screen.Chats.route) { 
                ChatsScreen(
                    onChatClick = { navController.navigate(Screen.Conversation.route) }
                ) 
            }
            composable(Screen.Conversation.route) {
                ConversationScreen(onBackClick = { navController.popBackStack() })
            }
            composable(Screen.ViewProfile.route) { 
                ProfileTabScreen(
                    onLogout = {
                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) 
            }
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
