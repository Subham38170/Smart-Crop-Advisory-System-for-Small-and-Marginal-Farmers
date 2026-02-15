package com.example.krishimitra.presentation.buy_sell_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.model.crops_data.CropModel
import com.example.krishimitra.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BuySellScreenViewModel @Inject
constructor(
    private val repo: Repo,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {


    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()


    init {
        getAllUserData()
        getAllBuyingCrops()
        getMyListedCrops()
    }


    private val _sellScreenState = MutableStateFlow(SellScreenState())
    val sellScreenState = _sellScreenState.asStateFlow()

    private val _buyScreenState = MutableStateFlow(BuyScreenState())
    val buyScreenState = _buyScreenState.asStateFlow()

    private fun getAllUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getUserDataFromFirebase()
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Error<*> -> {
                            _event.emit(result.exception)
                        }

                        ResultState.Loading -> {}
                        is ResultState.Success -> {
                            _sellScreenState.update {
                                it.copy(
                                    userData = result.data

                                )
                            }
                            Log.d("USER_DATA", result.data.toString())
                        }
                    }


                }
        }
    }

    fun onEvent(event: BuySellScreenEvent) {
        when (event) {
            is BuySellScreenEvent.onSearch -> {

            }

            is BuySellScreenEvent.onSuggestionSearch -> {

            }

            is BuySellScreenEvent.onListProduct -> {
                uploadMyCrop(event.cropData)
            }

            is BuySellScreenEvent.onCropSearch -> {
                searchCrops(event.cropName)
            }

            is BuySellScreenEvent.loadAllCrops -> {
                getAllBuyingCrops()
            }

            is BuySellScreenEvent.deleteSellCrop -> {
                removeCrop(event.cropModel)
            }
        }
    }

    private fun removeCrop(
        crop: CropModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteListedCrop(
                cropModel = crop
            ).collectLatest { result ->
                when (result) {
                    is ResultState.Error<*> -> {
                        _event.emit(result.exception)
                    }

                    is ResultState.Loading -> {

                    }

                    is ResultState.Success -> {

                    }
                }
            }
        }
    }

    private fun searchCrops(
        cropName: String

    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.searchBuyingCrops(cropName)
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Error -> {
                            _event.emit(result.exception)
                        }

                        ResultState.Loading -> {
                            _buyScreenState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }

                        }

                        is ResultState.Success -> {
                            _buyScreenState.update {
                                it.copy(
                                    isLoading = false,
                                    cropList = result.data
                                )
                            }
                        }
                    }
                }
        }

    }

    fun uploadMyCrop(
        cropModel: CropModel
    ) {


        viewModelScope.launch {


            _sellScreenState.update {
                it.copy(
                    isUploading = true
                )
            }

            repo.sellCrop(
                cropModel.copy(
                    state = sellScreenState.value.userData?.state ?: "Unknown",
                    village = sellScreenState.value.userData?.village ?: "Unknown",
                    district = sellScreenState.value.userData?.district ?: "Unknown",
                    mobileNo = sellScreenState.value.userData?.mobileNo ?: "Unknown",
                    farmerId = firebaseAuth.uid ?: ""
                )
            )
                .collectLatest {
                    when (it) {
                        is ResultState.Error<*> -> {
                            _sellScreenState.update {
                                it.copy(
                                    isUploading = false
                                )
                            }
                            _event.emit(it.exception)
                        }

                        is ResultState.Loading -> {

                            _sellScreenState.update {
                                it.copy(
                                    isUploading = true
                                )
                            }

                        }

                        is ResultState.Success<*> -> {
                            _sellScreenState.update {
                                it.copy(
                                    isUploading = false
                                )
                            }
                            _event.emit("Uploaded Sucessfully")

                        }


                    }
                }
        }
    }


    private fun getAllBuyingCrops() {

        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllBuyingCrops()
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Error<*> -> {
                            _buyScreenState.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _event.emit(result.exception)
                            Log.d("ERROR", result.exception)
                        }

                        is ResultState.Loading -> {
                            _buyScreenState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }

                        is ResultState.Success -> {
                            _buyScreenState.update {
                                it.copy(
                                    cropList = result.data,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun getMyListedCrops() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getMyListedItems()
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Error<*> -> {
                            _sellScreenState.update {
                                it.copy(
                                    isLoading = false
                                )
                            }
                            _event.emit(result.exception)
                        }

                        is ResultState.Loading -> {
                            _sellScreenState.update {
                                it.copy(
                                    isLoading = true
                                )
                            }
                        }

                        is ResultState.Success -> {
                            _sellScreenState.update {
                                it.copy(
                                    isLoading = false,
                                    cropList = result.data
                                )
                            }
                        }
                    }
                }
        }
    }
}