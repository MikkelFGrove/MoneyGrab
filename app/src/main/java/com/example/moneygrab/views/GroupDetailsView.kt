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
    var searchResult = mutableStateListOf<User>()

    var user: User? = null
    var group: Group by mutableStateOf(
        Group(
            id = -1,
            name = "",
            users = emptySet(),
            expenses = mutableListOf(),
            tabClosed = false,
            messages = mutableListOf(),
            description = ""
        )
    )

    var errorHappened = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var groupName = mutableStateOf("")

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
                }
            }
        }
    }

    fun fetchGroupData(groupId: Int) {
        viewModelScope.launch {
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
                    group = it
                }
            }

        }
    }
}

@Composable
fun GroupDetailsView(
    groupId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
){
    val context = LocalContext.current
    val groupDetailsViewModel: GroupDetailsViewModel = viewModel()

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
            value = groupDetailsViewModel.group.description, // TODO change to actually fetch the description once backend has been set up
            onValueChange = {},
            label = { Text("Description") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(0.85f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        ManageParticipantsBar(groupDetailsViewModel)

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
                modifier = Modifier.align(Alignment.Center)
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
                        }
                    )
                }
            }
        }

        PeopleList (
            users = chosenUsers,
            onClick = { user -> chosenUsers.remove(user) }
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

