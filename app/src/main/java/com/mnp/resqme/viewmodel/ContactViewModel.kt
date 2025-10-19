package com.mnp.resqme.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mnp.resqme.data.models.EmergencyContact
import com.mnp.resqme.data.repository.ContactRepository
import com.mnp.resqme.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val repository: ContactRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val userId = auth.currentUser?.uid ?: ""

    val contacts: StateFlow<List<EmergencyContact>> = repository.getUserContacts(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _operationState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operationState: StateFlow<UiState<String>> = _operationState.asStateFlow()

    fun addContact(contact: EmergencyContact) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val result = repository.addContact(contact, userId)
            _operationState.value = if (result.isSuccess) {
                UiState.Success("Contact added successfully")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to add contact")
            }
        }
    }

    fun updateContact(contactId: String, contact: EmergencyContact) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val result = repository.updateContact(contactId, contact.copy(userId = userId))
            _operationState.value = if (result.isSuccess) {
                UiState.Success("Contact updated successfully")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to update contact")
            }
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val result = repository.deleteContact(contactId)
            _operationState.value = if (result.isSuccess) {
                UiState.Success("Contact deleted successfully")
            } else {
                UiState.Error(result.exceptionOrNull()?.message ?: "Failed to delete contact")
            }
        }
    }

    fun setPrimaryContact(contactId: String) {
        viewModelScope.launch {
            val result = repository.setPrimaryContact(userId, contactId)
            if (result.isFailure) {
                _operationState.value = UiState.Error("Failed to set primary contact")
            }
        }
    }

    fun resetState() {
        _operationState.value = UiState.Idle
    }
}