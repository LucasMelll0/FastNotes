package com.example.fastnotes.ui.recyclerview.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.fastnotes.R
import com.example.fastnotes.databinding.NoteListItemBinding
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note

class NotesAdapter(
    private val context: Context,
    noteList: List<Note> = emptyList(),
    private val userFieldEnable: Boolean = true,
    var whenClickItem: (note: Note) -> Unit = {},
    var whenClickDelete: (note: Note) -> Unit = {}
) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val dataSet: MutableList<Note> = noteList.toMutableList()

    inner class NotesViewHolder(private val binding: NoteListItemBinding) :
        RecyclerView.ViewHolder(binding.root), PopupMenu.OnMenuItemClickListener {

        private lateinit var note: Note

        init {
            itemView.setOnClickListener {
                if (::note.isInitialized) {
                    whenClickItem(note)
                }
            }
            itemView.setOnLongClickListener {
                if (::note.isInitialized) {
                    PopupMenu(context, itemView).apply {
                        menuInflater.inflate(R.menu.note_list_menu, menu)
                        setOnMenuItemClickListener(this@NotesViewHolder)
                    }.show()
                }
                true
            }
        }

        fun bindItem(note: Note) {
            this.note = note
            binding.apply {
                if (userFieldEnable) {
                    imageviewListItem.tryLoadImage(note.image)
                    textviewTitleListItem.text = note.title
                    textviewUserListItem.text = note.user
                    textviewNoteListItem.text = note.description
                } else {
                    imageviewListItem.tryLoadImage(note.image)
                    textviewTitleListItem.text = note.title
                    textviewUserListItem.visibility = View.GONE
                    textviewNoteListItem.text = note.description
                }
            }
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            item?.let {
                when (it.itemId) {
                    R.id.note_list_menu_delete -> {
                        whenClickDelete(note)
                    }
                }
            }
            return true
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

    fun update(notes: List<Note>) {
        notifyItemRangeRemoved(0, this.dataSet.size)
        dataSet.clear()
        dataSet.addAll(notes)
        notifyItemInserted(this.dataSet.size)
    }


}