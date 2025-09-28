@file:OptIn(ExperimentalMaterial3Api::class)

package com.mnp.resqme.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mnp.resqme.ui.components.RescueBottomNavigation
import com.mnp.resqme.util.getEmergencyActions
import com.mnp.resqme.util.getEmergencyContacts
import com.mnp.resqme.util.openDialer


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    navController: NavController
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Emergency",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            )
        },
        bottomBar = {
            RescueBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Emergency Warning
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency Contacts",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "In a life-threatening emergency, call immediately:",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Emergency Services",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(getEmergencyContacts()) { contact ->
                EmergencyContactCard(
                    name = contact.name,
                    number = contact.number,
                    description = contact.description,
                    icon = contact.icon,
                    isUrgent = contact.isUrgent,
                    onClick = { context.openDialer(contact.number) }
                )
            }

            item {
                Text(
                    text = "Quick Actions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            items(getEmergencyActions()) { action ->
                EmergencyActionCard(
                    title = action.title,
                    description = action.description,
                    icon = action.icon,
                    onClick = action.onClick
                )
            }
        }
    }
}

@Composable
fun EmergencyContactCard(
    name: String,
    number: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isUrgent: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = if (isUrgent) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                modifier = Modifier.size(48.dp),
                tint = if (isUrgent) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isUrgent) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Text(
                    text = number,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = if (isUrgent) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Call",
                tint = if (isUrgent) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
fun EmergencyActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Action"
            )
        }
    }
}

