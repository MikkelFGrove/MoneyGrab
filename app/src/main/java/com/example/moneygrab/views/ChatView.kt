// ChatScreen.kt
package com.example.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

data class MoneyRequest(val text: String, val isMine: Boolean)

@Composable
fun TopBar(groupName: String, calculatedSum: Double) {
    var color: Color
    if (calculatedSum < 0){
        color = Color.Red
    } else if(calculatedSum > 0) {
        color = Color.Green
    } else {
        color = Color.White
    }
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { println("We go back type shi")}) {
                    Text("Back")
                }
                Button(
                    onClick = { println("Go to pay!")},
                    contentPadding = PaddingValues(
                        start = 4.dp,
                        top = 1.dp,
                        end = 4.dp,
                        bottom = 1.dp,
                    ),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        contentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    ),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 3.dp, minHeight = 3.dp)

                ) {
                    Text(calculatedSum.toString() +  "DKK", color = color)
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
fun Bubbles(moneyRequest: MoneyRequest) {
    val colors = MaterialTheme.colorScheme
    val bubbleColor = if (moneyRequest.isMine) {
        colors.primary
    } else {
        colors.primary.copy(alpha = 0.2f)
    }

    val textColor = if (moneyRequest.isMine) {
        Color.White
    } else {
        Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 10.dp),
        horizontalArrangement = if (moneyRequest.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 2.dp
        ) {
            Text(
                text = moneyRequest.text,
                color = textColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
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
        Button(
            onClick = {println("MI BOMBACLAT")},
            modifier = Modifier
                .padding(start= 50.dp, end = 50.dp, top = 5.dp, bottom = 5.dp),
            shape = RoundedCornerShape(5.dp)

        ) {
            Text("Add Expense")

        }
    }

}

@Composable
fun ChatScreen(groupName: String, messages: List<MoneyRequest>, calculatedSum: Double) {
    var inputText by remember { mutableStateOf("") }
    messages.toMutableStateList()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            groupName = groupName,
            calculatedSum = calculatedSum
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
        ChatScreen(groupName = "Meow", messages = remember {mutableStateListOf(
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
        )}, calculatedSum = 0.toDouble())
    }
}
