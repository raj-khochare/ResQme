package com.mnp.resqme.util

import android.util.Patterns

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidName(name: String): Boolean {
        return name.trim().length >= 2
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.trim().length >= 10 && phone.all { it.isDigit() || it == '+' || it == '-' || it == ' ' }
    }

    fun getPasswordStrength(password: String): PasswordStrength {
        return when {
            password.length < 6 -> PasswordStrength.WEAK
            password.length < 8 -> PasswordStrength.MEDIUM
            password.length >= 8 && password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isDigit() } -> PasswordStrength.STRONG
            else -> PasswordStrength.MEDIUM
        }
    }
}

enum class PasswordStrength {
    WEAK, MEDIUM, STRONG

}