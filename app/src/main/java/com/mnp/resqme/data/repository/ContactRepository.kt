package com.mnp.resqme.data.repository

import android.util.Log
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
        Log.d("ContactRepository", "getUserContacts called for userId: $userId")

        val listener = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
            .whereEqualTo("userId", userId)
            .orderBy("isPrimary", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .orderBy("name")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ContactRepository", "Error listening to contacts", error)
                    close(error)
                    return@addSnapshotListener
                }

                val contacts = snapshot?.documents?.mapNotNull { doc ->
                    Log.d("ContactRepository", "Processing document: ${doc.id}")
                    doc.toObject(EmergencyContact::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                Log.d("ContactRepository", "Found ${contacts.size} contacts")
                trySend(contacts)
            }

        awaitClose {
            Log.d("ContactRepository", "Removing snapshot listener")
            listener.remove()
        }
    }

    suspend fun addContact(contact: EmergencyContact, userId: String): Result<String> {
        return try {
            Log.d("ContactRepository", "addContact called")
            Log.d("ContactRepository", "  Collection: ${Constants.EMERGENCY_CONTACTS_COLLECTION}")
            Log.d("ContactRepository", "  UserId: $userId")

            // Create a map without the id field - let Firestore generate it
            val contactData = hashMapOf(
                "name" to contact.name,
                "phoneNumber" to contact.phoneNumber,
                "relationship" to contact.relationship,
                "isPrimary" to contact.isPrimary,
                "userId" to userId
            )

            Log.d("ContactRepository", "  Contact data: $contactData")

            val docRef = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .add(contactData)
                .await()

            Log.d("ContactRepository", "Contact added successfully with ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error adding contact", e)
            Result.failure(e)
        }
    }

    suspend fun updateContact(contactId: String, contact: EmergencyContact): Result<Unit> {
        return try {
            Log.d("ContactRepository", "updateContact called for ID: $contactId")

            val contactData = hashMapOf(
                "name" to contact.name,
                "phoneNumber" to contact.phoneNumber,
                "relationship" to contact.relationship,
                "isPrimary" to contact.isPrimary,
                "userId" to contact.userId
            )

            firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .document(contactId)
                .set(contactData)
                .await()

            Log.d("ContactRepository", "Contact updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error updating contact", e)
            Result.failure(e)
        }
    }

    suspend fun deleteContact(contactId: String): Result<Unit> {
        return try {
            Log.d("ContactRepository", "deleteContact called for ID: $contactId")

            firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .document(contactId)
                .delete()
                .await()

            Log.d("ContactRepository", "Contact deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error deleting contact", e)
            Result.failure(e)
        }
    }

    suspend fun setPrimaryContact(userId: String, contactId: String): Result<Unit> {
        return try {
            Log.d("ContactRepository", "setPrimaryContact called")
            Log.d("ContactRepository", "  UserId: $userId")
            Log.d("ContactRepository", "  ContactId: $contactId")

            val batch = firestore.batch()
            val contacts = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            Log.d("ContactRepository", "Found ${contacts.documents.size} contacts to update")

            contacts.documents.forEach { doc ->
                batch.update(doc.reference, "isPrimary", false)
            }

            val contactRef = firestore.collection(Constants.EMERGENCY_CONTACTS_COLLECTION)
                .document(contactId)
            batch.update(contactRef, "isPrimary", true)

            batch.commit().await()

            Log.d("ContactRepository", "Primary contact set successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ContactRepository", "Error setting primary contact", e)
            Result.failure(e)
        }
    }
}