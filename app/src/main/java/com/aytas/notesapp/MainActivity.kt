package com.aytas.notesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.aytas.notesapp.databinding.ActivityMainBinding
import com.aytas.notesapp.db.NoteDatabase
import com.aytas.notesapp.repository.NoteRepository
import com.aytas.notesapp.viewModel.NoteMainViewModel
import com.aytas.notesapp.viewModel.NoteMainViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var noteMainViewModel: NoteMainViewModel
    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)

        try {
            val dao=NoteDatabase.getDatabase(applicationContext).getNoteDao()
            val noteRepository=NoteRepository(dao)
            noteMainViewModel=ViewModelProvider(this,NoteMainViewModelFactory(noteRepository))
                .get(NoteMainViewModel::class.java)
        }
        catch (e:java.lang.Exception){
            Log.d("TAG",e.toString())
        }

    }
}