package com.example.notesappdb.presentation

import com.example.notesappdb.data.Note

sealed interface NoteEvent {
    object sortNotes:NoteEvent
    data class DeleteNote(val note: Note):NoteEvent
    data class SaveNote(
        val title: String,
        val description: String
    ):NoteEvent
}