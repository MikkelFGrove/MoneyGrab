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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.authentication.CurrentUser
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.RetrofitClient
import kotlinx.coroutines.launch

class ExpenseCreationViewModel(): ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var group by mutableStateOf<Group?>(null)
    var stringAmount by mutableStateOf("")
    var description by mutableStateOf("")
    var expense: Expense? = null

    fun fetchGroup(groupId: Int) {
        viewModelScope.launch {
            val response = try {
                api.getGroup(groupId)
            } catch (e: Exception) {
                null
            }

            response?.body()?.let {
                group = it
            }
        }
    }

    fun createExpense(lender: User) {
        viewModelScope.launch {
            expense = Expense(
                id = -1,
                amount = try {
                    stringAmount.toFloat()
                } catch (e: Exception) {
                    0f
                },
                description = description,
                owner = lender,
                group = group?.id ?: -1,
                payers = emptyList()
            )

            val response = try {
                expense?.let {
                    api.createExpense(
                        body = it
                    )
                }
            } catch (e: Exception) {
                println(e.message)
                null
            }

            response?.body()?.let { res ->
                expense?.let { it ->
                    it.id = res
                }
            }
        }
    }
}

@Composable
fun AddExpenseView(groupId: Int, addToExpense: (Expense?) -> Unit, back: () -> Unit) {
    val expenseCreationViewModel: ExpenseCreationViewModel = viewModel()
    var stringAmount = expenseCreationViewModel.stringAmount
    var description = expenseCreationViewModel.description

    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()

    LaunchedEffect(groupId) {
        expenseCreationViewModel.fetchGroup(groupId)
    }

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
                value = stringAmount,
                onValueChange = { stringAmount = it },
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
                currentUser?.let {
                    expenseCreationViewModel.createExpense(it)
                    addToExpense(expenseCreationViewModel.expense)
                }
            },
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
        AddExpenseView()
    }
}