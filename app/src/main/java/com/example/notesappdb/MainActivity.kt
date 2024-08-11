package com.example.notesappdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.notesappdb.data.NotesDatabase
import com.example.notesappdb.presentation.AddNoteScreen
import com.example.notesappdb.presentation.NoteScreen
import com.example.notesappdb.presentation.NotesViewModel
import com.example.notesappdb.ui.theme.NotesAppDBTheme

class MainActivity : ComponentActivity() {
    private  val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            NotesDatabase::class.java,
            "notes.db"
        ).build()
    }
    private  val viewModel by viewModels<NotesViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(database.dao) as T
                }
            }
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppDBTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.state.collectAsState()
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "NoteScreen") {
                        composable("NoteScreen"){
                            NoteScreen(state = state,
                                onEvent = viewModel::onEvent,
                                navController = navController)
                        }
                        composable("AddNoteScreen"){
                            AddNoteScreen(state = state,
                                onEvent = viewModel::onEvent,
                                navController = navController)
                        }
                        
                    }
                }
            }
        }
    }
}

