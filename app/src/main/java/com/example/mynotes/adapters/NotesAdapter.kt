package com.example.mynotes.adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mynotes.R
import com.example.mynotes.databinding.ItemContainerNoteBinding
import com.example.mynotes.entities.Note

class NotesAdapter (private var notes: List<Note>): RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private lateinit var itemContainerNoteBinding: ItemContainerNoteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        itemContainerNoteBinding = ItemContainerNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NotesViewHolder(itemContainerNoteBinding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.setNote(notes.get(position))

    }
    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class NotesViewHolder(private val _binding: ItemContainerNoteBinding) : RecyclerView.ViewHolder(_binding.root) {


        fun setNote(note: Note) {
            _binding.textTitle.text = note.title
            if (note.subtitle.trim { it <= ' ' }.isEmpty()) {
                _binding.textSubtitle.visibility = View.GONE
            } else {
                _binding.textSubtitle.text = note.subtitle
            }

            val gradientDrawable : GradientDrawable = _binding.layoutNote.background as GradientDrawable
            if(note.color != null){
                gradientDrawable.setColor(Color.parseColor(note.color))
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"))
            }

            if(note.imagePath != null){
                _binding.imageNote.setImageURI(
                    Uri.parse(note.imagePath))
                _binding.imageNote.visibility = View.VISIBLE
            }else{
                _binding.imageNote.visibility = View.GONE
            }
        }


    }
}