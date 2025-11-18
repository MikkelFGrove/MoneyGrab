package com.example.moneygrab.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch

// TODO - Pass on user information to a User Context, this context could be injected into the ViewModel
class SignupViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var name = mutableStateOf("")
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var errorHasOccurred = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    fun signup(navigation: () -> Unit) {
        viewModelScope.launch {
            val response = try {
                api.login(APIEndpoints.LoginData(phone.value, password.value))
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                errorHasOccurred.value = true
                null
            }

            if (response?.code() != 200) {
                errorMessage.value = "The phone number is already in use"
                errorHasOccurred.value = true
            } else {
                // Set user in Context
                navigation()
            }
        }
    }
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    signupViewModel: SignupViewModel = SignupViewModel(),
    onSignUpClicked: () -> Unit,
) {
    var name by signupViewModel.name
    var phone by signupViewModel.phone
    var password by signupViewModel.password
    var errorMessage by signupViewModel.errorMessage
    var errorHasOccurred by signupViewModel.errorHasOccurred

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
            onClick = { signupViewModel.signup(onSignUpClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Create account", fontSize = 18.sp)
        }

        if (errorHasOccurred) {
            Spacer(Modifier.height(12.dp))
            Text(errorMessage, color = Color.Red)
        }
    }
}
@Composable
fun ErrorCard(text: String, modifier: Modifier = Modifier) {
    Card (
        modifier = modifier,
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardColors(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError,
            Color.Transparent,
            Color.Transparent
        )
    ) {
        Text (
            modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp),
            text = text,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MoneyGrabTheme {
        SignUpScreen(
            onSignUpClicked = { }
        )
    }
}
