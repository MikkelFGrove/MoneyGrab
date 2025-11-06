package com.example.moneygrab.views


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.moneygrab.RetrofitClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

data class User (
    val name: String,
    val phoneNumber: Number
)

class GroupViewModel() : ViewModel() {
    var chosenUsers = mutableStateListOf<User>()
    var searchResult = mutableStateListOf<User>()
    var groupName = mutableStateOf("")
    var image = mutableStateOf<Bitmap?>(null)

    fun getSuggestedUsers(searchString: String) {
        searchResult.clear()
        searchResult.addAll(
            mutableListOf(
                User(searchString, 12345678),
                User(searchString, 87654321),
                User(searchString, 12345678),
                User(searchString, 87654321),
                User(searchString, 12345678),
                User(searchString, 87654321),
                User(searchString, 12345678),
                User(searchString, 87654321),
                User(searchString, 12345678),
                User(searchString, 87654321),
                User(searchString, 12345678),
                User(searchString, 87654321),
            )
        )
        /*viewModelScope.launch {
            val response = retrofitClient.api.getSuggestedUsers(searchString)
            searchResult.addAll(response)
        }*/
    }

    fun printChosen() {
        println(chosenUsers)
    }
}

@Composable
fun GroupCreationView(modifier: Modifier = Modifier, groupViewModel: GroupViewModel = viewModel()) {
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ImageButton(groupViewModel)

        var groupName by groupViewModel.groupName
        OutlinedTextField (
            value = groupName,
            label = { Text("Name") },
            onValueChange = { groupName = it },
            modifier = Modifier
                .fillMaxWidth(0.85f)
        )

        AccountSearchBar(groupViewModel)

        CreateButton(groupViewModel)
    }
}

@Composable
fun CreateButton(groupViewModel: GroupViewModel) {
    Card (
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 20.dp)
            .background(Color.Transparent),
    ) {
        Button(
            onClick = { println(groupViewModel.groupName.value) },
            shape = MaterialTheme.shapes.small,
        ) {
            Text(
                text = "Create Group",
                fontSize = 4.em,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun ImageButton(groupViewModel: GroupViewModel) {
    var imageUri: Uri? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    var image by groupViewModel.image

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(imageUri)
                .target { result ->
                     image = (result as BitmapDrawable).bitmap
                }
                .build()

            val disposable = loader.enqueue(req)
        } else {
            println("Failed")
        }
    }

    Button (
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(200.dp),
        contentPadding = PaddingValues(0.dp, 0.dp),
        colors = ButtonColors(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary, Color.Transparent, Color.Transparent),
        shape = MaterialTheme.shapes.large,
        onClick = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
    ) {
        if (imageUri != null) {
            val painter = rememberAsyncImagePainter(
                ImageRequest
                    .Builder(LocalContext.current)
                    .data(data = imageUri)
                    .build()
            )
            Image (
                painter = painter,
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Image"
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Image"
            )
        }

    }
}

@Composable
fun PeopleList(users: SnapshotStateList<User>, onClick: (User) -> Unit) {
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        itemsIndexed(items = users) { i, user ->
            PeopleCard(user, { onClick(user) })
        }
    }
}

@Composable
fun PeopleCard(user: User, onClick: (User) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.85f),
        shape = MaterialTheme.shapes.small,

        ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(15.dp, 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = user.name,
                )
                Text(
                    text = user.phoneNumber.toString(),
                    color = Color.Gray
                )
            }
            FilledIconButton(
                onClick = { onClick(user) },
                colors = IconButtonColors(
                    Color.Transparent,
                    Color.Red,
                    Color.Transparent,
                    Color.Transparent
                ),
                modifier = Modifier.padding(5.dp, 5.dp)
            ) {
                Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove Person",
            ) }
        }
    }
}

@Composable
fun AccountSearchBar(groupViewModel: GroupViewModel) {
    Column (
        modifier = Modifier.fillMaxHeight(0.75f),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        var searchString by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(true) }
        val focusRequester = remember { FocusRequester() }
        val chosenUsers = groupViewModel.chosenUsers
        val searchResult = groupViewModel.searchResult

        Box(
            modifier = Modifier
        ) {
            OutlinedTextField (
                value = searchString,
                label = { Text("People") },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .focusRequester(focusRequester),
                onValueChange = {
                    searchString = it
                    groupViewModel.getSuggestedUsers(it)

                    focusRequester.requestFocus()

                    if (searchResult.isNotEmpty() && !expanded) {
                        expanded = true
                    }
                }
            )

            DropdownMenu (
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(300.dp)
            ) {
                searchResult.forEach { user ->
                    DropdownMenuItem(
                        text = {
                            Column (
                            ) {
                                Text(
                                    text = user.name
                                )
                                Text(
                                    color = Color.Gray,
                                    text = user.phoneNumber.toString()
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            chosenUsers.add(user)
                            searchResult.remove(user)
                            groupViewModel.printChosen()
                        }
                    )
                }
            }
        }

        PeopleList(
            users = chosenUsers,
            onClick = { user -> chosenUsers.remove(user) }
        )
    }
}