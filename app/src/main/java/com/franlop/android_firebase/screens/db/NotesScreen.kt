package com.franlop.android_firebase.screens.db

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.franlop.android_firebase.model.Note
import com.franlop.android_firebase.utils.FirestoreManager
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(firestore: FirestoreManager) {
    var showAddNoteDialog by remember { mutableStateOf(false) }
    val notes by firestore.getNotesFlow().collectAsState(emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddNoteDialog = true
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
            }
            if (showAddNoteDialog) {
                AddNoteDialog(
                    onNoteAdded = { note ->
                        scope.launch {
                            firestore.addNote(note)
                        }
                        showAddNoteDialog = false
                    },
                    onDialogDismissed = { showAddNoteDialog = false }
                )
            }
        }
    ) { paddingValues ->
        if (notes.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(6.dp),
                modifier = Modifier.padding(paddingValues)
            ) {
                items(notes, key = { it.id ?: it.hashCode() }) { note ->
                    NoteItem(note = note, firestore = firestore)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se encontraron\nNotas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, firestore: FirestoreManager) {
    var showDeleteNoteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (showDeleteNoteDialog) {
        DeleteNoteDialog(
            onConfirmDelete = {
                scope.launch {
                    firestore.deleteNote(note.id ?: "")
                }
                showDeleteNoteDialog = false
            },
            onDismiss = {
                showDeleteNoteDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.padding(6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            IconButton(
                onClick = { showDeleteNoteDialog = true }
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
            }
        }
    }
}

@Composable
fun AddNoteDialog(
    onNoteAdded: (Note) -> Unit,
    onDialogDismissed: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDialogDismissed,
        title = { Text(text = "Agregar Nota") },
        confirmButton = {
            Button(
                onClick = {
                    val newNote = Note(
                        title = title,
                        content = content
                    )
                    onNoteAdded(newNote)
                }
            ) {
                Text(text = "Agregar")
            }
        },
        dismissButton = {
            Button(onClick = onDialogDismissed) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Título") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    label = { Text(text = "Contenido") }
                )
            }
        }
    )
}

@Composable
fun DeleteNoteDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar nota") },
        text = { Text("¿Estás seguro que deseas eliminar esta nota?") },
        confirmButton = {
            Button(onClick = onConfirmDelete) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
