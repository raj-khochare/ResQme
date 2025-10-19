package com.mnp.resqme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mnp.resqme.ui.screens.EmergencyContactsScreen
import com.mnp.resqme.ui.screens.EmergencyScreen
import com.mnp.resqme.ui.screens.GuideDetailScreen
import com.mnp.resqme.ui.screens.GuidesScreen
import com.mnp.resqme.ui.screens.LoginScreen
import com.mnp.resqme.ui.screens.NewsScreen
import com.mnp.resqme.ui.screens.ProfileScreen
import com.mnp.resqme.ui.screens.RegisterScreen
import com.mnp.resqme.ui.screens.SplashScreen
import com.mnp.resqme.ui.screens.HomeScreen
import com.mnp.resqme.viewmodel.ThemeViewModel

@Composable
fun RescueNavigation(navController: NavHostController) {
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Guides.route) {
            GuidesScreen(navController = navController)
        }

        composable(Screen.Emergency.route) {
            EmergencyScreen(navController = navController)
        }

        composable(Screen.News.route) {
            NewsScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                    navController = navController,
            isDarkMode = isDarkMode,
                onThemeToggle = { themeViewModel.toggleTheme() }
            )
        }

        composable(Screen.GuideDetail.route) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
            GuideDetailScreen(navController = navController, guideId = guideId)
        }
        composable(Screen.EmergencyContacts.route) {
            EmergencyContactsScreen(navController = navController)
        }
    }
}