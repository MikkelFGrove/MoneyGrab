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
import com.example.debtcalculator.data.Group
import com.example.moneygrab.screens.HomeScreen
import com.example.moneygrab.screens.LoginScreen

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
            LoginScreen(onLoginClicked = { navController.navigate("groupPage")})
        }
        composable("groupPage") {
            GroupPage(listOf(), { navController.navigate("groupCreation") })
        }
        composable ("addToExpense") {
            AddPayersView(
                group = group
            )
        }
        composable("groupCreation") {
            GroupCreationView()
        }
    }
}

