package com.example.krishimitra.presentation.feedback_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.krishimitra.Constants
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.feedback.FeedbackData
import com.example.krishimitra.presentation.components.rememberSpeechToTextLauncher
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FeedbackScreen(
    state: FeedbackScreenState,
    onEvent: (FeedbackScreenEvent) -> Unit,
    event: SharedFlow<String>,
    snackbarHostState: SnackbarHostState,
    moveBackToProfileScreen: () -> Unit
) {

    val context = LocalContext.current

    val emptyFeedback = stringResource(id = R.string.feedback_empty)
    val selectUserRating = stringResource(id = R.string.feedback_select_rating)
    var description by rememberSaveable { mutableStateOf("") }
    var issues by rememberSaveable { mutableStateOf("") }
    var improvement by rememberSaveable { mutableStateOf("") }

    var userRating by remember { mutableStateOf(-1) }
    LaunchedEffect(event) {
        event.collectLatest {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.isSending) {
        if (state.isSending == false) {
            userRating = -1
            issues = ""
            improvement = ""
            description = ""
        }
    }
    val descriptionLauncher = rememberSpeechToTextLauncher(
        langCode = state.currLang,

        onResult = { spokenText ->
            description = spokenText
        })

    val issuesLauncher = rememberSpeechToTextLauncher(
        langCode = state.currLang,

        onResult = { spokenText ->
            issues = spokenText
        }
    )

    val improvementLauncher = rememberSpeechToTextLauncher(
        langCode = state.currLang,
        onResult = { spokenText ->
            improvement = spokenText

        }
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.light_green))
            .padding(vertical = 60.dp, horizontal = 8.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(
                        onClick = moveBackToProfileScreen
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Arrow back"
                        )

                    }
                    Text(
                        text = stringResource(id = R.string.feedback),
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            item {
                FeedbackTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    onMicClick = descriptionLauncher,
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxLines = 7,
                    label = stringResource(id = R.string.feedback_description)
                )
            }
            item {
                FeedbackTextField(
                    value = issues,
                    onValueChange = {
                        issues = it
                    },
                    onMicClick = issuesLauncher,
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxLines = 7,
                    label = stringResource(id = R.string.feedback_issues)
                )
            }
            item {
                FeedbackTextField(
                    value = improvement,
                    onValueChange = {
                        improvement = it
                    },
                    onMicClick = improvementLauncher,
                    modifier = Modifier
                        .fillMaxWidth(),
                    maxLines = 7,
                    label = stringResource(id = R.string.feedback_improvement)

                )
            }
            item {
                UserExperienceBox(
                    onClick = {
                        userRating = it
                    },
                    selected = userRating
                )
            }
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.slight_dark_green),
                        contentColor = Color.White
                    ),
                    onClick = {
                        if (description.isEmpty()) {
                            Toast.makeText(context, emptyFeedback, Toast.LENGTH_SHORT).show()
                        } else if (userRating == -1) {
                            Toast.makeText(context, selectUserRating, Toast.LENGTH_SHORT).show()
                        } else {
                            onEvent(
                                FeedbackScreenEvent.sendFeedback(
                                    FeedbackData(
                                        description = description,
                                        improvement = improvement,
                                        issues = issues,
                                        userRating = userRating,
                                        language = state.currLang
                                    )
                                )
                            )
                        }
                    },
                    enabled = !state.isSending
                ) {
                    if (state.isSending) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = stringResource(id = R.string.submit),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

        }


    }
}


@Composable
fun UserExperienceBox(
    selected: Int,
    onClick: (Int) -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.feedback_rating),
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.slight_dark_green),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeedbackButton(
                modifier = Modifier.weight(1f),
                selected = selected == 1,
                emoji = "\uD83D\uDE42",
                label = stringResource(id = R.string.feedback_good),
                onClick = { onClick(1) }
            )

            FeedbackButton(
                modifier = Modifier.weight(1f),
                selected = selected == 2,
                emoji = "\uD83D\uDE10",
                label = stringResource(id = R.string.feedback_avg),
                onClick = { onClick(2) }
            )

            FeedbackButton(
                modifier = Modifier.weight(1f),
                selected = selected == 3,
                emoji = "\uD83D\uDE41",
                label = stringResource(id = R.string.feedback_bad),
                onClick = { onClick(3) }
            )
        }
    }
}

@Composable
fun FeedbackButton(
    modifier: Modifier,
    selected: Boolean,
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
            .background(
                color = if (selected) Color(0xFFE0F7FA) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF00796B) else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            fontSize = 42.sp
        )
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF00796B) else Color.Black
        )
    }
}

@Composable
fun FeedbackTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 2
) {

    Column {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.slight_dark_green),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(bottom = 4.dp)
        )
        OutlinedTextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,

            trailingIcon = {
                IconButton(
                    onClick = onMicClick
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_mic_24),
                        contentDescription = "Mic"
                    )
                }
            },
            minLines = 2,
            maxLines = maxLines,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colorResource(id = R.color.slight_dark_green),
                focusedBorderColor = colorResource(id = R.color.slight_dark_green),
                focusedLabelColor = colorResource(R.color.slight_dark_green),
                unfocusedTextColor = colorResource(id = R.color.slight_dark_green),
                cursorColor = colorResource(id = R.color.slight_dark_green),
                focusedContainerColor = colorResource(id = R.color.white),
                unfocusedContainerColor = colorResource(id = R.color.white),
                focusedTrailingIconColor = colorResource(id = R.color.slight_dark_green),
                unfocusedTrailingIconColor = colorResource(id = R.color.slight_dark_green),
                focusedLeadingIconColor = colorResource(id = R.color.slight_dark_green),
                unfocusedLeadingIconColor = colorResource(id = R.color.slight_dark_green),
                focusedPlaceholderColor = colorResource(id = R.color.slight_dark_green),
                unfocusedPlaceholderColor = colorResource(id = R.color.slight_dark_green),
                focusedTextColor = colorResource(id = R.color.slight_dark_green)
            )
        )
    }
}