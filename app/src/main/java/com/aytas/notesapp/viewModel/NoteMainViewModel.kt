package com.aytas.notesapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aytas.notesapp.models.Note
import com.aytas.notesapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteMainViewModel(private val noteRepository: NoteRepository):ViewModel() {

    fun addNote(note:Note){
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.addNote(note)
        }
    }

    fun updateNote(note:Note){
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }

    fun deleteNote(note:Note){
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteNote(note)
        }
    }

    fun getAllNote():LiveData<List<Note>>{
        return noteRepository.getNote()
    }

    fun searchNote(query:String):LiveData<List<Note>>{
        return noteRepository.searchNote(query)
    }
}