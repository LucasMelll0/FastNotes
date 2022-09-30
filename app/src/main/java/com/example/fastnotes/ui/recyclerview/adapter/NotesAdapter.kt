package com.example.fastnotes.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fastnotes.databinding.NoteListItemBinding
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note

class NotesAdapter(
    private val context: Context,
    noteList: List<Note> = emptyList(),
    private val userFieldEnable: Boolean = true
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val dataSet: MutableList<Note> = noteList.toMutableList()

    inner class NotesViewHolder(private val binding: NoteListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindItem(note: Note){
            binding.apply {
                if (userFieldEnable){
                    imageviewListItem.tryLoadImage(note.image)
                    textviewTitleListItem.text = note.title
                    textviewUserListItem.text = note.user
                    textviewNoteListItem.text = note.description
                }else{
                    imageviewListItem.tryLoadImage(note.image)
                    textviewTitleListItem.text = note.title
                    textviewUserListItem.visibility = View.GONE
                    textviewNoteListItem.text = note.description
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = NoteListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindItem(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    fun addAll(notes: List<Note>){
        dataSet.clear()
        dataSet.addAll(notes)
    }

}