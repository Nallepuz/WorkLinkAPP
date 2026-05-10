package com.example.worklink.ui.navigation

sealed class Destinations(val route: String) {
    object Splash : Destinations("splash")
    object Login : Destinations("login")
    object Calendar : Destinations("calendar")
    object Requests : Destinations("requests")
    object Balance : Destinations("balance")
    object Account : Destinations("account")
    object Admin : Destinations("admin")
    object Notifications : Destinations("notifications")
    object TurnManagement : Destinations("turn_management")
}