package com.example.moneygrab.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.moneygrab.CurrentUser
import com.example.moneygrab.R
import com.example.moneygrab.components.SlideToUnlock
import com.example.moneygrab.ui.theme.MoneyGrabTheme

private fun getSum(currentUser: User?, group1: Group?): Double{
    /*val api = RetrofitClient().api
    return try {
        api.fetchGroups(user)
    } catch (e: Exception){
        emptyList()
    }*/
    return 0.0
}

private fun fetchGroup(id: Int): Group?{
    /*val api = RetrofitClient().api
    return try {
        api.fetchGroups(user)
    } catch (e: Exception){
        emptyList()
    }*/
    return null
}
@Composable
fun ConfirmPaymentPage(
    modifier: Modifier = Modifier,
    groupId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()
    val group = fetchGroup(groupId)
    val groupName = group?.name ?: ""
    val debt = getSum(currentUser, group)
    //State hoisting for the slider
    var isLoading by remember { mutableStateOf(false) }

    val headlineColor = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        )
        {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back"
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "You owe",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = headlineColor
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .scale(2f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 28.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = groupName,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Text(
                    text = "${debt} kr",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        letterSpacing = 0.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.weight(1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    SlideToUnlock(
                        isLoading = isLoading,
                        onUnlockRequested = { isLoading = true }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
}


@Preview(showBackground = true)
@Composable
fun PayPreview() {
    MoneyGrabTheme {
        ConfirmPaymentPage(groupId = 1, onBack = {println("bubu")})
    }
}
