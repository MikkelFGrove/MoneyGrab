package com.example.moneygrab.views


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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.Message
import com.example.debtcalculator.data.User
import com.example.moneygrab.R
import com.example.authentication.CurrentUser
import kotlin.time.TimeSource

/*
@Composable
fun View() {
    val group = TestData()

    Scaffold (modifier = Modifier.fillMaxSize()){ innerPadding ->
        AddPayersView(modifier = Modifier.padding(innerPadding), group = group)
    }
}*/


@Composable
fun TestData(): Group {
    val user1 = User(-1, "13241234","test1", null)
    val user2 = User(-1, "23412341","test2", null)
    val user3 = User(-1, "34123412","test3", null)

    val expense = Expense(12.35f, "Bare en test", user1, payers = arrayOf(user1, user2, user3))
    val mark = TimeSource.Monotonic.markNow()
    val messages = Message(user1, "test", mark)

    val group = Group(
        name = "Weekend trip",
        users = setOf(user1, user2, user3),
        expenses = mutableListOf(expense),
        messages = listOf(messages),
        id = 1,
        tabClosed = false
    )
    return group
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
fun AddPayersView(modifier: Modifier = Modifier, groupId: Int, onAddExpense: (Group) -> Unit = {}, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }.getUser()
    val group = fetchGroup(groupId)?: fetchGroups(currentUser).first()
    val isDropDownExpanded = remember { mutableStateOf(false) }
    val users = group?.users?.toList()
    val selectedUsers = remember { mutableStateListOf<User>().apply {addAll(group?.users ?: mutableStateListOf<User>())}}
    val expense = group?.expenses[group.expenses.size-1]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back"
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select people to pay for ${expense?.description}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 12.dp)
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
                users?.forEach { user ->
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

        Button(
            onClick = {
                val updatedExpense = expense?: Expense(
                    amount = 1.toFloat(),
                    description = "TODO()",
                    lender = User(-1, "123123", "TODO()", null),
                    payers = emptyArray()
                ).copy(payers = selectedUsers.toTypedArray())
                val updatedGroup = group?.copy(
                    expenses = group.expenses.apply {
                        this[this.lastIndex] = updatedExpense
                    }
                )
                println(updatedGroup)
                onAddExpense(group)
            },
            modifier = Modifier.padding(horizontal = 14.dp).height(64.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = selectedUsers.isNotEmpty()
        )
        {
            Text(
                text = "Add Expense with ${selectedUsers.size} payer${if (selectedUsers.size != 1) "s" else ""}"
            )
        }

    }

}
@Preview(showBackground = true)
@Composable
fun AddToExpenseView() {
    val group = testData()

    Scaffold (modifier = Modifier.fillMaxSize()){ innerPadding ->
        AddPayersView(modifier = Modifier.padding(innerPadding), groupId = 1)
    }
}

@Composable
fun testData(): Group {
    val user1 = User(-1, "13241234","test1", null)
    val user2 = User(-1, "23412341","test2", null)
    val user3 = User(-1, "34123412","test3", null)

    val expense = Expense(12.35f, "Bare en test", user1, payers = arrayOf(user1, user2, user3))
    val mark = TimeSource.Monotonic.markNow()
    val messages = Message(user1, "test", mark)

    val group = Group(
        name = "Weekend trip",
        users = setOf(user1, user2, user3),
        expenses = mutableListOf(expense),
        messages = listOf(messages),
        id = 1,
        tabClosed = false
    )
    return group
}