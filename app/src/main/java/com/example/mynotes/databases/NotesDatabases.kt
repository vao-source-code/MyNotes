package com.example.mynotes.databases

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.mynotes.dao.NoteDao
import com.example.mynotes.entities.Note

@Database(entities = [Note::class], version = 3 , exportSchema = false )
abstract class NotesDatabases : RoomDatabase() {


    companion object{
        @JvmStatic
        private var notesDatabases: NotesDatabases? = null

        @Synchronized
        @JvmStatic
        fun getNotesDatabases(context: Context?): NotesDatabases? {


            if (context != null) {
                notesDatabases = notesDatabases ?: Room.databaseBuilder(context.applicationContext , NotesDatabases::class.java , "notes_db").build()
            }

            return notesDatabases
        }
    }


    abstract fun  noteDao() : NoteDao
}