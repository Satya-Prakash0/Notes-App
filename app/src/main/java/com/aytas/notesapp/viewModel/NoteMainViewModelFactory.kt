package com.aytas.notesapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aytas.notesapp.repository.NoteRepository

class NoteMainViewModelFactory(private val noteRepository: NoteRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteMainViewModel(noteRepository) as T
    }
}