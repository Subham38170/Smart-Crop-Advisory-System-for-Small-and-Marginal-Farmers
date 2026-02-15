package com.example.krishimitra.presentation.buy_sell_screen

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.krishimitra.R
import com.example.krishimitra.domain.model.crops_data.CropModel
import com.example.krishimitra.domain.model.farmer_data.UserDataModel
import com.example.krishimitra.presentation.components.CustomOutlinedTextField
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

data class SellScreenState(
    val cropList: List<CropModel> = emptyList(),
    val isLoading: Boolean = false,
    val updateAddCropModel: CropModel? = null,
    val userData: UserDataModel? = null,
    val isUploading: Boolean = false,
    val isDeleting: Boolean = false


)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SellScreen(
    state: SellScreenState,
    sellCrop: (CropModel) -> Unit,
    onEvent: (BuySellScreenEvent)-> Unit
) {

    var showDialog by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).diskCache {
        DiskCache.Builder().directory(context.cacheDir.resolve("offline_images"))
            .maxSizeBytes(100L * 1024L * 1024).build()
    }.build()




    LaunchedEffect(state.isUploading) {
        if (state.isUploading == false) {
            showDialog = false
        }
    }

    if (showDialog) {

        SellScreenAlertDialog(
            onDismissRequest = {
                showDialog = !showDialog
            },
            isUploading = state.isUploading,
            sellCrop = sellCrop
        )

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        Column {
            Text(
                text = stringResource(id = R.string.recently_added),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(8.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.cropList) { item ->
                    CropSellDataItem(
                        crop = item,
                        context = context,
                        imageLoader = imageLoader,
                        onUpdateClick = {},
                        onRemoveClick = {
                            onEvent(BuySellScreenEvent.deleteSellCrop(item))
                        }
                    )

                }
            }

        }
        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            containerColor = colorResource(id = R.color.slight_dark_green),
            contentColor = Color.White,
            modifier = Modifier
                .padding(bottom = 148.dp, end = 20.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SellScreenAlertDialog(
    onDismissRequest: () -> Unit,
    isUploading: Boolean,
    sellCrop: (CropModel) -> Unit


) {
    val galleryPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    val imagePermissionState = rememberPermissionState(galleryPermission)

    var imageUploadUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), onResult = {
            imageUploadUri = it
        })
    var name by remember { mutableStateOf("") }
    var variety by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier
                        .height(32.dp),
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.slight_dark_green)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    enabled = !isUploading
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel)
                    )
                }
                Button(
                    modifier = Modifier
                        .height(32.dp),
                    onClick = {
                        if (!isUploading) {
                            sellCrop(
                                CropModel(
                                    cropName = name,
                                    variety = variety,
                                    quantity = quantity.toLong(),
                                    price_per_unit = price.toDouble(),
                                    imageUrl = imageUploadUri.toString()
                                )
                            )
                        }

                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.slight_dark_green)
                    ),

                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)

                ) {
                    if (!isUploading) {
                        Text(
                            text = stringResource(id = R.string.sell)
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.crop_data)
            )
        },
        text = {
            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        CustomOutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = stringResource(id = R.string.name),
                            supportingText = "",
                            readOnly = false
                        )
                        CustomOutlinedTextField(
                            value = variety,
                            onValueChange = { variety = it },
                            label = stringResource(id = R.string.variety),
                            supportingText = "",
                            readOnly = false
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .width(80.dp)
                            .clickable(
                                enabled = true,
                                onClick = {
                                    if (imagePermissionState.status.isGranted) {
                                        galleryLauncher.launch("image/*")
                                    } else {
                                        imagePermissionState.launchPermissionRequest()
                                    }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUploadUri == null) {
                            Text(
                                text = stringResource(id = R.string.upload_image),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            AsyncImage(
                                model = imageUploadUri,
                                contentDescription = "Upload Image",
                                modifier = Modifier
                                    .height(80.dp)
                                    .width(80.dp),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        label = {
                            Text(
                                text = stringResource(id = R.string.price)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.grass_green),
                            focusedBorderColor = colorResource(id = R.color.grass_green),
                            focusedLabelColor = colorResource(R.color.grass_green),
                            unfocusedTextColor = colorResource(id = R.color.grass_green),
                            cursorColor = colorResource(id = R.color.grass_green),
                        )

                    )
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        label = {
                            Text(
                                text = stringResource(id = R.string.quantity)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = colorResource(id = R.color.grass_green),
                            focusedBorderColor = colorResource(id = R.color.grass_green),
                            focusedLabelColor = colorResource(R.color.grass_green),
                            unfocusedTextColor = colorResource(id = R.color.grass_green),
                            cursorColor = colorResource(id = R.color.grass_green),
                        ),
                    )

                }
            }

        }
    )
}

@Composable
fun CropSellDataItem(
    crop: CropModel,
    onUpdateClick: () -> Unit,
    onRemoveClick: () -> Unit,
    context: Context,
    imageLoader: ImageLoader
) {
    Box {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color = Color.White)

        ) {

            AsyncImage(
                model = ImageRequest.Builder(context).data(crop.imageUrl).crossfade(true)
                    .build(),
                contentDescription = "Crop Image",
                imageLoader = imageLoader,
                modifier = Modifier
                    .weight(3f),
                contentScale = ContentScale.FillBounds

            )

            VerticalDivider()
            Column(
                modifier = Modifier
                    .weight(7f)
                    .padding(8.dp)
            ) {
                TextRow(
                    key = "Name:- ",
                    value = crop.cropName

                )

                TextRow(
                    key = "Variety:- ",
                    value = crop.variety

                )
                TextRow(
                    key = "Price:- ",
                    value = crop.price_per_unit.toString(),
                    trailingValue = "rs/kg"

                )

                TextRow(
                    key = "Quantity:- ",
                    value = crop.quantity.toString()

                )
                TextRow(
                    key = "Location:- ",
                    value = crop.village + ", " + crop.district + ", " + crop.state

                )





                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .height(32.dp),
                        onClick = onUpdateClick,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.slight_dark_green)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel)
                        )
                    }
                    Button(
                        modifier = Modifier
                            .height(32.dp),
                        onClick = onRemoveClick,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.slight_dark_green)
                        ),

                        contentPadding = PaddingValues(
                            horizontal = 8.dp, vertical = 0.dp
                        )

                    ) {
                        Text(
                            text = stringResource(id = R.string.remoge)
                        )
                    }
                }
            }
        }
    }
}




