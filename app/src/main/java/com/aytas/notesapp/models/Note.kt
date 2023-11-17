package com.aytas.notesapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    val title:String,
    val date: String,
    val content:String,
    val color:Int=-1

) : java.io.Serializable
