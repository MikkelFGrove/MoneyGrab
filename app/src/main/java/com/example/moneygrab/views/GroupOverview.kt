package com.example.moneygrab.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.debtcalculator.data.Group
import com.example.authentication.CurrentUser
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import kotlinx.coroutines.launch

@Immutable
data class FrontendGroup(val id: Int, val name: String)

class GroupPageViewModel() : ViewModel(){
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var groups = mutableStateListOf<Group>()
    var errorHappened = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    fun fetchGroups(userId: Int) {
        println("outer1")
        viewModelScope.launch {
            println("outer2")
            val response = try {
                api.getGroups(userId)
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                println(e.message)
                null
            }
            println(response?.body())

            if (!(response?.isSuccessful ?: false)){
                errorMessage.value = "An error has occurred"
                errorHappened.value = true
                println(errorMessage)
            } else {
                response.body()?.let { list ->
                    groups.clear()
                    groups.addAll(list)
                    println(groups)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupPage(onGroupClicked: (Group) -> Unit, onProfileClicked: () -> Unit, onCreateGroupClicked: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }
    var user = currentUser.getUser()

    val groupPageViewModel: GroupPageViewModel = viewModel()


    LaunchedEffect(user){
        user?.let {
            println("User ID: ${it.id}")
            groupPageViewModel.fetchGroups(it.id)
        }
    }
    val groups: List<Group> = groupPageViewModel.groups

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            stickyHeader {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart= 12.dp, topEnd= 12.dp, bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Groups",
                            fontSize = 45.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable(onClick = onProfileClicked)
                                .background(color = MaterialTheme.colorScheme.background)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_profile_placeholder),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }
                        Spacer(modifier= Modifier.size(10.dp))
                    }
                }
            }

            items(groups, key = { it.id }) { group ->
                GroupCard(
                    name = group.name,
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = { onGroupClicked(group)
                    println(group.toString())}
                )
            }
        }

        // Sticky "+" button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            onClick = onCreateGroupClicked,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp) // matches your previous spacing
        ) {
            Text(
                text = "+",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun GroupCard(name: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}){
    Card(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.9f)
            .height(100.dp)
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    ) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GroupPreview() {
    MoneyGrabTheme {
        GroupPage(
            onProfileClicked = {},
            onCreateGroupClicked = {},
            onGroupClicked = {}
        )
    }
}
