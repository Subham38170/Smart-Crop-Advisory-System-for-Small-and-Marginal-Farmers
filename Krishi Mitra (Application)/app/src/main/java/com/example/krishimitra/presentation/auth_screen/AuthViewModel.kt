package com.example.krishimitra.presentation.auth_screen

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.krishimitra.Constants
import com.example.krishimitra.FirebaseConstants
import com.example.krishimitra.data.repo.DataStoreManager
import com.example.krishimitra.domain.model.farmer_data.UserDataModel
import com.example.krishimitra.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
class AuthViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val repo: Repo,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        getLanguage()
    }

    fun getLocation() {
        _state.update { it.copy(isLocationLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val location = repo.getLocation()
                if (location != null) {
                    _state.update {
                        it.copy(
                            isLocationLoading = false,
                            location = location
                        )
                    }
                } else {
                    _state.update { it.copy(isLocationLoading = false) }
                    _error.emit("Unable to access location")
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLocationLoading = false) }
                _error.emit(e.message ?: "Unknown error while getting location")
            }
        }
    }

    private fun getLanguage() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getLanguage().collectLatest { langcode ->
                _state.update {
                    it.copy(
                        currentLanguage = Constants.SUPPORTED_LANGUAGES
                            .find { it.code == langcode }
                            ?.nativeName ?: "English"
                    )
                }
            }
        }
    }

    fun changeLanguage(langCode: String) {
        viewModelScope.launch {
            repo.changeLanguage(langCode)
            getLanguage()
        }
    }

    fun signIn(userData: UserDataModel) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isSignLoading = true) }
            firebaseAuth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnSuccessListener {
                    _state.update { it.copy(isSignLoading = false, isSuccess = true) }
                    fireStore.collection(FirebaseConstants.USERS)
                        .document(firebaseAuth.uid ?: "Unknown")
                        .get()
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                dataStoreManager.storeUserData(
                                    it.toObject(UserDataModel::class.java) ?: UserDataModel()
                                )
                            }
                        }
                }
                .addOnFailureListener { e ->
                    _state.update { it.copy(isSignLoading = false) }
                    viewModelScope.launch { _error.emit(e.message ?: "Sign in failed") }
                }
        }
    }

    fun signUp(userData: UserDataModel) {
        _state.update { it.copy(isSignLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            firebaseAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnSuccessListener {
                    fireStore.collection(FirebaseConstants.USERS)
                        .document(firebaseAuth.uid ?: "Unknown")
                        .set(userData)
                        .addOnSuccessListener {
                            _state.update { it.copy(isSignLoading = false, isSuccess = true) }
                            viewModelScope.launch {
                                dataStoreManager.storeUserData(userData)
                            }
                        }
                        .addOnFailureListener { e ->
                            _state.update { it.copy(isSignLoading = false) }
                            viewModelScope.launch {
                                _error.emit(
                                    e.message ?: "Failed to save user"
                                )
                            }
                        }
                }
                .addOnFailureListener { e ->
                    _state.update { it.copy(isSignLoading = false) }
                    viewModelScope.launch { _error.emit(e.message ?: "Sign up failed") }
                }
        }
    }

    fun onEnableLocationPermission(activity: Activity) {
        repo.requestLocationPermission(activity)
    }
}
