package com.example.roomdatabase.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdatabase.model.Note
import java.util.concurrent.locks.Lock

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)


abstract class NoteDatabase: RoomDatabase(){

    abstract fun getNoteDao(): DAO

    companion object{
        @Volatile
        private var instance : NoteDatabase? = null
        private val Lock= Any()


        operator fun invoke(context: Context) = instance?: synchronized(Lock){
            instance ?: createDataBase(context).also {
                instance = it
            }
        }


        private fun createDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NoteDatabase::class.java,
            "note_database"
        ).build()
    }




}