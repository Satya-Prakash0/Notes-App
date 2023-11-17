package com.aytas.notesapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aytas.notesapp.models.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase :RoomDatabase(){

    abstract fun getNoteDao():DAO

    companion object{
        @Volatile

        private var INSTANCE: NoteDatabase?=null
        fun getDatabase(context: Context): NoteDatabase{
            if (INSTANCE==null){
                synchronized(this){
                    INSTANCE= Room.databaseBuilder(context.applicationContext,NoteDatabase::class.java,"NoteDB")
                        .build()
                }
            }
            return INSTANCE!!
        }
    }

}