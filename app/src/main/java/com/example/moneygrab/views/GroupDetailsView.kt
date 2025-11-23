package com.example.moneygrab.views

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authentication.CurrentUser
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch

class GroupDetailsViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var chosenUsers = mutableStateListOf<User>()
    var initialUsers = mutableStateListOf<User>()
    var initialDescription = ""
    var searchResult = mutableStateListOf<User>()
    var description = mutableStateOf("")

    var user: User? = null
    var group: Group by mutableStateOf(
        Group(
            id = -1,
            name = "",
            users = mutableSetOf(),
            expenses = mutableListOf(),
            isClosed = false,
            messages = mutableListOf(),
            description = "",
        )
    )

    var errorHappened = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var changeMade = mutableStateOf(false)

    fun setUser(context: Context) {
        CurrentUser(context).getUser()?.let {
            user = it
        }
    }
    fun getSuggestedUsers(searchString: String) {
        viewModelScope.launch {
            val response = try {
                api.getSuggestedUsers(searchString)
            } catch (e: Exception) {
                null
            }

            if (response?.code() == 200) {
                response.body()?.let {
                    searchResult.clear()
                    searchResult.addAll(it)
                    searchResult.removeAll(chosenUsers)
                }
            }
        }
    }

    fun fetchGroupData(groupId: Int) {
        viewModelScope.launch {
            val groupResponse = try {
                api.getGroup(groupId)
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                errorHappened.value = true
                null
            }

            if (!(groupResponse?.isSuccessful ?: false)) {
                errorMessage.value = "The phone number or password is incorrect"
                errorHappened.value = true
            } else {
                groupResponse.body()?.let {
                    println("Group: ${it}")
                    group = it
                }
            }

            val userResponse = try {
                api.getUsersInGroup(groupId)
            } catch (e: Exception) {
                println(e.message)
                null
            }

            userResponse?.body()?.let {
                group.users = it
                chosenUsers.clear()
                chosenUsers.addAll(it)
                description.value = group.description

                // Capture initial values
                initialUsers.addAll(chosenUsers)
                initialDescription = group.description
            }
        }
    }

    fun saveGroup(navigation: () -> Unit) {
        viewModelScope.launch {
            group.description = description.value
            group.users.addAll(chosenUsers)

            var response = try {
                api.updateGroup(group.id, APIEndpoints.GroupData(group.name, group.description, chosenUsers))
            } catch (e: Exception) {
                println(e.message)
                null
            }

            response?.let {
                if (it.isSuccessful) {
                    navigation()
                }
            }
        }
    }

    fun checkDataChanged() {
        var usersChanged = checkUsersChanged()
        var textChanged = (initialDescription != description.value)
        if (usersChanged || textChanged) {
            changeMade.value = true
        } else {
            changeMade.value = false
        }
    }

    private fun checkUsersChanged(): Boolean {
        var res = false

        if (!initialUsers.containsAll(chosenUsers)) res = true
        if (!chosenUsers.containsAll(initialUsers)) res = true

        return res
    }
}

@Composable
fun GroupDetailsView(
    groupId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    var groupDetailsViewModel: GroupDetailsViewModel = viewModel()
    var changeMade = groupDetailsViewModel.changeMade
    var description = groupDetailsViewModel.description

    LaunchedEffect(groupId) {
        groupDetailsViewModel.setUser(context)
        groupDetailsViewModel.fetchGroupData(groupId)
    }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        TopDetailsBar(
            groupName = groupDetailsViewModel.group.name,
            onBack = onBack
        )
        OutlinedTextField(
            value = description.value,
            onValueChange = {
                description.value = it
                groupDetailsViewModel.checkDataChanged()
                },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(0.85f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        ManageParticipantsBar(groupDetailsViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        Button (
            onClick = { groupDetailsViewModel.saveGroup(onBack) },
            shape = MaterialTheme.shapes.small,
            enabled = changeMade.value
        ) {
            Text(
                text = "Save Group",
                fontSize = 20.sp
                )
        }
    }
}

@Composable
fun TopDetailsBar(groupName: String, onBack: () -> Unit) {
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
            }
            Text(
                text = groupName,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
fun ManageParticipantsBar(groupDetailsViewModel: GroupDetailsViewModel) {
    Column (
        modifier = Modifier.fillMaxHeight(0.75f),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        var searchString by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        val chosenUsers = groupDetailsViewModel.chosenUsers
        val searchResult = groupDetailsViewModel.searchResult

        Box (
            modifier = Modifier
        ) {
            OutlinedTextField (
                value = searchString,
                label = { Text("People") },
                modifier = Modifier
                    .fillMaxWidth(0.85f),
                onValueChange = {
                    searchString = it
                    groupDetailsViewModel.getSuggestedUsers(searchString)
                    groupDetailsViewModel.checkDataChanged()

                    if (searchResult.isNotEmpty() && !expanded) {
                        expanded = true
                    }
                }
            )

            DropdownMenu (
                expanded = expanded,
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(300.dp)
            ) {
                searchResult.forEach { user ->
                    DropdownMenuItem (
                        text = {
                            Column {
                                Text (
                                    text = user.name
                                )
                                Text (
                                    color = Color.Gray,
                                    text = user.phoneNumber.toString()
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            chosenUsers.add(user)
                            searchResult.remove(user)
                            groupDetailsViewModel.checkDataChanged()
                        }
                    )
                }
            }
        }

        PeopleList (
            users = chosenUsers,
            onClick = { user ->
                chosenUsers.remove(user)
                groupDetailsViewModel.checkDataChanged()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GroupDetailsPreview() {
    MoneyGrabTheme {
        GroupDetailsView(1, onBack = {println("back")})
    }
}

