package com.example.moneygrab.views

import android.content.Context
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authentication.CurrentUser
import androidx.compose.ui.unit.sp
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var wrongCredentials = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    var isLoading = mutableStateOf(false)

    fun login(navigation: () -> Unit, context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            val response = try {
                api.login(APIEndpoints.LoginData(phone.value, password.value))
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                wrongCredentials.value = true
                isLoading.value = false
                null
            }

            isLoading.value = false

            if (response?.code() == 523) {
                errorMessage.value = "Network connection could not be established"
                wrongCredentials.value = true
            } else if (response?.code() != 200){
                errorMessage.value = "The phone number or password is incorrect"
                wrongCredentials.value = true
            } else {
                response.body()?.let {
                    CurrentUser(context).saveUser(it)
                    navigation()
                }

            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier, onLoginClicked: () -> Unit, onSignupClicked: () -> Unit) {
    val loginViewModel: LoginViewModel = viewModel()
    var phone by loginViewModel.phone
    var password by loginViewModel.password
    var wrongCredentials by loginViewModel.wrongCredentials
    var errorMessage by loginViewModel.errorMessage
    var isLoading by loginViewModel.isLoading
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Logging in")

        }
        Spacer(Modifier.height(12.dp))
        Text("Login",
            fontSize = 26.sp,
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
            onClick = { loginViewModel.login(onLoginClicked, context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text("Login")
        }

        if (wrongCredentials) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(errorMessage, color = Color.Red)
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MoneyGrabTheme {
        LoginScreen(
            onSignupClicked = {},
            onLoginClicked = {}
        )
    }
}
