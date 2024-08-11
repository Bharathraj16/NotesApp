package com.example.notesappdb.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesappdb.data.Note
import com.example.notesappdb.data.NoteDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val dao: NoteDao,
):ViewModel() {


    private val isSortedByDateAdded = MutableStateFlow(true)
    @OptIn(ExperimentalCoroutinesApi::class)
    private var notes: StateFlow<List<Note>> = isSortedByDateAdded.flatMapLatest { sort->
        if(sort){
            dao.getNotesOrderedByDataAdded()
        }else{
            dao.getNotesOrderedByTitle()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val _state=MutableStateFlow(NotesState())
    val state= combine(
            _state,isSortedByDateAdded,notes
            ){
        state,isSortedByDateAdded,notes->
        state.copy(
            notes = notes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotesState())

    fun onEvent(event:NoteEvent){
        when(event){
            is NoteEvent.DeleteNote->{
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }
            is NoteEvent.SaveNote->{
                val note=Note(
                    title=state.value.title.value ,
                    description=state.value.description.value,
                    dateAdded=System.currentTimeMillis()
                )
                viewModelScope.launch {
                    dao.upsertNote(note)
                }
                _state.update {
                    it.copy(
                        title=mutableStateOf(""),
                        description= mutableStateOf("")
                    )
                }
            }
            NoteEvent.sortNotes->{
                isSortedByDateAdded.value=!isSortedByDateAdded.value
            }
        }
    }
}