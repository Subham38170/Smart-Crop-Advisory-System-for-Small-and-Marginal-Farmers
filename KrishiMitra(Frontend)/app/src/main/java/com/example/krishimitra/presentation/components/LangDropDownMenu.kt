package com.example.krishimitra.presentation.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.krishimitra.Language

@Composable
fun LangDropDownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    langList: List<Language>,
    onClick: (String) -> Unit,
    modifier: Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        langList.forEach {
            DropdownMenuItem(
                text = {
                    Text(
                        text = it.nativeName
                    )
                },
                onClick = {
                    onClick(it.code)
                }
            )
        }

    }
}