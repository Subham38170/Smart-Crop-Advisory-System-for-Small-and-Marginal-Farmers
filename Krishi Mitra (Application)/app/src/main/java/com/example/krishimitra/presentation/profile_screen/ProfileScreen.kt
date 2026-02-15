package com.example.krishimitra.presentation.profile_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.Constants
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.farmer_data.UserDataModel

@Composable
fun ProfileScreen(
    state: ProfileScreenState,
    logOut: () -> Unit,
    moveToFeedbackScreen: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.light_green))

            .padding(top = 40.dp)
            .padding(8.dp)
    ) {
        LazyColumn {

            item {
                ProfileBox(
                    userData = state.userData
                )

            }
            item {

                ProfileScreenButton(
                    onClick = {
                        moveToFeedbackScreen()
                    },
                    text = stringResource(R.string.feedback),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            item {
                ProfileScreenButton(
                    onClick = logOut,
                    text = stringResource(id = R.string.logout),
                    modifier = Modifier
                        .fillMaxWidth()
                )

            }
        }
    }
}

@Composable
fun ProfileScreenButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.slight_dark_green),
            contentColor = Color.White
        ),
        modifier = modifier,
        shape = RoundedCornerShape(Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


@Composable
fun ProfileBox(
    userData: UserDataModel
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE))
            .background(colorResource(id = R.color.slight_dark_green))
            .border(1.dp,color = colorResource(id = R.color.yellow))
            .height(180.dp)
            .padding(16.dp)
    ) {

        Box(
            modifier = Modifier
                .padding(end = 16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.outline_person_24),
                contentDescription = "Image",
                modifier = Modifier
                    .size(100.dp)
                    .border(
                        2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
        Column {
            Text(
                text = userData.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "+91 " + userData.mobileNo,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1
            )

            Row {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color.White
                )
                Text(
                    text = userData.village + ", " + userData.district + ", " + userData.state,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 3
                )
            }

        }
    }


}