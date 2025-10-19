package com.mnp.resqme.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mnp.resqme.data.models.EmergencyContact
import com.mnp.resqme.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getUserContacts(userId: String): Flow<List<EmergencyContact>> = callbackFlow {
        val listener = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("isPrimary", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val contacts = snapshot?.documents?.mapNotNull {
                    it.toObject(EmergencyContact::class.java)?.copy(id = it.id)
                } ?: emptyList()

                trySend(contacts)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addContact(contact: EmergencyContact, userId: String): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .add(contact.copy(userId = userId))
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateContact(contactId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .document(contactId)
                .set(contact)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteContact(contactId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .document(contactId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setPrimaryContact(userId: String, contactId: String): Result<Unit> {
        return try {
            // Clear all primary flags
            val batch = firestore.batch()
            val contacts = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            contacts.documents.forEach { doc ->
                batch.update(doc.reference, "isPrimary", false)
            }

            // Set new primary
            val contactRef = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .document(contactId)
            batch.update(contactRef, "isPrimary", true)

            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}