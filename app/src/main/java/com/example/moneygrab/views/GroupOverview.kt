package com.example.moneygrab.views

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authentication.CurrentUser
import com.example.debtcalculator.data.Group
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class GroupPageViewModel : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()
    var groups = mutableStateListOf<Group>()
    var errorHappened = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    var amountsOwed = mutableStateMapOf<Int, Float>()
    var profilePicture = mutableStateOf<ImageBitmap?>(null)

    @OptIn(ExperimentalEncodingApi::class)
    fun loadImage(img: String) {
        val decodedString: ByteArray = Base64.decode(img)
        if (decodedString.isNotEmpty()) {
            profilePicture.value =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    .asImageBitmap()
        }
    }

    fun fetchGroups(userId: Int) {
        println("outer1")
        viewModelScope.launch {
            isLoading.value = true
            println("outer2")
            val response = try {
                api.getGroups(userId)
            } catch (e: Exception) {
                errorMessage.value = "An error has occurred"
                println(e.message)
                isLoading.value = false
                null
            }
            println(response?.body())
            isLoading.value = false

            if (response?.code() == 523) {
                errorMessage.value = "Could not fetch groups due to network error"
                errorHappened.value = true
            } else if (!(response?.isSuccessful ?: false)) {
                errorMessage.value = "Internal server error"
                errorHappened.value = true
                println(errorMessage)
            } else {
                response.body()?.let { list ->
                    groups.clear()
                    groups.addAll(list)
                    println(groups)
                    amountsOwed.clear()
                    list.forEach { group ->
                        fetchAmountsOwedForGroup(group.id, userId)
                    }
                }
            }
        }
    }

    private fun fetchAmountsOwedForGroup(groupId: Int, userId: Int) {
        viewModelScope.launch {
            val response = try {
                api.getAmountOwed(groupId, userId)
            } catch (e: Exception) {
                println(e.message)
                null
            }

            response?.body()?.let {
                amountsOwed[groupId] = it.amount
            }
        }
    }
}



@Composable
fun Top(onProfilePressed: () -> Unit, groupPageViewModel: GroupPageViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        //color = MaterialTheme.colorScheme.primary
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Groups",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .clickable(onClick = onProfilePressed)
                        .background(MaterialTheme.colorScheme.background)

                ) {
                    var painter: Painter? = null

                    groupPageViewModel.profilePicture.value?.let {
                        painter = BitmapPainter(it)
                    }

                    if (painter == null) painter = painterResource(id = R.drawable.ic_profile_placeholder)

                    Image(
                        painter = painter,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(),
                thickness = 0.4.dp,
                color = Color.Gray
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalEncodingApi::class)
@Composable
fun GroupPage(onGroupClicked: (Group) -> Unit, onProfileClicked: () -> Unit, onCreateGroupClicked: () -> Unit, groupPageViewModel: GroupPageViewModel = viewModel(), modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val currentUser = remember { CurrentUser(context) }
    var user = currentUser.getUser()
    val amountsOwed = groupPageViewModel.amountsOwed
    val groups: List<Group> = groupPageViewModel.groups

    LaunchedEffect(user){
        user?.let {
            println("User ID: ${it.id}")
            groupPageViewModel.fetchGroups(it.id)
            it.image?.let { img ->
                groupPageViewModel.loadImage(img)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Top(onProfilePressed = onProfileClicked, groupPageViewModel)
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
                    if (groupPageViewModel.isLoading.value) {
                        Spacer(Modifier.height(32.dp))
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Fetching Groups")
                    }
                    if (groupPageViewModel.errorHappened.value) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(groupPageViewModel.errorMessage.value, color = Color.Red)
                    }
                }

                items(groups, key = { it.id }) { group ->
                    val amountOwedForGroup = amountsOwed[group.id] ?: 0f
                    GroupCard(
                        name = group.name,
                        amountOwed = amountOwedForGroup,
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            onGroupClicked(group)
                            println(group.toString())
                        },
                        image = group.image
                    )
                }
            }
        }
        
        // Sticky "+" button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            onClick = onCreateGroupClicked,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)

        ) {
            Text(
                text = "+",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@OptIn(ExperimentalEncodingApi::class)
@Composable
fun GroupCard(
    name: String,
    amountOwed: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    image: String?
) {
    val amountColor = when {
        amountOwed < 0 -> MaterialTheme.colorScheme.error
        amountOwed > 0 -> MaterialTheme.colorScheme.primary
        else -> Color.Black
    }

    val backgroundColor = MaterialTheme.colorScheme.secondaryContainer

    Card(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(180.dp)
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            image?.let {
                val decodedString: ByteArray = Base64.decode(image)
                if (decodedString.isNotEmpty()) {
                    val bitmap =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                            .asImageBitmap()
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Image of the group",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(120.dp).align(Alignment.TopCenter)
                    )
                }
            }

            Text(
                text = name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
            )

            Text(
                text = "${amountOwed} DKK",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = amountColor,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
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
            onGroupClicked = {},
        )
    }
}
