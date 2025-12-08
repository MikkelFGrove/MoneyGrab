package com.example.moneygrab.views

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.moneygrab.R
import com.example.authentication.CurrentUser
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.RetrofitClient
import kotlinx.coroutines.launch
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class ExpenseCreationViewModel(): ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var group by mutableStateOf<Group?>(null)
    var stringAmount = mutableStateOf("")
    var description = mutableStateOf("")
    var selectedLender = mutableStateOf<User?>(null)
    var selectedUsers = mutableStateListOf<User>()
    var expense: Expense? = null


    fun fetchGroup(groupId: Int) {
        viewModelScope.launch {
            var tempGroup: Group? = null
            val groupResponse = try {
                api.getGroup(groupId)
            } catch (e: Exception) {
                println(e.message)
                null
            }

            groupResponse?.body()?.let {
                tempGroup = it
            }

            tempGroup?.let { g ->
                val usersResponse = try {
                    api.getUsersInGroup(g.id)
                } catch (e: Exception) {
                    println(e.message)
                    null
                }

                usersResponse?.body()?.let { users ->
                    g.users = users
                    selectedUsers.clear()
                    selectedUsers.addAll(users)
                    group = tempGroup
                }
            }
        }
    }

    fun createExpense(user: User, context: Context, onCreateExpense: (Group?) -> Unit) {
        viewModelScope.launch {
            if (stringAmount.value == ""){
                MotionToast.createColorToast(
                    context as Activity,
                    "Error",
                    "Cannot be negative or 0",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular))
            } else {
                if (Integer.parseInt(stringAmount.value) > 0 ){
                    var e = Expense(
                        id = -1,
                        amount = try {
                            stringAmount.value.toFloat()
                        } catch (e: Exception) {
                            0f
                        },
                        description = description.value,
                        owner = selectedLender.value,
                        group = group?.id ?: -1,
                        payers = selectedUsers
                    )

                    val response = try {
                        api.createExpense(
                            body = e
                        )
                    } catch (e: Exception) {
                        println(e.message)
                        null
                    }

                    response?.body()?.let { res ->
                        e.id = res
                        expense = e
                    }
                    onCreateExpense(group)
                } else {
                    MotionToast.createColorToast(
                        context as Activity,
                        "Error",
                        "Cannot be negative or 0",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(context, www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                    stringAmount.value = Integer.toString(0)
                }
            }

        }
    }
}

@Composable
fun AddExpenseView(groupId: Int, onCreateExpense: (Group?) -> Unit, back: () -> Unit) {
    val expenseCreationViewModel: ExpenseCreationViewModel = viewModel()
    var stringAmount = expenseCreationViewModel.stringAmount
    var description = expenseCreationViewModel.description
    var selectedUsers = expenseCreationViewModel.selectedUsers

    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()

    LaunchedEffect(groupId) {
        expenseCreationViewModel.fetchGroup(groupId)
        expenseCreationViewModel.selectedLender.value = currentUser
    }

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
            IconButton(onClick = back) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp),
        verticalArrangement = Arrangement.spacedBy(space = 32.dp,
            alignment = Alignment.CenterVertically
        ),

    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = stringAmount.value,
                onValueChange = { stringAmount.value = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                placeholder = { Text("Amount") },
                suffix = { Text("DKK", style = MaterialTheme.typography.bodyLarge)},
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number), singleLine = true
            )
        }

        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Add Text") })


        AddLenderToExpense(expenseCreationViewModel, currentUser);

        AddUsersToExpense(expenseCreationViewModel, currentUser)

        Button(
            onClick = {
                    currentUser?.let {
                        expenseCreationViewModel.createExpense(it, context, onCreateExpense)
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Create Expense with ${selectedUsers.size} payer${if (selectedUsers.size != 1) "s" else ""}"
            )
        }
    }
}


@Composable
fun AddLenderToExpense(expenseCreationViewModel: ExpenseCreationViewModel, currentUser: User?) {
    val isDropDownExpanded = remember { mutableStateOf(false) }
    var group = expenseCreationViewModel.group
    var selectedUser = expenseCreationViewModel.selectedLender.value
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text (
            text = "Paid by",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { isDropDownExpanded.value = true }
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
            ) {
                expenseCreationViewModel.selectedLender.value?.let {
                    Text(
                        text = it.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown icon"
                )
            }

            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = { isDropDownExpanded.value = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                group?.users?.forEach { user ->
                    val isSelected = (selectedUser?.id == user.id)
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = user.name)
                                if(isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            expenseCreationViewModel.selectedLender.value = user
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun AddUsersToExpense(expenseCreationViewModel: ExpenseCreationViewModel, currentUser: User?) {
    var group = expenseCreationViewModel.group
    var selectedUsers = expenseCreationViewModel.selectedUsers

    val isDropDownExpanded = remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select people to pay",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { isDropDownExpanded.value = true }
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = "Select payers",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown icon"
                )
            }

            DropdownMenu(
                expanded = isDropDownExpanded.value,
                onDismissRequest = { isDropDownExpanded.value = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                group?.users?.forEach { user ->
                    val isSelected = user in selectedUsers
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = user.name)
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            if(isSelected) selectedUsers.remove(user)
                            else selectedUsers.add(user)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(128.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseView() {
    MaterialTheme {
        AddExpenseView()
    }
}