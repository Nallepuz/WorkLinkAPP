package com.example.worklink.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.worklink.ui.account.AccountScreen
import com.example.worklink.ui.admin.AdminScreen
import com.example.worklink.ui.admin.TurnManagementScreen
import com.example.worklink.ui.balance.BalanceScreen
import com.example.worklink.ui.calendar.CalendarScreen
import com.example.worklink.ui.login.LoginScreen
import com.example.worklink.ui.notifications.NotificationsScreen
import com.example.worklink.ui.notifications.NotificationsViewModel
import com.example.worklink.ui.requests.RequestsScreen
import com.example.worklink.ui.splash.SplashScreen
import com.example.worklink.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    var isAdmin by remember { mutableStateOf(false) }
    var sessionChecked by remember { mutableStateOf(false) }
    var hasSession by remember { mutableStateOf(false) }

    val notificationsViewModel: NotificationsViewModel = viewModel()
    val notificationsUiState by notificationsViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        sessionManager.token.collect { token ->
            hasSession = !token.isNullOrEmpty()
        }
    }

    LaunchedEffect(Unit) {
        sessionManager.roleId.collect { roleId ->
            isAdmin = roleId == 1L
            sessionChecked = true
        }
    }

    LaunchedEffect(currentRoute) {
        if (currentRoute != null &&
            currentRoute != Destinations.Login.route &&
            currentRoute != Destinations.Splash.route
        ) {
            notificationsViewModel.loadNotifications()
        }
    }

    val showBottomBar = currentRoute != Destinations.Login.route &&
            currentRoute != Destinations.Splash.route &&
            currentRoute != Destinations.TurnManagement.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    isAdmin = isAdmin,
                    unreadCount = notificationsUiState.unreadCount
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                        fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                        fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(
                route = Destinations.Splash.route,
                enterTransition = { fadeIn(animationSpec = tween(500)) },
                exitTransition = { fadeOut(animationSpec = tween(500)) }
            ) {
                SplashScreen(
                    onSplashFinished = {
                        scope.launch {
                            val token = sessionManager.token.first()
                            val destination = if (!token.isNullOrEmpty())
                                Destinations.Calendar.route
                            else
                                Destinations.Login.route
                            navController.navigate(destination) {
                                popUpTo(Destinations.Splash.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(
                route = Destinations.Login.route,
                enterTransition = { fadeIn(animationSpec = tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) }
            ) {
                LoginScreen(
                    onLoginSuccess = {
                        scope.launch {
                            val roleId = sessionManager.roleId.first()
                            isAdmin = roleId == 1L
                            notificationsViewModel.loadNotifications()
                        }
                        navController.navigate(Destinations.Calendar.route) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Destinations.Calendar.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                CalendarScreen()
            }

            composable(
                route = Destinations.Requests.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                RequestsScreen()
            }

            composable(
                route = Destinations.Balance.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                BalanceScreen()
            }

            composable(
                route = Destinations.Account.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                AccountScreen(
                    onLogout = {
                        scope.launch {
                            sessionManager.clearSession()
                            isAdmin = false
                            navController.navigate(Destinations.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            composable(
                route = Destinations.Admin.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                AdminScreen(
                    onNavigateToTurnManagement = {
                        navController.navigate(Destinations.TurnManagement.route)
                    }
                )
            }

            composable(
                route = Destinations.Notifications.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                NotificationsScreen(viewModel = notificationsViewModel)
            }

            composable(
                route = Destinations.TurnManagement.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                },
                popEnterTransition = {
                    slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300)) +
                            fadeIn(animationSpec = tween(300))
                },
                popExitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300)) +
                            fadeOut(animationSpec = tween(300))
                }
            ) {
                TurnManagementScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}