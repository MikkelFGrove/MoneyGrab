package com.example.moneygrab.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.authentication.CurrentUser


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
fun AddExpenseView(groupId: Int, addToExpense: (Group?) -> Unit, back: () -> Unit) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()

    val group = fetchGroup(groupId)?: fetchGroups(currentUser).first()

    Button(modifier = Modifier.padding(start= 10.dp, top = 10.dp), onClick = back) {
        Text("Back")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(64.dp),
        verticalArrangement = Arrangement.spacedBy(space = 32.dp,
            alignment = Alignment.CenterVertically),

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.weight(1f)
                    .padding(end = 10.dp),
                placeholder = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), singleLine = true
            )
            Text("DKK", style = MaterialTheme.typography.bodyLarge)
        }

        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Add Text") })
        Button(
            onClick = {
                var expense = Expense(
                    amount = try {
                        amount.toFloat()
                    } catch (e: Exception) {
                        0f
                    },
                    description = description,
                    //CHANGE THIS WHEN AUTH CONTEXT
                    lender = currentUser?: User(
                        id = -1,
                        phoneNumber = "0",
                        name = "TODO()",
                        image = null
                    ),
                    payers = group?.users?.toTypedArray()?: emptyArray<User>()
                )
                group.expenses?.add(expense)
                addToExpense(group) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Participants")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseView() {
    MaterialTheme {
    }
}