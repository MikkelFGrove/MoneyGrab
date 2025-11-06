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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moneygrab.ui.theme.MoneyGrabTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.moneygrab.R

@Immutable
data class FrontendGroup(val id: Int, val name: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupPage(onProfileClicked: () -> Unit, groups: List<FrontendGroup>, onCreateGroupClicked: () -> Unit, modifier: Modifier = Modifier) {
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
                        .fillMaxWidth()
                        .background(Color(0xFF1E88E5)), // background for proper stickiness
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)),
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
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable(onClick = onProfileClicked)
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
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Sticky "+" button
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
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
fun GroupCard(name: String, modifier: Modifier = Modifier){
    Card(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.9f)
            .height(100.dp)
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E88E5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    ) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
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
            groups = listOf(
                FrontendGroup(1, "Årsfest"),
                FrontendGroup(2, "Sommerhus"),
                FrontendGroup(3, "Bytur")
            ),
            onCreateGroupClicked = {}
        )
    }
}
