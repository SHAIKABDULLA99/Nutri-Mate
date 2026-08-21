package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.viewmodel.NutriMateViewModel
import com.example.util.AppLanguage

sealed class Screen(
    val route: String,
    val titleEn: String,
    val titleTe: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Dashboard", "హోమ్", Icons.Filled.Home, Icons.Outlined.Home)
    object Scan : Screen("scan", "AI Scan", "AI స్కాన్", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt)
    object Search : Screen("search", "Food Search", "ఆహారాలు", Icons.Filled.Search, Icons.Outlined.Search)
    object Grocery : Screen("grocery", "Grocery", "కిరాణా", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)
    object Challenges : Screen("challenges", "Tribes", "ఛాలెంజ్", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
    object Analytics : Screen("analytics", "Reports", "రిపోర్ట్స్", Icons.Filled.Analytics, Icons.Outlined.Analytics)
    object Profile : Screen("profile", "Profile", "ప్రొఫైల్", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Scan,
    Screen.Search,
    Screen.Grocery,
    Screen.Challenges,
    Screen.Analytics
)

@Composable
fun NutriMateApp(
    viewModel: NutriMateViewModel,
    navController: NavHostController = rememberNavController()
) {
    val lang by viewModel.currentLanguage.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (bottomNavItems.any { it.route == currentRoute }) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val label = if (lang == AppLanguage.TELUGU) item.titleTe else item.titleEn

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToScan = { navController.navigate(Screen.Scan.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToGrocery = { navController.navigate(Screen.Grocery.route) },
                    onNavigateToChallenges = { navController.navigate(Screen.Challenges.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            composable(Screen.Scan.route) {
                ScanFoodScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToGrocery = { navController.navigate(Screen.Grocery.route) }
                )
            }

            composable(Screen.Search.route) {
                FoodSearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onScanClick = { navController.navigate(Screen.Scan.route) }
                )
            }

            composable(Screen.Grocery.route) {
                GroceryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Challenges.route) {
                CommunityChallengesScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
