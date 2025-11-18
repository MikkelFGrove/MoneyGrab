// ChatScreen.kt
package com.example.moneygrab.views

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.moneygrab.CurrentUser
import com.example.moneygrab.R
import kotlin.system.exitProcess

import com.example.moneygrab.views.TestData

data class MoneyRequest(val text: String, val isMine: Boolean)
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
                    Text(calculatedSum.toString() + " DKK", color = color, fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }
            }
            Text(
                text = groupName,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
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
     val bubbleColor = if (moneyRequest.lender.phoneNumber == CurrentUser(LocalContext.current).getUser()?.phoneNumber) {
        colors.primary
    } else {
        colors.primary.copy(alpha = 0.2f)
    }

    val textColor = if (moneyRequest.lender.phoneNumber == CurrentUser(LocalContext.current).getUser()?.phoneNumber) {
        Color.White
    } else {
        Color.Black
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        horizontalArrangement = if (moneyRequest.lender.phoneNumber == CurrentUser(LocalContext.current).getUser()?.phoneNumber) Arrangement.End else Arrangement.Start
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
fun InputBar(addExpense: (Group) -> Unit, group: Group) {
    Surface (modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp)
    {
        Button(
            onClick = { addExpense(group) },
            modifier = Modifier
                .padding(start= 50.dp, end = 50.dp, top = 5.dp, bottom = 10.dp),
            shape = RoundedCornerShape(5.dp)

        ) {
            Text("Add Expense", fontSize = MaterialTheme.typography.titleLarge.fontSize)

        }
    }

}

@Composable
fun ChatScreen(groupID: Int, addExpense: (Group) -> Unit, onBack: () -> Unit = {}, onConfirmation: (Group) -> Unit) {
    var showCloseDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()
    val group = fetchGroup(groupID)?: fetchGroups(currentUser).first()

    println("insidechat screen" + groupID)

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            groupName = group?.name ?: "null",
            calculatedSum = getSum(currentUser, group),
            onBack = onBack,
            onPayDebt = {
                showCloseDialog = true
            }
        )

        MessagesList(
            messages = group?.expenses?.toMutableList() ?: emptyList(),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        InputBar(addExpense, group)

        if (showCloseDialog) {
            DialogCloseTheTab(
                onDismissRequest = { showCloseDialog = false },
                onConfirmation = { onConfirmation(group) },
                group = group
            )
        }
    }
}

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
        ChatScreen(
            group = 1,
            addExpense = { println("Norway") },
            onConfirmation = { println("meep") },
            groupID = TODO(),
            onBack = TODO()
        )
    }
}
*/