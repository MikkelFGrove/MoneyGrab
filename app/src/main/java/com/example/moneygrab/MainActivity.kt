package com.example.moneygrab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneygrab.views.LoginScreen

import com.example.moneygrab.ui.theme.MoneyGrabTheme
import com.example.moneygrab.views.AddPayersView
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
    val group = testData()

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signup") {
        composable("signup") {
            SignUpScreen(onSignUpSuccess = {navController.navigate("pPage")
            })
        }

        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("pPage")})
        }
        composable("groupPage") {
            GroupPage(listOf(), { navController.navigate("groupCreation") })
        }
        composable("ppage") {
            ProfilePage(
                onBackClick = { println("Sigma back") },
                onEditClick = { println("Sigma back") },
                onLogoutClick = { println("Sigma back") },
                onManageCardsClick = { println("Sigma back") }
            )
        }
        composable ("addToExpense") {
            AddPayersView(
                group = group
            )
        }
        composable("groupCreation") {
            GroupCreationView({ navController.navigate("groupPage") })
        }
    }
}

