package com.mnp.resqme.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.makePhoneCall(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_CALL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivity(intent)
}

fun Context.openDialer(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    startActivity(intent)
}

// String extensions
fun String.isValidEmail(): Boolean = ValidationUtils.isValidEmail(this)
fun String.isValidPassword(): Boolean = ValidationUtils.isValidPassword(this)
fun String.isValidName(): Boolean = ValidationUtils.isValidName(this)
fun String.isValidPhone(): Boolean = ValidationUtils.isValidPhone(this)