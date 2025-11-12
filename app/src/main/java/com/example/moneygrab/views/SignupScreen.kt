package com.example.moneygrab.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneygrab.CurrentUser
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch
import com.example.debtcalculator.data.User

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
        Text(
            text = "Sign up",
            fontSize = 26.sp,
            modifier = Modifier.padding(bottom = 12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val user = signUpUser(name, email, phone, password)
                        currentUser.saveUser(user)
                        onSignUpSuccess()
                    } catch (e: Exception) {
                        errorMessage = "Buhu"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Create account", fontSize = 18.sp)
        }

        errorMessage?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = Color.Red)
        }
    }
}

suspend fun signUpUser(name: String, email: String, phone: String, password: String): User {
    val api = RetrofitClient().api
    return try {
        api.signup(
            mapOf("name" to name, "email" to email, "phone" to phone, "password" to password
            )
        )
    } catch (e: Exception) {
        User(phoneNumber = phone, name = name, image = null)
    }
}

/*@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MoneyGrabTheme {
        SignUpScreen(
            onSignUpClicked = { _, _, _, _ -> }
        )
    }
}*/
