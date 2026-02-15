package com.example.krishimitra.presentation.community_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.krishimitra.R

@Composable
fun ComunityMainScreen(
    moveToMessageScreen: (String) -> Unit
) {
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(4.dp)
        ) {
            LazyColumn {
                item {
                    StateItem(
                        onClick = { moveToMessageScreen("Odisha") },
                        name = "Odisha"
                    )
                }
            }
        }

    }
}


@Composable
fun StateItem(
    onClick: () -> Unit,
    name: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(
                enabled = true,
                interactionSource = null,
                onClick = onClick
            )

    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_message_24),
            contentDescription = "",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium
        )


    }


}