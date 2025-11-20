// ChatScreen.kt
package com.example.moneygrab.views

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.debtcalculator.data.Message
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import kotlinx.coroutines.launch
import com.example.authentication.CurrentUser


class ChatViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var user: User? = null
    var group: Group by mutableStateOf(Group(
        id = -1,
        name = "",
        users = emptySet(),
        expenses = mutableListOf(),
        tabClosed = false,
        description = "",
        messages = mutableListOf()
    ))
    var amountOwed = mutableFloatStateOf(0f)
    var messages = mutableStateListOf<Message>()
    var errorHappened = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var showCloseDialog = mutableStateOf(false)

    fun setUser(context: Context) {
        CurrentUser(context).getUser()?.let {
            user = it
        }
    }

    fun fetchGroupData(groupId: Int) {
        viewModelScope.launch {
            var g: Group? = null

            val response = try {
                api.getGroup(groupId)
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                errorHappened.value = true
                null
            }

            if (!(response?.isSuccessful ?: false)) {
                errorMessage.value = "The phone number or password is incorrect"
                errorHappened.value = true
            } else {
                response.body()?.let {
                    println("Group: ${it}")
                    g = it
                }
            }

            g?.let { g ->
                val expenses = try {
                    api.getExpensesInGroup(g.id)
                } catch (e: Exception) {
                    println(e.message)
                    null
                }

                expenses?.body()?.let { e ->
                    var updatedExpenses: MutableList<Expense> = mutableListOf()
                    println("Expenses: ${e}")
                    for (expense: APIEndpoints.ChatExpense in e) {
                        updatedExpenses.add(Expense(
                            expense.id,
                            expense.amount,
                            expense.description,
                            expense.group,
                            User(
                                expense.owner,
                                "",
                                "",
                                ""
                            ),
                            mutableListOf()))
                    }
                    g.expenses = updatedExpenses
                }
                group = g
            }

            println("Number of expenses: ${group.expenses}")
        }
    }

    fun fetchAmountOwed(groupId: Int) {
        viewModelScope.launch {
            user?.let {
                val response = try {
                    api.getAmountOwed(groupId, it.id)
                } catch (e: Exception) {
                    println(e.message)
                    null
                }

                response?.body()?.let {
                    println(it)
                    amountOwed.floatValue = it.amount
                }
            }
        }
    }

    fun closeTab() {
        viewModelScope.launch {
            val response = try {
                api.closeTab(group.id)
            } catch (e: Exception) {
                null
            }

            response?.let {
                fetchGroupData(group.id)
            }
        }
    }
}

@Composable
fun TopBar(group: Group, groupName: String, calculatedSum: Float, onBack: () -> Unit, onPayDebt: () -> Unit, onName: (Group) -> Unit) {
    var color: Color
    val group = group
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
                    Text("$calculatedSum DKK", color = color)
                }
            }
            TextButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = { onName(group) }){
                Text(
                text = groupName,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                textAlign = TextAlign.Center,
                )
            }
        }
    }
}


@Composable
fun Bubbles(moneyRequest: Expense) {
    val colors = MaterialTheme.colorScheme
    //This needs to be implemented again when login authcontext is up and running
     val bubbleColor = if (moneyRequest.owner.id == CurrentUser(LocalContext.current).getUser()?.id) {
        colors.primary
    } else {
        colors.primary.copy(alpha = 0.2f)
    }

    val textColor = if (moneyRequest.owner.id == CurrentUser(LocalContext.current).getUser()?.id) {
        Color.White
    } else {
        Color.Black
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        horizontalArrangement = if (moneyRequest.owner.id == CurrentUser(LocalContext.current).getUser()?.id) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = "${moneyRequest.description} ${ "%.2f".format(moneyRequest.amount) }",
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
fun InputBar(onNotifyUsers: () -> Unit, addExpense: (Group) -> Unit, group: Group) {
    Surface(
        modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp
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
                Button(
                    onClick = { addExpense(group) },
                    modifier = Modifier,
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text("Add Expense", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }
                Button(
                    onClick = { onNotifyUsers() },
                    modifier = Modifier,
                    shape = RoundedCornerShape(5.dp)

                ) {
                    Text("Notify users", fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }
            }
        }
    }
}

@Composable
fun ChatScreen(groupId: Int, addExpense: (Group) -> Unit,
               onBack: () -> Unit = {}, onConfirmation: (Group) -> Unit,
               onName: (Group) -> Unit, onNotifyUsers: () -> Unit) {
    val chatViewModel: ChatViewModel = viewModel()
    var showCloseDialog by chatViewModel.showCloseDialog
    var amountOwed by chatViewModel.amountOwed
    var groupName = chatViewModel.group.name
    var expenses = chatViewModel.group.expenses
    var context = LocalContext.current

    LaunchedEffect(groupId) {
        chatViewModel.setUser(context)
        chatViewModel.fetchGroupData(groupId)
        chatViewModel.fetchAmountOwed(groupId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            group = chatViewModel.group,
            groupName = groupName,
            calculatedSum = amountOwed,
            onName = onName,
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

        InputBar(onNotifyUsers, addExpense, chatViewModel.group)

        if (showCloseDialog) {
            DialogCloseTheTab(
                onDismissRequest = { showCloseDialog = false },
                onConfirmation = {
                    showCloseDialog = false
                    chatViewModel.closeTab()
                    onConfirmation
                },
                group = chatViewModel.group
            )
        }
    }
}

@Composable
fun DialogCloseTheTab(
    onDismissRequest: () -> Unit,
    onConfirmation: (Group) -> Unit,
    group: Group
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
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
                    text = "Do you wish to close the tab? \n" +
                            "No more expenses can be added before all members " +
                            "have paid their debts.",
                    modifier = Modifier.padding(5.dp),
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
                        Text("No")
                    }
                    TextButton(
                        onClick = { onConfirmation(group) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Close Tab")
                    }
                }
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    val group = TestData()
    MaterialTheme {
        ChatScreen(groupId = 1, addExpense = {println("Norway")}
        )
    }
}*/