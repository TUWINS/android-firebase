package com.franlop.android_firebase.utils

import android.content.Context
import com.franlop.android_firebase.model.Note
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreManager(context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = AuthManager(context)

    suspend fun addNote(note: Note) {
        val currentUserId = auth.getCurrentUser()?.uid.orEmpty()
        note.userId = currentUserId
        firestore.collection("notes").add(note).await()
    }

    suspend fun updateNote(note: Note) {
        val noteId = note.id
        if (!noteId.isNullOrEmpty()) {
            firestore.collection("notes").document(noteId).set(note).await()
        }
    }

    suspend fun deleteNote(noteId: String) {
        firestore.collection("notes").document(noteId).delete().await()
    }

    fun getNotesFlow(): Flow<List<Note>> = callbackFlow {
        val currentUserId = auth.getCurrentUser()?.uid.orEmpty()
        val notesRef = firestore.collection("notes")
            .whereEqualTo("userId", currentUserId)

        val subscription = notesRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            snapshot?.let { querySnapshot ->
                val notes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Note::class.java)?.apply {
                        id = document.id
                    }
                }
                trySend(notes).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }
}
