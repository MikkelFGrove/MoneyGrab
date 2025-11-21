package com.example.moneygrab.views

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.authentication.CurrentUser
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Message
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.components.SlideToUnlock
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

class ConfirmPaymentModelView() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var user: User? = null
    var amountOwed = mutableFloatStateOf(0f)
    var errorHasOccurred = mutableStateOf(false)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var group: Group by mutableStateOf(Group(
        id = -1,
        name = "",
        users = emptySet(),
        expenses = mutableListOf(),
        tabClosed = false,
        description = "",
        messages = mutableListOf()
    ))

    fun setUser(context: Context) {
        CurrentUser(context).getUser()?.let {
            user = it
        }
    }

    fun fetchGroupData(groupId: Int) {
        viewModelScope.launch {
            val response = try {
                api.getGroup(groupId)
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                null
            }

            if (response?.code() != 200) {
                errorMessage.value = "The phone number or password is incorrect"
            } else {
                response.body()?.let {
                    group = it
                }
            }
        }
    }
    var sum = mutableStateOf(0f)
    fun getSum( groupId: Int){
        println(groupId.toString() + "<<<<<<<<<<<<<<<<<<<<<<")
        viewModelScope.launch {
            user?.let {
                val response = try {
                    api.getAmountOwed(groupId, it.id)
                } catch (e: Exception) {
                    println(e.message)
                    null
                }
                println(groupId + it.id)
                response?.body()?.let {
                    println(it)
                    amountOwed.floatValue = it.amount
                }
            }
        }
    }
    fun payTransaction(navigation: (Group) -> Unit, context: Context){
        println("WHAT THE HELLY")
        viewModelScope.launch {
            isLoading.value = true
            val response = try {
                api.payTransactions(APIEndpoints.PayTransactionsRequest(
                    groupId = group.id,
                    userId = CurrentUser(context).getUser()?.id ?: -1
                ))
            } catch (e: Exception) {
                println(e.message)
                errorMessage.value = "An error has occurred"
                errorHasOccurred.value = true
                null
            }
            delay(3000)

            if (!(response?.isSuccessful ?: false)) {
                errorMessage.value = "Not enough funds"
                errorHasOccurred.value = true
            } else {
                isLoading.value = false
                navigation(group)
                println(group)
            }
        }
    }

}

@Composable
fun ConfirmPaymentPage(
    modifier: Modifier = Modifier,
    groupId: Int,
    navigation: (Group) -> Unit
) {

    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()
    val confirmPaymentModelView: ConfirmPaymentModelView = viewModel()

    LaunchedEffect(groupId) {
        confirmPaymentModelView.fetchGroupData(groupId)
        confirmPaymentModelView.getSum(groupId)
        confirmPaymentModelView.setUser(context)
    }

    val groupName = confirmPaymentModelView.group.name
    //State hoisting for the slider

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
                    IconButton(onClick = { navigation(confirmPaymentModelView.group) }) {
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
                        text = confirmPaymentModelView.group.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                Text(
                    text = "${confirmPaymentModelView.amountOwed.value} kr",
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
                        isLoading = confirmPaymentModelView.isLoading.value,
                        onUnlockRequested = {
                            confirmPaymentModelView.payTransaction(
                                context = context,
                                navigation = { paidGroup -> navigation(paidGroup) }
                            )
                        }
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
        }
}


/*@Preview(showBackground = true)
@Composable
fun PayPreview() {
    MoneyGrabTheme {
        ConfirmPaymentPage(groupId = 1, onBack = {println("bubu")})
    }
}
*/