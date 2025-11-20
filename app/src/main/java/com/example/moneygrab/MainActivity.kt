package com.example.moneygrab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyGrabTheme {
                    NavManager()
            }
        }
    }
    
}

@Composable
fun NavManager() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }

    val startDestination = if (currentUser.getUser() != null) "groupPage" else "login"

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
                composable("signup") {
                    SignUpScreen(onSignUpClicked = {
                        navController.navigate("ProfilePage")
                    })
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
                        onBack = { navController.navigateUp() },
                        onConfirmation = { group ->
                            navController.navigate("confirmPayment/${group.id}")
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
                        back = { navController.popBackStack() }
                    )
                }

                composable("ProfilePage") {
                    ProfilePage(
                        onBackClick = { navController.popBackStack() },
                        onLogoutClick = { navController.navigate("login") }
                        //onEditClick = { },
                    )
                }

                composable("groupCreation") {
                    GroupCreationView(
                        onCreateGroupNavigation = { navController.navigate("groupPage") },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(
                    "confirmPayment/{groupId}", arguments = listOf(
                    navArgument("groupId") { type = NavType.IntType }
                )) { backStackEntry ->
                    val groupId = backStackEntry.arguments?.getInt("groupId") ?: 1
                    ConfirmPaymentPage(
                        groupId = groupId,
                        onBack = { navController.popBackStack() },
                        navigation = { group ->
                            navController.navigate("groupChat/${group.id}")
                        })
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

