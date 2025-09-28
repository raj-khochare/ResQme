package com.mnp.resqme.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Guide related data classes
data class GuideCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

data class GuideItem(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: Int,
    val estimatedTime: Int,
    val steps: List<String>
)

data class ProfileInfo(
    val title: String,
    val value: String,
    val icon: ImageVector
)

data class AppSetting(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// Emergency related data classes
data class EmergencyContact(
    val name: String,
    val number: String,
    val description: String,
    val icon: ImageVector,
    val isUrgent: Boolean
)

data class EmergencyAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val route: String
)