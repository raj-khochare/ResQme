package com.mnp.resqme.ui.navigation

sealed class Screen (val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Guides : Screen("guides")
    object Emergency : Screen("emergency")
    object Profile : Screen("profile")
    object News : Screen("news")
    object EmergencyContacts : Screen("emergency_contacts")
    object GuideDetail : Screen("guide_detail/{guideId}") {
        fun createRoute(guideId: String) = "guide_detail/$guideId"
    }
}