package es.uc3m.duodating.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    // Auth Flow
    object Welcome : Screen("welcome")
    object CreateAccount : Screen("create_account")
    object Login : Screen("login")

    // Onboarding Flow
    object BuildSelfProfile : Screen("build_self_profile")
    object ChoosePrompt : Screen("choose_prompt")
    object FindDuoPartner : Screen("find_duo_partner")
    object CreateDuoProfile : Screen("create_duo_profile")

    // Main App Flow
    object Discover : Screen("discover")
    object Matches : Screen("matches")
    object Chats : Screen("chats")
    object ViewProfile : Screen("view_profile")
}

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Discover : BottomBarScreen(
        route = Screen.Discover.route,
        title = "Discover",
        icon = Icons.Default.Search
    )

    object Matches : BottomBarScreen(
        route = Screen.Matches.route,
        title = "Matches",
        icon = Icons.Default.Favorite
    )

    object Chats : BottomBarScreen(
        route = Screen.Chats.route,
        title = "Chats",
        icon = Icons.AutoMirrored.Filled.Chat
    )

    object Profile : BottomBarScreen(
        route = Screen.ViewProfile.route,
        title = "Profile",
        icon = Icons.Default.Person
    )
}
