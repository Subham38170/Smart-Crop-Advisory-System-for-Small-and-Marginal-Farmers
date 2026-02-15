package com.example.krishimitra.presentation.auth_screen


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.krishimitra.Constants
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.farmer_data.UserDataModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    moveToSignUpScreen: () -> Unit,
    signIn: (UserDataModel) -> Unit,
    authState: AuthState,
    context: Context
) {
    var password by rememberSaveable { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    val gmailRegex = Regex("^[a-zA-Z0-9._%+-]+@gmail\\.com$")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.email),
                    fontSize = Constants.TEXT_FIELD_DEFAULT_SIZE,
                    fontWeight = FontWeight.Bold                )
            },
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
                if (email.isNotEmpty() && !email.matches(gmailRegex)) {
                    Text(
                        text = stringResource(id = R.string.enter_a_valid_gmail_address),
                        color = Color.Red
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = ""
                )
            },
            shape = RoundedCornerShape(Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(
                mask = '*'
            ),
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.password),
                    fontSize = Constants.TEXT_FIELD_DEFAULT_SIZE,
                    fontWeight = FontWeight.Bold
                )
            },
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
            trailingIcon = {
                IconButton(
                    onClick = { showPassword = !showPassword }
                ) {
                    Icon(
                        painter = if (showPassword) painterResource(id = R.drawable.outline_visibility_off_24) else painterResource(
                            id = R.drawable.outline_visibility_24
                        ),
                        contentDescription = if (showPassword) "Hide password" else "Show password"
                    )

                }
            },
            supportingText = {
                if (password.isNotEmpty() && password.length < 8) {
                    Text(
                        text = stringResource(R.string.password_must_be_8_characters_long),
                        color = Color.Red
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = ""
                )
            }



        )



        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {
                if (password.length < 8 || !email.matches(gmailRegex)
                ) {
                    Toast.makeText(context, "Enter valid details", Toast.LENGTH_SHORT).show()
                } else if (authState.isSignLoading) {
                    Toast.makeText(context, "Wait...", Toast.LENGTH_SHORT).show()
                } else {
                    signIn(
                        UserDataModel(
                            email = email,
                            password = password
                        )
                    )

                }
            },
            shape = RoundedCornerShape(Constants.TEXT_FIELD_ROUNDED_CORNER_SIZE),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.red),
                contentColor = Color.White
            )

        ) {
            if (authState.isSignLoading) {
                CircularProgressIndicator(
                    color = Color.White
                )
            } else {
                Text(
                    text = stringResource(id = R.string.login),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.dont_have_account),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextButton(
                onClick = moveToSignUpScreen
            ) {
                Text(
                    text = stringResource(id = R.string.create),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.yellow)


                )
            }
        }

    }

}

