package com.example.krishimitra.presentation.profile_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.domain.ResultState
import com.example.krishimitra.domain.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val repo: Repo
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    init {

        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("USER_DATA","Loading")

            repo.getUserDataFromFirebase().collect { result ->
                when (result) {
                    is ResultState.Error<*> -> {}
                    is ResultState.Loading -> {}
                    is ResultState.Success -> {
                        _state.update {
                            it.copy(
                                userData = result.data
                            )
                        }
                        Log.d("PROFILE_SCREEN_USER_DATA",result.data.toString())

                    }
                }

            }
        }
    }
}
