package com.example.notesappdb.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.notesappdb.data.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val title:MutableState<String> = mutableStateOf(""),
    val description:MutableState<String> = mutableStateOf("")
)
