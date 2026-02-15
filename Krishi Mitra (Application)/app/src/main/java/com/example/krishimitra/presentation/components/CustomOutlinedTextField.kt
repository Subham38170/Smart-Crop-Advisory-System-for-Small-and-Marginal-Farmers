package com.example.krishimitra.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.krishimitra.Constants
import com.example.krishimitra.R


@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    supportingText: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean
) {


    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth(),
        placeholder = {
            Text(
                text = label,
                fontSize = Constants.TEXT_FIELD_DEFAULT_SIZE,
                fontWeight = FontWeight.Bold
            )
        },
        readOnly = readOnly,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorResource(id = R.color.red),
            focusedBorderColor = colorResource(id = R.color.red),
            focusedLabelColor = colorResource(R.color.red),
            unfocusedTextColor = colorResource(id = R.color.red),
            cursorColor = colorResource(id = R.color.red),
            focusedContainerColor = colorResource(id = R.color.white),
            unfocusedContainerColor = colorResource(id = R.color.white),
            focusedTrailingIconColor = colorResource(id = R.color.red),
            unfocusedTrailingIconColor = colorResource(id = R.color.red),
            focusedLeadingIconColor = colorResource(id = R.color.red),
            unfocusedLeadingIconColor = colorResource(id = R.color.red),
            focusedPlaceholderColor = colorResource(id = R.color.red),
            unfocusedPlaceholderColor = colorResource(id = R.color.red),
            focusedTextColor = colorResource(id = R.color.red)
        ),
        supportingText = {
            if(supportingText.isNotEmpty()){
                Text(
                    text = supportingText,
                    color = Color.Red
                )
            }
        },
        shape = RoundedCornerShape(Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE)

    )

}