package com.example.roomdatabase.repository

import com.example.roomdatabase.db.NoteDatabase
import com.example.roomdatabase.model.Note

class NoteRepository(private var db:NoteDatabase) {

    fun getNote() = db.getNoteDao().getAllNote();

    fun searchNote(query:String) = db.getNoteDao().searchNote(query)

    suspend fun addNote(note: Note) = db.getNoteDao().addNote(note)

    suspend fun updateNote(note: Note) = db.getNoteDao().updateNote(note)

    suspend fun deleteNote(note: Note) = db.getNoteDao().deleteNote(note)

}