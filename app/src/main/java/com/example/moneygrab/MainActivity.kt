package com.example.moneygrab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneygrab.views.ChatScreen
import com.example.moneygrab.views.LoginScreen
import com.example.moneygrab.views.SignUpScreen
import com.example.moneygrab.views.FrontendGroup
import com.example.moneygrab.views.LoginScreen

import com.example.moneygrab.ui.theme.MoneyGrabTheme
import com.example.moneygrab.views.AddPayersView
import com.example.moneygrab.views.ConfirmPaymentPage
import com.example.moneygrab.views.CredentialMethod
import com.example.moneygrab.views.testData
import com.example.moneygrab.views.GroupCreationView
import com.example.moneygrab.views.GroupPage
import com.example.moneygrab.views.ProfilePage
import com.example.moneygrab.views.SignUpScreen
import java.sql.SQLOutput


class MainActivity : ComponentActivity() {
    private lateinit var apiInterface: APIEndpoints

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
    val group = TestData()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signup") {
        composable("signup") {
            SignUpScreen(onSignUpSuccess = {navController.navigate("pPage")
            })
        }

        composable("login") {
            LoginScreen(
                onLoginClicked = { navController.navigate("groupPage")},
                onSignupClicked = { navController.navigate("signUp")}
            )
        }

        composable("signUp") {
            SignUpScreen { name, email, phone, password ->
                navController.navigate("groupPage")
            }
        }

        composable("groupPage") {
            GroupPage(
                groups = listOf(
                    FrontendGroup(1, "Årsfest")
                ),
                onProfileClicked = { navController.navigate("ProfilePage") },
                onCreateGroupClicked = { navController.navigate("groupCreation") },
                onGroupClicked = { navController.navigate("groupChat") }
            )
        }

        composable("groupChat") {
            ChatScreen(
                group = testData().copy(name = "Chat"),
                addExpense = { navController.navigate("addToExpense") },
                onBack = { navController.navigateUp() },
                onPayDebt = {navController.navigate("confirmPayment")}
            )
        }

        composable("ProfilePage") {
            ProfilePage(
                credentialMethod = CredentialMethod(
                    fullName = "Magnus Hende jdj",
                    phoneNumber = "43434343"
                ),
                onBackClick = { navController.popBackStack() },
                onLogoutClick = { navController.navigate("login") }
                //onEditClick = { },
            )
        }

        composable("addToExpense") {
            AddPayersView(
                group = group,
                onAddExpense = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }


        composable("groupCreation") {
            GroupCreationView(
                onCreateGroupNavigation = { navController.navigate("groupPage") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("confirmPayment"){
            ConfirmPaymentPage(groupName = group.name, debt = 100,
                onBack = { navController.popBackStack()})
        }

    }
}

