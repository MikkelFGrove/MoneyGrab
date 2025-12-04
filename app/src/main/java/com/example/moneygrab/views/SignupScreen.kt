package com.example.moneygrab.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.authentication.CurrentUser
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch
import com.example.moneygrab.R
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.sign

class SignupViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var name = mutableStateOf("")
    var phone = mutableStateOf("")
    var password = mutableStateOf("")
    var image = ""
    var errorHasOccurred = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    fun signup(navigation: () -> Unit, context: Context) {
        viewModelScope.launch {
            val response = try {
                api.signup(APIEndpoints.SignupData(phone.value, password.value, name.value, image))
            } catch (e: Exception) {
                println(e.message)
                errorMessage.value = "An error has occurred"
                errorHasOccurred.value = true
                null
            }

            if (!(response?.isSuccessful ?: false)) {
                errorMessage.value = "The phone number is already in use"
                println(response?.body()?.name)
                errorHasOccurred.value = true
            } else {
                response.body()?.let {
                    CurrentUser(context).saveUser(it)
                    navigation()
                }
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun storeImage(img: Bitmap?) {
        img?.let {
            val os = ByteArrayOutputStream()
            img.compress(Bitmap.CompressFormat.JPEG, 15, os)
            val byteArray: ByteArray = os.toByteArray()
            val encoded = Base64.encode(byteArray);
            image = encoded
            println(image)
        }
    }
}

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpClicked: () -> Unit,
    onBackLogin: () -> Unit
) {
    val signupViewModel: SignupViewModel = viewModel()
    var name by signupViewModel.name
    var phone by signupViewModel.phone
    var password by signupViewModel.password
    var errorMessage by signupViewModel.errorMessage
    var errorHasOccurred by signupViewModel.errorHasOccurred
    var context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBackLogin) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back"
            )
        }
    }
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

            Spacer(Modifier.height(12.dp))

            ImageButton(signupViewModel)

            Spacer(Modifier.height(12.dp))

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
            onClick = { signupViewModel.signup(onSignUpClicked, context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)

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

@Composable
fun ImageButton(signupViewModel: SignupViewModel) {
    var imageUri: Uri? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(imageUri)
                .target { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    signupViewModel.storeImage(bitmap)
                }
                .build()
            loader.enqueue(req)
        } else {
            println("Failed")
        }
    }

    Button (
        modifier = Modifier
            .width(250.dp)
            .height(150.dp),
        contentPadding = PaddingValues(0.dp, 0.dp),
        colors = ButtonColors(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, Color.Transparent, Color.Transparent),
        shape = MaterialTheme.shapes.large,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

    ) {
        if (imageUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = imageUri)
                    .build()
            )
            Image (
                painter = painter,
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Image"
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Image"
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    MoneyGrabTheme {
        SignUpScreen(
            onSignUpClicked = { },
            onBackLogin = {}
        )
    }
}
