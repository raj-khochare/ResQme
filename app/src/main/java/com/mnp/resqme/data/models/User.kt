package com.mnp.resqme.data.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val medicalConditions: String = "",
    val allergies: String = "",
    val bloodType: String = "",
    val emergencyContactIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)