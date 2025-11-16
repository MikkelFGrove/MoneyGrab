package com.example.moneygrab.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneygrab.R
import com.example.moneygrab.ui.theme.MoneyGrabTheme

data class CredentialMethod(
    val fullName: String,
    val phoneNumber: String
)


@Composable
fun ProfilePage(
    credentialMethod: CredentialMethod,
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
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
            IconButton(onClick = onEditClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        // Currently just a placeholder, would be cool to actually
        // create the functionality to add images from your PC
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
        Text(
            text = credentialMethod.fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = credentialMethod.phoneNumber,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))


        // Logout button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935), // Maybe add the purple button back?
                contentColor = Color.White 
            )
        ) {
            Text(
                text = "Log Out",
                fontSize = 20.sp)
        }
    }

}