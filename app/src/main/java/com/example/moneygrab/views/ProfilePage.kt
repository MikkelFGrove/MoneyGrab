package com.example.moneygrab.views

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authentication.CurrentUser
import com.example.debtcalculator.data.User
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import kotlinx.coroutines.launch
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import com.example.moneygrab.components.openNotificationSettings

class ProfilePageViewModel() : ViewModel() {
    var editMode = mutableStateOf(false)

    var currentUser = mutableStateOf<User?>(null)
    var name = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var errorMessage = mutableStateOf("")
    private val api: APIEndpoints = RetrofitClient.getAPI()
    private var userId = mutableStateOf(-1)

    fun getUser(context: Context) {
        currentUser.value = CurrentUser(context).getUser()
        name.value = currentUser.value?.name.toString()
        phoneNumber.value = currentUser.value?.phoneNumber.toString()
        userId.value = currentUser.value?.id?: -1
    }

    fun saveUser(context: Context) {
        viewModelScope.launch {
            val response = try {
                api.updateUser(APIEndpoints.UpdateUser( phoneNumber.value, name.value, "", userId.value))
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                null
            }

            if (!(response?.isSuccessful?: false)) {
                errorMessage.value = "The phone number or password is incorrect"
            } else {
                response.body()?.let {
                    println("SIGMASABALLS")
                    CurrentUser(context).saveUser(it)
                    editMode.value = false
                }
            }
        }
    }


}

@Composable
fun ProfilePage(
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val profilePageViewModel: ProfilePageViewModel = viewModel()
    LaunchedEffect(CurrentUser(context).getUser()) {
        profilePageViewModel.getUser(context)
    }
    var name by profilePageViewModel.name
    var phone by profilePageViewModel.phoneNumber

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar for arrow and edit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
            IconButton(onClick = {
                profilePageViewModel.editMode.value = !profilePageViewModel.editMode.value
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile image
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Takes parameters fullName, phoneNumber
        if (profilePageViewModel.editMode.value) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Phone number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = profilePageViewModel.name.value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = profilePageViewModel.phoneNumber.value,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if(!profilePageViewModel.editMode.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { context.openNotificationSettings() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notification_icon),
                    contentDescription = "Notification Icon",
                    modifier = Modifier.size(30.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Text(
                    text = "Manage notifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (profilePageViewModel.editMode.value){
            Button(
                onClick = {
                    profilePageViewModel.saveUser(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Save User",
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Logout button
        Button(
            onClick = {
                CurrentUser(context = context).clear()
                println("What")
                onLogoutClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Log Out",
                fontSize = 20.sp
            )
        }
    }
}
