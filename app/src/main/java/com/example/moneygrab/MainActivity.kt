package com.example.moneygrab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.ChatScreen
import com.example.debtcalculator.data.Group
import com.example.moneygrab.screens.HomeScreen
import com.example.moneygrab.screens.LoginScreen
import com.example.moneygrab.screens.SignUpScreen
import com.example.moneygrab.views.FrontendGroup
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import com.example.moneygrab.views.AddPayersView
import com.example.moneygrab.views.TestData
import com.example.moneygrab.views.GroupCreationView
import com.example.moneygrab.views.GroupPage


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

    NavHost(navController = navController, startDestination = "login") {
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
                group = TestData().copy(name = "Chat"),
                addExpense = { navController.navigate("addToExpense") },
                onBack = { navController.navigateUp() }
            )
        }

        composable("ProfilePage") {
            ProfilePage(
                credentialMethod = CredentialMethod(
                    fullName = "43",
                    email = "43",
                    phoneNumber = "43"
                ),
                paymentMethods = emptyList(),
                onBackClick = { navController.popBackStack() },
                onLogoutClick = { navController.navigate("login") }
                //onEditClick = { },
            )
        }

        composable("addToExpense") {
            AddPayersView(
                group = group,
                onAddExpense = { navController.popBackStack() }
            )
        }


        composable("groupCreation") {
            GroupCreationView(
                onCreateDone = {navController.popBackStack()}
            )
        }
    }
}

