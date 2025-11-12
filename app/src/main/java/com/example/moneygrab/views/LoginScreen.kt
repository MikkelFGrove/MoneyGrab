package com.example.moneygrab.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.CurrentUser
import com.example.debtcalculator.data.User

import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onLoginClicked: () -> Unit, onSignupClicked: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var password by remember {mutableStateOf("")}
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login",
            modifier = Modifier
                .padding(bottom = 12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it},
            label = { Text("Phone number")},
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    val user = try {
                        loginUser(phone, password)
                    } catch (e: Exception) {
                        null
                    }
                    println(user?.phoneNumber)
                    if (user != null) {
                        currentUser.saveUser(user)
                        onLoginSuccess()
                    } else {
                        errorMessage = "Buhu"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text("Login")
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = Color.Red)
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Don’t have an account? ")

            Text(
                text = "Sign up",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable { onSignupClicked() }
            )
        }
    }
}

suspend fun loginUser(phone: String, password: String): User {
    val api = RetrofitClient().api
    return try {
        api.login(mapOf("phone" to phone, "password" to password))
    } catch (e: Exception) {
        User(phoneNumber = phone, name = "Mi Bomba Clat", image = null)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MoneyGrabTheme {
        LoginScreen(
            onLoginClicked = {},
            onSignupClicked = {}
        )
    }
}
