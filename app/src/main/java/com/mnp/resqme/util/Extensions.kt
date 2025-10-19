package com.mnp.resqme.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri


//fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
//    Toast.makeText(this, message, duration).show()
//}
//
fun Context.makePhoneCall(phoneNumber: String) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
        == PackageManager.PERMISSION_GRANTED) {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    } else {
        // Permission not granted - need to request it
        // This should be handled at the Activity/Composable level
        Toast.makeText(this, "Phone call permission required", Toast.LENGTH_SHORT).show()
    }
}

//fun Context.openDialer(phoneNumber: String) {
//    val intent = Intent(Intent.ACTION_DIAL).apply {
//        data = Uri.parse("tel:$phoneNumber")
//    }
//    startActivity(intent)
//}

//// String extensions
//fun String.isValidEmail(): Boolean = ValidationUtils.isValidEmail(this)
//fun String.isValidPassword(): Boolean = ValidationUtils.isValidPassword(this)
//fun String.isValidName(): Boolean = ValidationUtils.isValidName(this)
//fun String.isValidPhone(): Boolean = ValidationUtils.isValidPhone(this)