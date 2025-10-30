package com.example.moneygrab.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



@Preview(showBackground = true)
@Composable
fun view() {
    Scaffold (modifier = Modifier.fillMaxSize()){ innerPadding ->
        GroupCreationView(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun GroupCreationView(modifier: Modifier = Modifier) {
    Column (
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(50.dp)
    ) {
        ElevatedButton (
            modifier = Modifier.fillMaxWidth(0.75f).height(200.dp),
            shape = MaterialTheme.shapes.large,
            onClick = {
                println("Add image button clicked")
            }
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
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
        var groupName by remember { mutableStateOf("") }
        OutlinedTextField (
            value = groupName,
            label = { Text("Name") },
            onValueChange = {groupName = it},
            modifier = Modifier
                .fillMaxWidth(0.75f)
        )

        var personName by remember { mutableStateOf("") }
        OutlinedTextField (
            value = personName,
            label = {Text("People")},
            onValueChange = {personName = it},
            modifier = Modifier
                //.fillMaxHeight()
                .fillMaxWidth(0.75f)
        )

        /*Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(0.75f)
        ) {

            Button(
                onClick = { println("Add person button clicked") },
                shape = MaterialTheme.shapes.extraSmall,
                modifier = Modifier.fillMaxHeight()
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Person"
                )
            }
        }*/
        Button(
            onClick = { println("Create group button clicked") },
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Create Group"
            )
        }
    }
}