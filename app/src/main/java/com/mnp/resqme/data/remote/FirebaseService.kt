package com.mnp.resqme.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mnp.resqme.data.models.User
import com.mnp.resqme.util.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? = auth.currentUser

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>{
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun createUserWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserToFirestore(user: User): Result<Unit> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(user.uid)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserFromFirestore(uid: String): Result<User> {
        return try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            if (document.exists()) {
                val user = document.toObject(User::class.java)!!
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ADD THESE MISSING METHODS:
    suspend fun updateUserField(uid: String, field: String, value: Any): Result<Unit> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .update(field, value)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            getUserFromFirestore(currentUser.uid)
        } else {
            Result.failure(Exception("No user logged in"))
        }
    }

    suspend fun deleteUser(): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Delete user document from Firestore
                firestore.collection(Constants.USERS_COLLECTION)
                    .document(currentUser.uid)
                    .delete()
                    .await()

                // Delete Firebase Auth user
                currentUser.delete().await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No user logged in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean = currentUser != null
}