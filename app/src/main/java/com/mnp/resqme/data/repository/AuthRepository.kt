package com.mnp.resqme.data.repository

import com.google.firebase.auth.FirebaseUser
import com.mnp.resqme.data.models.User
import com.mnp.resqme.data.remote.FirebaseService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseService: FirebaseService
) {

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return firebaseService.signInWithEmail(email, password)
    }

    suspend fun registerUser(
        email: String,
        password: String,
        name: String
    ): Result<FirebaseUser> {
        val result = firebaseService.createUserWithEmail(email, password)

        return if (result.isSuccess) {
            val firebaseUser = result.getOrNull()!!
            val user = User(
                uid = firebaseUser.uid,
                email = email,
                name = name
            )

            val saveResult = firebaseService.saveUserToFirestore(user)
            if (saveResult.isSuccess) {
                Result.success(firebaseUser)
            } else {
                Result.failure(saveResult.exceptionOrNull()!!)
            }
        } else {
            result
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        val currentUser = firebaseService.currentUser
        return if (currentUser != null) {
            firebaseService.getUserFromFirestore(currentUser.uid)
        } else {
            Result.failure(Exception("No user logged in"))
        }
    }

    fun logout() {
        firebaseService.signOut()
    }

    fun isLoggedIn(): Boolean = firebaseService.isUserLoggedIn()
}