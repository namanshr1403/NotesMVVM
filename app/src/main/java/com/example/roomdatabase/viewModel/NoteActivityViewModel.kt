package com.example.roomdatabase.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.roomdatabase.model.Note
import com.example.roomdatabase.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteActivityViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    fun saveNote(newNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.addNote(newNote)
    }

    fun updateNote(existingNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.updateNote(existingNote)
    }

    fun deleteNote(deleteNote: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepository.deleteNote(deleteNote)
    }

    fun searchQuery(query:String): LiveData<List<Note>>
    {
        return noteRepository.searchNote(query)
    }

    fun getAllNote(): LiveData<List<Note>> = noteRepository.getNote()

}