package com.example.moneygrab

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moneygrab.components.NotificationHelper
import com.example.authentication.CurrentUser
import com.example.moneygrab.views.ChatScreen
import com.example.moneygrab.views.LoginScreen
import com.example.moneygrab.views.SignUpScreen

import com.example.moneygrab.ui.theme.MoneyGrabTheme
import com.example.moneygrab.views.AddExpenseView
import com.example.moneygrab.views.ConfirmPaymentPage
import com.example.moneygrab.views.GroupCreationView
import com.example.moneygrab.views.GroupDetailsView
import com.example.moneygrab.views.GroupDetailsViewModel
import com.example.moneygrab.views.GroupPage
import com.example.moneygrab.views.ProfilePage




class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                showPaymentNotification()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Permission not granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        enableEdgeToEdge()
        setContent {
            MoneyGrabTheme {
                NavManager()
            }
        }
    }

    fun notifyUsers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        showPaymentNotification()
    }

    private fun showPaymentNotification() {
        NotificationHelper.sendNotification(
            this,
            "Time to pay!",
            "The tab has been closed in your group and requests everyone to pay their debts"
        )
    }
}

@Composable
fun NavManager() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }
    val activity = context as? MainActivity

    val startDestination = if (currentUser.getUser() != null) "groupPage" else "login"

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
                composable("signup") {
                    SignUpScreen(
                        onSignUpClicked = { navController.navigate("ProfilePage") },
                        onBackLogin = { navController.navigate("login")}
                    )

                }

                composable("login") {
                    LoginScreen(
                        onLoginClicked = { navController.navigate("groupPage") },
                        onSignupClicked = { navController.navigate("signUp") }
                    )
                }

                composable("groupPage") {
                    GroupPage(
                        onProfileClicked = { navController.navigate("ProfilePage") },
                        onCreateGroupClicked = { navController.navigate("groupCreation") },
                        onGroupClicked = { group ->
                            navController.navigate("groupChat/${group.id}")
                        }
                    )
                }

                composable(
                    "groupChat/{groupId}", arguments = listOf(
                    navArgument("groupId") { type = NavType.IntType }
                )) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1

                    ChatScreen(
                        groupId = groupId,
                        addExpense = { group -> navController.navigate("addExpense/${group.id}") },
                        onBack = { navController.navigate("groupPage") },
                        onConfirmation = { group ->
                            navController.navigate("confirmPayment/${group.id}")
                        },
                        onNotifyUsers = {
                            activity?.notifyUsers()
                        },
                        onName = { group -> navController.navigate("groupDetails/${group.id}")}
                    )
                }

                composable(
                    "addExpense/{groupId}", arguments = listOf(
                    navArgument("groupId") { type = NavType.IntType }
                )) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
                    AddExpenseView(
                        groupId = groupId,
                        onCreateExpense = { group -> navController.navigate("groupChat/${group?.id}") },
                        back = { navController.navigate("groupChat/$groupId") }
                    )
                }

                composable("ProfilePage") {
                    ProfilePage(
                        onBackClick = { navController.navigate("groupPage") },
                        onLogoutClick = { navController.navigate("login") }
                    )
                }

                composable("groupCreation") {
                    GroupCreationView(
                        onCreateGroupNavigation = { navController.navigate("groupPage") },
                        onBack = { navController.navigate("groupPage") }
                    )
                }

                composable(
                    "confirmPayment/{groupId}", arguments = listOf(
                    navArgument("groupId") { type = NavType.IntType }
                )) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
                    ConfirmPaymentPage(
                        groupId = groupId,
                        navigation = { group -> navController.navigate("groupChat/${group.id}")}
                    )
                }

                composable("groupDetails/{groupId}", arguments = listOf(
                    navArgument("groupId") { type = NavType.IntType }
                )) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
                    GroupDetailsView(
                        groupId = groupId,
                        onBack = {navController.popBackStack() },
                    )
                }

            }
        }
}

