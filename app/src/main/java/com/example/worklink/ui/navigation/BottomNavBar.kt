package com.example.worklink.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val destination: Destinations
)

@Composable
fun BottomNavBar(
    navController: NavController,
    isAdmin: Boolean = false,
    unreadCount: Long = 0L
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = if (isAdmin) {
        mutableListOf(
            BottomNavItem("Calendario", Icons.Default.DateRange, Destinations.Calendar),
            BottomNavItem("Admin", Icons.Default.AdminPanelSettings, Destinations.Admin),
            BottomNavItem("Cuenta", Icons.Default.Person, Destinations.Account)
        )
    } else {
        mutableListOf(
            BottomNavItem("Calendario", Icons.Default.DateRange, Destinations.Calendar),
            BottomNavItem("Solicitudes", Icons.Default.List, Destinations.Requests),
            BottomNavItem("Balance", Icons.Default.Star, Destinations.Balance),
            BottomNavItem("Cuenta", Icons.Default.Person, Destinations.Account)
        )
    }

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.destination.route,
                onClick = {
                    navController.navigate(item.destination.route) {
                        popUpTo(Destinations.Calendar.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }

        NavigationBarItem(
            selected = currentRoute == Destinations.Notifications.route,
            onClick = {
                navController.navigate(Destinations.Notifications.route) {
                    popUpTo(Destinations.Calendar.route) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadCount > 0) {
                            Badge {
                                Text(
                                    text = if (unreadCount > 99) "99+" else unreadCount.toString()
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        if (unreadCount > 0) Icons.Default.Notifications
                        else Icons.Default.NotificationsNone,
                        contentDescription = "Notificaciones"
                    )
                }
            },
            label = { Text("Avisos") }
        )
    }
}