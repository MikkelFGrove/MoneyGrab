// ChatScreen.kt
package com.example.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.User
import com.example.debtcalculator.data.Message
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.views.TestData
import kotlinx.coroutines.launch

data class MoneyRequest(val text: String, val isMine: Boolean)

class ChatViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var user: User? = null
    var groupId = mutableIntStateOf(-1)
    var groupName = mutableStateOf("")
    var expenses = mutableStateListOf<Expense>()
    var tabClosed = mutableStateOf(false)
    var amountOwed = mutableFloatStateOf(Float.NaN)
    var messages = mutableStateListOf<Message>()
    var errorHappened = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var showCloseDialog = mutableStateOf(false)

    fun fetchGroupData() {
        viewModelScope.launch {
            val response = try {
                api.getGroup(groupId.intValue)
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                errorHappened.value = true
                null
            }

            if (response?.code() != 200) {
                errorMessage.value = "The phone number or password is incorrect"
                errorHappened.value = true
            } else {
                response.body()?.let {
                    groupName.value = it.name
                    expenses.clear()
                    expenses.addAll(it.expenses)
                    messages.clear()
                    messages.addAll(it.messages)
                }
            }
        }
    }

    fun fetchAmountOwed() {
        viewModelScope.launch {
            user?.let {
                val response = try {
                    api.getAmountOwed(groupId.intValue, it.phoneNumber)
                } catch (e: Exception) {
                    null
                }

                response?.let {
                    tabClosed.value = true
                    fetchGroupData()
                }
            }
        }
    }

    fun closeTab() {
        viewModelScope.launch {
            val response = try {
                api.closeTab(groupId.intValue)
            } catch (e: Exception) {
                null
            }

            response?.let {
                tabClosed.value = true
                fetchGroupData()
            }
        }
    }
}

@Composable
fun TopBar(groupName: String, calculatedSum: Double, onBack: () -> Unit, onPayDebt: () -> Unit) {
    var color: Color
    if (calculatedSum < 0){
        color = Color.Red
    } else if(calculatedSum > 0) {
        color = Color.Green
    } else {
        color = Color.White
    }
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
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
                Button(
                    onClick = onPayDebt,
                    contentPadding = PaddingValues(
                        start = 4.dp,
                        top = 1.dp,
                        end = 4.dp,
                        bottom = 1.dp,
                    ),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        contentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    ),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 3.dp, minHeight = 3.dp)

                ) {
                    Text(calculatedSum.toString() +  "DKK", color = color)
                }
            }
            Text(
                text = groupName,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@Composable
fun Bubbles(moneyRequest: Expense) {
    val colors = MaterialTheme.colorScheme
    //This needs to be implemented again when login authcontext is up and running
    /* val bubbleColor = if (moneyRequest.isMine) {
        colors.primary
    } else {
        colors.primary.copy(alpha = 0.2f)
    }

    val textColor = if (moneyRequest.isMine) {
        Color.White
    } else {
        Color.Black
    }*/
    val bubbleColor = colors.primary
    val textColor = Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.Start /*if (moneyRequest.isMine) Arrangement.End else Arrangement.Start*/
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = moneyRequest.description,
                color = textColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}


@Composable
fun MessagesList(messages: List<Expense>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(messages) { _, msg ->
            Bubbles(moneyRequest = msg)
        }
    }
}

@Composable
fun InputBar(addExpense: () -> Unit) {
    Surface (modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp)
    {
        Button(
            onClick = addExpense,
            modifier = Modifier
                .padding(start= 50.dp, end = 50.dp, top = 5.dp, bottom = 5.dp),
            shape = RoundedCornerShape(5.dp)

        ) {
            Text("Add Expense")

        }
    }

}

@Composable
fun ChatScreen(groupId: Int, addExpense: () -> Unit, onBack: () -> Unit = {}, chatViewModel: ChatViewModel = ChatViewModel()) {
    var showCloseDialog by chatViewModel.showCloseDialog
    var groupName = chatViewModel.groupName
    var expenses = chatViewModel.expenses

    LaunchedEffect(groupId) {
        chatViewModel.groupId.intValue = groupId
        chatViewModel.fetchGroupData()
        chatViewModel.fetchAmountOwed()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            groupName = groupName.value,
            //Change to API-call 😁
            calculatedSum = 0.00,
            onBack = onBack,
            onPayDebt = {
                showCloseDialog = true
            }
        )

        MessagesList(
            messages = expenses,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        InputBar(addExpense)

        if (showCloseDialog) {
            DialogCloseTheTab(
                onDismissRequest = { showCloseDialog = false },
                onConfirmation = { chatViewModel.closeTab() }
            )
        }
    }
}

@Composable
fun DialogCloseTheTab(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Do you wish to close the tab? " +
                            "No more expenses can be added before all members" +
                            "have paid their debts.",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val group = TestData()
    MaterialTheme {
        ChatScreen(groupId = 1, addExpense = {println("Norway")}
        )
    }
}
