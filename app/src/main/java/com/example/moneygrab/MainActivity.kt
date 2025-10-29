package com.example.moneygrab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moneygrab.screens.HomeScreen
import com.example.moneygrab.screens.LoginScreen
import com.example.moneygrab.ui.theme.MoneyGrabTheme

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

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginClicked ={ navController.navigate("home")})
        }
        composable("home"){
            HomeScreen()
        }
    }
}