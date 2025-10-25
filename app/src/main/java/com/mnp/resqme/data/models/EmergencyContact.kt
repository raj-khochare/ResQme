package com.mnp.resqme.data.models

import com.google.firebase.firestore.Exclude

data class EmergencyContact(
    @get:Exclude
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val relationship: String = "",
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", false, 0L)
}