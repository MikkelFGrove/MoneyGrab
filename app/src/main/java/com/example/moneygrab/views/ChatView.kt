// ChatScreen.kt
package com.example.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

data class MoneyRequest(val text: String, val isMine: Boolean)

@Composable
fun TopBar(groupName: String) {
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { println("We go back type shi")}) {
                Text("Back")
            }
            Text(
                text = groupName,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Button(onClick = { println("Emils kode :)")}) {
                Text("Calculate")
            }
        }
    }
}

@Composable
fun Bubbles(moneyRequest: MoneyRequest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 10.dp),
        horizontalArrangement = if (moneyRequest.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (moneyRequest.isMine) Color.Blue else Color.Green,
        ) {
            Text(
                text = moneyRequest.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
fun MessagesList(messages: List<MoneyRequest>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        itemsIndexed(messages) { _, msg ->
            Bubbles(moneyRequest = msg)
        }
    }
}

@Composable
fun InputBar(currentText: String, onTextChange: (String) -> Unit, ) {
    Surface (modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp)
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = currentText,
                onValueChange = onTextChange,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                placeholder = {Text ("GET YOUR MONEY UP")}
            )

            Button(
                onClick = {println("MI BOMBACLAT")},
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Text("Send")
            }
        }
    }

}

@Composable
fun ChatScreen(groupName: String) {
    val messages = remember { mutableStateListOf(
        MoneyRequest("Magnus: 3kr (broke ass)", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("You: 30 mil", isMine = true),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
        MoneyRequest("Andreas: 30kr", isMine = false),
    ) }

    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            groupName = groupName,
        )

        MessagesList(
            messages = messages,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        InputBar(
            currentText = inputText,
            onTextChange = { inputText = it },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MaterialTheme {
        ChatScreen(groupName = "Meow")
    }
}
