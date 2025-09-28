package com.mnp.resqme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnp.resqme.data.models.User
import com.mnp.resqme.data.repository.UserRepository
import com.mnp.resqme.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val userState: StateFlow<UiState<User>> = _userState

    private val _updateState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val updateState: StateFlow<UiState<String>> = _updateState

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            val result = userRepository.getCurrentUser()

            if (result.isSuccess) {
                _userState.value = UiState.Success(result.getOrNull()!!)
            } else {
                _userState.value = UiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load user"
                )
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading

            val result = userRepository.updateUser(user)

            if (result.isSuccess) {
                _updateState.value = UiState.Success("Profile updated successfully")
                _userState.value = UiState.Success(user)
            } else {
                _updateState.value = UiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to update profile"
                )
            }
        }
    }

    fun updateUserField(uid: String, field: String, value: Any) {
        viewModelScope.launch {
            val result = userRepository.updateUserField(uid, field, value)

            if (result.isSuccess) {
                loadCurrentUser() // Refresh user data
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = UiState.Idle
    }
}