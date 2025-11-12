package com.example.moneygrab.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moneygrab.CurrentUser
import com.example.moneygrab.R
import com.example.moneygrab.ui.theme.MoneyGrabTheme

data class CredentialMethod(
    val fullName: String,
    val email: String,
    val phoneNumber: String
)

data class PaymentMethod(
    val brand: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int
)

@Composable
fun ProfilePage(

    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onManageCardsClick: () -> Unit = {}
)

{
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }

    val fullName: String = currentUser.getUser()?.name ?: "null"

    val phoneNumber: String = currentUser.getUser()?.phoneNumber ?: "null"

    //get from user or remove
    val email = "Sigma Grandpa 67"
    val paymentMethods: List<PaymentMethod> = emptyList()

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

        // Takes parameters fullName, email, phoneNumber
        Text(
            text = credentialMethod.fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = credentialMethod.email,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Gray
        )
        Text(
            text = credentialMethod.phoneNumber,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Manage credit card
        // Would need logic for managing cards
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Payment methods",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            TextButton(
                onClick = onManageCardsClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF1E88E5)
                )
            ) {
                Text(
                    text = "Manage",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }



        if (paymentMethods.isEmpty()) {
            Text(
                text = "No payment methods added yet.",
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align (Alignment.CenterHorizontally)
            )
        } else {
            paymentMethods.forEach { method ->
                PaymentCard(method)
            }
        }


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

@Composable
private fun PaymentCard(method: PaymentMethod) {
    val masked = "•••• ${method.last4.takeLast(4)}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)), // background color
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = masked,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = method.brand,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
            Text(
                text = "Expires %02d/%02d".format(method.expMonth, method.expYear % 100),
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
}


/*@Preview(showBackground = true)
@Composable
fun ProfilePagePreview() {
    MoneyGrabTheme {
        ProfilePage(
            fullName = "Magnussen R. Christensen",
            email = "magnussen@gmail.com",
            phoneNumber = "+45 42560809",
            paymentMethods = listOf(
                PaymentMethod("Visa", "4444", 8, 2027)
            )
        )
    }
}*/


