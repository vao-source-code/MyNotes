package com.example.mynotes.entities

import android.os.Parcelable
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "notes")
data class Note (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
     var id: Int = 0 ,

    @ColumnInfo(name = "title")
     var title: String,

    @ColumnInfo(name = "date_time")
     var dateTime: String,

    @ColumnInfo(name = "subtitle")
     var subtitle: String,

    @ColumnInfo(name = "note_text")
     var noteText: String,

    @ColumnInfo(name = "image_path")
     var imagePath: String? = null,

    @ColumnInfo(name = "color")
     var color: String? = null ,

    @ColumnInfo(name = "web_link")
     var webLink: String? = null ,

    ) : Parcelable

