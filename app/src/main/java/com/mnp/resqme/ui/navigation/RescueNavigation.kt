package com.mnp.resqme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mnp.resqme.ui.screens.*

@Composable
fun RescueNavigation(navController: NavHostController) {
    val navController = rememberNavController()
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

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.GuideDetail.route) { backStackEntry ->
            val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
            GuideDetailScreen(navController = navController, guideId = guideId)
        }
    }
}