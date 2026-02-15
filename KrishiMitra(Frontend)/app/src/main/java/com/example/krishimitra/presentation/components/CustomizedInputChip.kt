package com.example.krishimitra.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun CustomizedInputChip(
    isSelected: Boolean,
    onSelect: () -> Unit,
    label: String,
    onDeselect: () -> Unit
) {

    FilterChip(
        selected = isSelected,
        onClick = onSelect,
        label = {
            Text(
                text = label
            )
        },
        trailingIcon = {

            if (isSelected) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Localized description",
                    modifier = Modifier
                        .size(InputChipDefaults.AvatarSize)
                        .clickable(
                            enabled = true,
                            onClick = onDeselect
                        )
                )
            }

        }

    )
}