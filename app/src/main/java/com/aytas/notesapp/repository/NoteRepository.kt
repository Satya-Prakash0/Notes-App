package com.aytas.notesapp.repository

import com.aytas.notesapp.db.DAO
import com.aytas.notesapp.models.Note

class NoteRepository(private val noteDao: DAO) {

    fun getNote()=noteDao.getAllNote();

    fun searchNote(query:String)=noteDao.searchNote(query)

    suspend fun addNote(note: Note)=noteDao.addNote(note)

    suspend fun updateNote(note: Note)=noteDao.updateNote(note)

    suspend fun deleteNote(note: Note)=noteDao.deleteNote(note)

}