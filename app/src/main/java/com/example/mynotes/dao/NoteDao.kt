package com.example.mynotes.dao

import androidx.room.*
import com.example.mynotes.entities.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
            fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(vararg note: Note)

    @Delete
    fun deleteNote(note : Note)

}