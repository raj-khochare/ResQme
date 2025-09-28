package com.mnp.resqme.data.repository

import com.mnp.resqme.data.models.User
import com.mnp.resqme.data.remote.FirebaseService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firebaseService: FirebaseService
) {

    suspend fun getCurrentUser(): Result<User> {
        return firebaseService.getCurrentUser()
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return firebaseService.saveUserToFirestore(user)
    }

    suspend fun updateUserField(uid: String, field: String, value: Any): Result<Unit> {
        return try {
            firebaseService.updateUserField(uid, field, value)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}