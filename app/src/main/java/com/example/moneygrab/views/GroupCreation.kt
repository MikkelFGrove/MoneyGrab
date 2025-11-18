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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.moneygrab.APIEndpoints
import com.example.moneygrab.R
import com.example.moneygrab.RetrofitClient
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import kotlinx.coroutines.launch
import retrofit2.Response

data class User (
    val name: String,
    val phoneNumber: Number
)

class GroupViewModel() : ViewModel() {
    private val api: APIEndpoints = RetrofitClient.getAPI()

    var chosenUsers = mutableStateListOf<User>()
    var searchResult = mutableStateListOf<User>()
    var groupName = mutableStateOf("")
    var image = mutableStateOf<Bitmap?>(null)
    var errorCreatingGroup = mutableStateOf(false)
    var errorMessage = mutableStateOf("")

    fun storeImage(img: Bitmap?) {
        image.value = img
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

    fun createGroup(navigation: () -> Unit) {
        viewModelScope.launch {
            val response = try {
                api.createGroup(APIEndpoints.GroupData(groupName.value, chosenUsers))
            } catch (e: Exception) {
                errorMessage.value = "A network error has occurred"
                errorCreatingGroup.value = true
                null
            }

            if (!(response?.isSuccessful ?: false)) {
                errorMessage.value = "An error has occurred on the server"
                errorCreatingGroup.value = true
            } else {
                navigation()
            }
        }
    }
}

@Composable
fun GroupCreationView(
    onBack: () -> Unit,
    onCreateGroupNavigation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupViewModel: GroupViewModel = viewModel()
    var errorCreatingGroup by groupViewModel.errorCreatingGroup
    var groupName by groupViewModel.groupName
    var errorMessage by groupViewModel.errorMessage

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

            Spacer(modifier = Modifier.width(48.dp))

        }

        Spacer(modifier = Modifier.height(24.dp))

        ImageButton(groupViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = groupName,
            label = { Text("Name") },
            onValueChange = { groupName = it },
            modifier = Modifier
                .fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AccountSearchBar(groupViewModel)

        Spacer(modifier = Modifier.height(24.dp))

        if (errorCreatingGroup) {
            ErrorCard(errorMessage)

            Spacer(modifier = Modifier.height(6.dp))
        }

        CreateButton(groupViewModel, onCreateGroupNavigation)
    }
}


@Composable
fun CreateButton(groupViewModel: GroupViewModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 20.dp)
            .background(Color.Transparent),
    ) {
        Button (
            onClick = {
                groupViewModel.createGroup(onClick)
            },
            shape = MaterialTheme.shapes.small,
        ) {
            Text (
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

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageUri = uri
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(imageUri)
                .target { result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    groupViewModel.storeImage(bitmap)
                }
                .build()
            loader.enqueue(req)
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
    Card (
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
                Text (
                    text = user.name,
                )
                Text (
                    text = user.phoneNumber.toString(),
                    color = Color.Gray
                )
            }
            FilledIconButton (
                onClick = { onClick(user) },
                colors = IconButtonColors(
                    Color.Transparent,
                    MaterialTheme.colorScheme.error,
                    Color.Transparent,
                    Color.Transparent
                ),
                modifier = Modifier.padding(5.dp, 5.dp)
            ) {
                Icon (
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove Person",
                )
            }
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
        var expanded by remember { mutableStateOf(false) }
        val chosenUsers = groupViewModel.chosenUsers
        val searchResult = groupViewModel.searchResult

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
                    groupViewModel.getSuggestedUsers(searchString)

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

@Preview
@Composable
fun GroupCreationPreview() {
    MoneyGrabTheme {
        GroupCreationView({}, {})
    }
}