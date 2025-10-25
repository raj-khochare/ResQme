@file:OptIn(ExperimentalMaterial3Api::class)

package com.mnp.resqme.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.firebase.auth.FirebaseAuth
import com.mnp.resqme.data.models.EmergencyContact
import com.mnp.resqme.ui.components.RescueBottomNavigation
import com.mnp.resqme.util.Constants
import com.mnp.resqme.util.getEmergencyActions
import com.mnp.resqme.util.getEmergencyServices
import com.mnp.resqme.util.makePhoneCall
import com.mnp.resqme.viewmodel.ContactViewModel


@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    navController: NavController,
    contactViewModel: ContactViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val contacts by contactViewModel.contacts.collectAsStateWithLifecycle()

    var isSendingSOS by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasSmsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasCallPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var pendingPhoneNumber by remember { mutableStateOf<String?>(null) }
    var shouldTriggerSOSAfterLocation by remember { mutableStateOf(false) }

    // Permission launcher for SOS (location + SMS)
    val sosPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        hasSmsPermission = permissions[Manifest.permission.SEND_SMS] ?: false

        // If permissions granted, trigger SOS again
        if (hasLocationPermission && hasSmsPermission) {
            triggerSOS(
                context = context,
                vibrator = vibrator,
                contacts = contacts,
                onSendingStateChange = { isSendingSOS = it }
            )
        } else {
            Toast.makeText(
                context,
                "Location and SMS permissions are required for SOS",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    fun handleSOSClick() {
        // Check if contacts exist first
        if (contacts.isEmpty()) {
            Toast.makeText(
                context,
                "Please add emergency contacts first",
                Toast.LENGTH_LONG
            ).show()
            navController.navigate("emergency_contacts")
            return
        }

        // Check if location is enabled
        if (!isLocationEnabled()) {
            showLocationDialog = true
            return
        }

        // Check permissions
        if (!hasLocationPermission || !hasSmsPermission) {
            sosPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS
                )
            )
        } else {
            triggerSOS(
                context = context,
                vibrator = vibrator,
                contacts = contacts,
                onSendingStateChange = { isSendingSOS = it }
            )
        }
    }

    // Location settings launcher - enables location with one tap
    val locationSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Location enabled successfully, trigger SOS
            Toast.makeText(context, "Location enabled", Toast.LENGTH_SHORT).show()
            if (shouldTriggerSOSAfterLocation) {
                shouldTriggerSOSAfterLocation = false
                handleSOSClick()
            }
        } else {
            // User declined, but still send SOS without precise location
            Toast.makeText(context, "Sending SOS without precise location", Toast.LENGTH_SHORT).show()
            if (shouldTriggerSOSAfterLocation) {
                shouldTriggerSOSAfterLocation = false
                if (!hasLocationPermission || !hasSmsPermission) {
                    sosPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.SEND_SMS
                        )
                    )
                } else {
                    triggerSOS(
                        context = context,
                        vibrator = vibrator,
                        contacts = contacts,
                        onSendingStateChange = { isSendingSOS = it }
                    )
                }
            }
        }
    }



    // Permission launcher for phone calls
    val callPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCallPermission = isGranted
        if (isGranted && pendingPhoneNumber != null) {
            context.makePhoneCall(pendingPhoneNumber!!)
            pendingPhoneNumber = null
        } else if (!isGranted) {
            Toast.makeText(context, "Phone call permission denied", Toast.LENGTH_SHORT).show()
            pendingPhoneNumber = null
        }
    }

    fun handlePhoneCall(phoneNumber: String) {
        if (hasCallPermission) {
            context.makePhoneCall(phoneNumber)
        } else {
            pendingPhoneNumber = phoneNumber
            callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    fun requestLocationEnable() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // Location is already enabled, proceed with SOS
            handleSOSClick()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location is not enabled, but we can show a dialog to enable it
                try {
                    shouldTriggerSOSAfterLocation = true
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    // Failed to show dialog, fall back to sending without location
                    Toast.makeText(
                        context,
                        "Cannot enable location. Sending SOS anyway.",
                        Toast.LENGTH_SHORT
                    ).show()
                    handleSOSClick()
                }
            } else {
                // Unknown error, proceed anyway
                handleSOSClick()
            }
        }
    }



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

                // SOS Button
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            // Smart location request - enables with one tap
                            requestLocationEnable()
                        },
                        enabled = !isSendingSOS,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            disabledContainerColor = Color.Red.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .size(150.dp)
                            .shadow(8.dp, CircleShape)
                    ) {
                        if (isSendingSOS) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        } else {
                            Text(
                                text = "SOS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info text below SOS button
                    if (contacts.isEmpty()) {
                        Text(
                            text = "No emergency contacts added",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { navController.navigate("emergency_contacts") }
                        ) {
                            Text("Add Contacts Now")
                        }
                    } else {
                        Text(
                            text = "Ready to send alert to ${contacts.size} contact(s)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        // Show primary contact if exists
                        contacts.firstOrNull { it.isPrimary }?.let { primary ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Primary: ${primary.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
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

            items(getEmergencyServices()) { contact ->
                EmergencyContactCard(
                    name = contact.name,
                    number = contact.phoneNumber,
                    description = contact.description,
                    icon = contact.icon,
                    isUrgent = contact.isUrgent,
                    onClick = { handlePhoneCall(contact.phoneNumber) }
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

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Location Settings Dialog (fallback)
    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Enable Location?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "For accurate emergency response, enable location services.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "In urgent situations, you can still send SOS without location.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLocationDialog = false
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Enable Location")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLocationDialog = false
                        // Allow sending without location in emergency
                        Toast.makeText(
                            context,
                            "Sending SOS without location",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (!hasLocationPermission || !hasSmsPermission) {
                            sosPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.SEND_SMS
                                )
                            )
                        } else {
                            triggerSOS(
                                context = context,
                                vibrator = vibrator,
                                contacts = contacts,
                                onSendingStateChange = { isSendingSOS = it }
                            )
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Send Anyway")
                }
            }
        )
    }
}

private fun triggerSOS(
    context: Context,
    vibrator: Vibrator,
    contacts: List<EmergencyContact>,
    onSendingStateChange: (Boolean) -> Unit
) {
    // Vibrate
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(500)
    }

    onSendingStateChange(true)

    // Check permissions at runtime
    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasSmsPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.SEND_SMS
    ) == PackageManager.PERMISSION_GRANTED

    // Get location and send SMS
    if (hasLocationPermission && hasSmsPermission) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    val locationStr = if (location != null) {
                        "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                    } else {
                        "Location unavailable"
                    }
                    sendSOSMessages(context, contacts, locationStr)
                    onSendingStateChange(false)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Location error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendSOSMessages(context, contacts, "Location unavailable")
                    onSendingStateChange(false)
                }
        } catch (e: SecurityException) {
            Toast.makeText(
                context,
                "Location permission error",
                Toast.LENGTH_SHORT
            ).show()
            sendSOSMessages(context, contacts, "Location unavailable")
            onSendingStateChange(false)
        }
    } else {
        onSendingStateChange(false)
        Toast.makeText(
            context,
            "Location and SMS permissions required",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun sendSOSMessages(
    context: Context,
    contacts: List<EmergencyContact>,
    location: String
) {
    // Define your default emergency number here
    val DEFAULT_EMERGENCY_NUMBER = Constants.SAMPLE_DEFAULT_EMERGENCY_NUMBER // ← Change this to your default number
//    val userName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User"

    try {
        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

//        val message = "EMERGENCY!!!\n\n I am $userName I need help!\n\nMy location: $location\n\n- Sent from ResQme"
        val message = "EMERGENCY!!! I need help!\n\nMy location: $location\n\n- Sent from ResQme"

        var successCount = 0
        var failCount = 0

        // Send to default emergency number FIRST
        try {
            smsManager.sendTextMessage(
                DEFAULT_EMERGENCY_NUMBER,
                null,
                message,
                null,
                null
            )
            successCount++
            android.util.Log.d("EmergencyScreen", "SMS sent to default emergency number: $DEFAULT_EMERGENCY_NUMBER")
        } catch (e: Exception) {
            failCount++
            android.util.Log.e("EmergencyScreen", "Failed to send SMS to default emergency number", e)
        }

        // Then send to all user's personal contacts
        contacts.forEach { contact ->
            try {
                smsManager.sendTextMessage(
                    contact.phoneNumber,
                    null,
                    message,
                    null,
                    null
                )
                successCount++
                android.util.Log.d("EmergencyScreen", "SMS sent to ${contact.name} (${contact.phoneNumber})")
            } catch (e: Exception) {
                failCount++
                android.util.Log.e("EmergencyScreen", "Failed to send SMS to ${contact.name}", e)
            }
        }

        if (successCount > 0) {
            val totalRecipients = if (contacts.isEmpty()) 1 else contacts.size + 1
            Toast.makeText(
                context,
                "✓ SOS sent to $successCount of $totalRecipients recipient(s)${if (failCount > 0) " ($failCount failed)" else ""}",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                context,
                "Failed to send SOS messages",
                Toast.LENGTH_SHORT
            ).show()
        }
    } catch (e: Exception) {
        android.util.Log.e("EmergencyScreen", "Error sending SMS", e)
        Toast.makeText(
            context,
            "Error sending SMS: ${e.message}",
            Toast.LENGTH_SHORT
        ).show()
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