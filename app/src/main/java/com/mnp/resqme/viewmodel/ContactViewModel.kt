package com.mnp.resqme.viewmodel

import android.util.Log
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

    init {
        Log.d("ContactViewModel", "Initialized with userId: $userId")
        if (userId.isEmpty()) {
            Log.e("ContactViewModel", "WARNING: User ID is empty! User not logged in?")
        }
    }

    val contacts: StateFlow<List<EmergencyContact>> = repository.getUserContacts(userId)
        .onEach { contactList ->
            Log.d("ContactViewModel", "Contacts updated: ${contactList.size} contacts")
            contactList.forEach { contact ->
                Log.d("ContactViewModel", "  - ${contact.name} (${contact.phoneNumber})")
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _operationState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operationState: StateFlow<UiState<String>> = _operationState.asStateFlow()

    fun addContact(contact: EmergencyContact) {
        Log.d("ContactViewModel", "addContact called")
        Log.d("ContactViewModel", "  Name: ${contact.name}")
        Log.d("ContactViewModel", "  Phone: ${contact.phoneNumber}")
        Log.d("ContactViewModel", "  Relationship: ${contact.relationship}")
        Log.d("ContactViewModel", "  UserId: $userId")

        if (userId.isEmpty()) {
            Log.e("ContactViewModel", "Cannot add contact: userId is empty")
            _operationState.value = UiState.Error("User not logged in")
            return
        }

        viewModelScope.launch {
            _operationState.value = UiState.Loading
            Log.d("ContactViewModel", "Calling repository.addContact...")

            val result = repository.addContact(contact, userId)

            if (result.isSuccess) {
                Log.d("ContactViewModel", "Contact added successfully! ID: ${result.getOrNull()}")
                _operationState.value = UiState.Success("Contact added successfully")
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to add contact"
                Log.e("ContactViewModel", "Failed to add contact: $errorMsg", result.exceptionOrNull())
                _operationState.value = UiState.Error(errorMsg)
            }
        }
    }

    fun updateContact(contactId: String, contact: EmergencyContact) {
        Log.d("ContactViewModel", "updateContact called for ID: $contactId")

        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val result = repository.updateContact(contactId, contact.copy(userId = userId))

            if (result.isSuccess) {
                Log.d("ContactViewModel", "Contact updated successfully")
                _operationState.value = UiState.Success("Contact updated successfully")
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to update contact"
                Log.e("ContactViewModel", "Failed to update contact: $errorMsg", result.exceptionOrNull())
                _operationState.value = UiState.Error(errorMsg)
            }
        }
    }

    fun deleteContact(contactId: String) {
        Log.d("ContactViewModel", "deleteContact called for ID: $contactId")

        viewModelScope.launch {
            _operationState.value = UiState.Loading
            val result = repository.deleteContact(contactId)

            if (result.isSuccess) {
                Log.d("ContactViewModel", "Contact deleted successfully")
                _operationState.value = UiState.Success("Contact deleted successfully")
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Failed to delete contact"
                Log.e("ContactViewModel", "Failed to delete contact: $errorMsg", result.exceptionOrNull())
                _operationState.value = UiState.Error(errorMsg)
            }
        }
    }

    fun setPrimaryContact(contactId: String) {
        Log.d("ContactViewModel", "setPrimaryContact called for ID: $contactId")

        viewModelScope.launch {
            val result = repository.setPrimaryContact(userId, contactId)

            if (result.isSuccess) {
                Log.d("ContactViewModel", "Primary contact set successfully")
            } else {
                val errorMsg = "Failed to set primary contact"
                Log.e("ContactViewModel", errorMsg, result.exceptionOrNull())
                _operationState.value = UiState.Error(errorMsg)
            }
        }
    }

    fun resetState() {
        Log.d("ContactViewModel", "resetState called")
        _operationState.value = UiState.Idle
    }
}