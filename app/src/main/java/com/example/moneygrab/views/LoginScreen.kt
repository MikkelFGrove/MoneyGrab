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
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch

// TODO - Pass on user information to a User Context, this context could be injected into the ViewModel
class LoginViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var wrongCredentials = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    fun login(navigation: () -> Unit) {
        viewModelScope.launch {
            val response = try {
                api.login(APIEndpoints.LoginData(phone.value, password.value))
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                wrongCredentials.value = true
                null
            }

            if (response?.code() != 200) {
                errorMessage.value = "The phone number or password is incorrect"
                wrongCredentials.value = true
            } else {
                // Set user in context
                navigation()
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onLoginClicked: () -> Unit, onSignupClicked: () -> Unit, loginViewModel: LoginViewModel = LoginViewModel()) {
    var phone by loginViewModel.phone
    var password by loginViewModel.password
    var wrongCredentials by loginViewModel.wrongCredentials
    var errorMessage by loginViewModel.errorMessage
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

        if (wrongCredentials) {
            ErrorCard(errorMessage)

            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = { loginViewModel.login(onLoginClicked) },
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

fun loginUser(phone: String, password: String): User {
    val api = RetrofitClient().api
    /*return try {
        //val loginRequest = LoginRequest(phone, password)
        //api.login(loginRequest)
    } catch (e: Exception) {
        User(phoneNumber = phone, name = "Mi Bomba Clat", image = null)
    }*/
    return User(phoneNumber = phone, name = "Mi Bomba Clat", image = null)
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MoneyGrabTheme {
        LoginScreen(
            onSignupClicked = {},
            onLoginSuccess = {}
        )
    }
}
