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
import com.example.debtcalculator.data.Expense
import com.example.debtcalculator.data.Group
import com.example.debtcalculator.data.User
import com.example.moneygrab.CurrentUser
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient

@Immutable
data class FrontendGroup(val id: Int, val name: String)

fun fetchGroups(user: User?): List<Group>{
    /*val api = RetrofitClient().api
    return try {
        api.fetchGroups(user)
    } catch (e: Exception){
        emptyList()
    }*/
    val userA = User(phoneNumber = "11111111", name = "Alice", image = null)
    val userB = User(phoneNumber = "22222222", name = "Bob", image = null)
    val userC = User(phoneNumber = "33333333", name = "Charlie", image = null)
    val userD = User(phoneNumber = "44444444", name = "Diana", image = null)

    val expense1 = Expense(
        amount = 120f,
        description = "Dinner",
        lender = userA,
        payers = arrayOf(userA, userB)
    )

    val expense2 = Expense(
        amount = 90f,
        description = "Cinema",
        lender = userC,
        payers = arrayOf(userC, userD)
    )

    val expense3 = Expense(
        amount = 300f,
        description = "Weekend trip",
        lender = userB,
        payers = arrayOf(userA, userB, userC, userD)
    )

    val group1 = Group(
        name = "Friends",
        users = setOf(userA, userB),
        expenses = mutableListOf(expense1),
        messages = emptyArray(), // Empty as requested
        id = 1
    )

    val group2 = Group(
        name = "Family",
        users = setOf(userC, userD),
        expenses = mutableListOf(expense2),
        messages = emptyArray(),
        id = 2
    )

    val group3 = Group(
        name = "Work Trip",
        users = setOf(userA, userB, userC, userD),
        expenses = mutableListOf(expense3),
        messages = emptyArray(),
        id = 3
    )

    return listOf(group1, group2, group3)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupPage(onGroupClicked: (Group) -> Unit, onProfileClicked: () -> Unit, onCreateGroupClicked: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }
    val groups = fetchGroups(currentUser.getUser())
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
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
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
