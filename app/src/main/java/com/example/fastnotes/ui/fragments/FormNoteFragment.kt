package com.example.fastnotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fastnotes.R
import com.example.fastnotes.database.AppDataBase
import com.example.fastnotes.databinding.FragmentFormNoteBinding
import com.example.fastnotes.extensions.showDialog
import com.example.fastnotes.extensions.tryLoadImage
import com.example.fastnotes.model.Note
import com.example.fastnotes.repositories.NoteRepository
import com.example.fastnotes.repositories.UserRepository
import com.example.fastnotes.ui.dialogs.FormImageDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class FormNoteFragment : Fragment() {

    private val args: FormNoteFragmentArgs by navArgs()
    private var _binding: FragmentFormNoteBinding? = null
    private val binding get() = _binding!!
    private val repository by lazy {
        NoteRepository(
            this,
            AppDataBase
                .instance(requireContext())
                .noteDao()
        )
    }
    private var noteId: String? = null
    private var noteKey: String? = null
    private var image: String? = null
    private var public: Boolean? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpSaveButton()
        setsUpBackButton()
        setsUpFabImage()
        checkHasArgs()

    }


    private fun setsUpFabImage() {
        binding.fabAddImage.setOnClickListener {
            FormImageDialog(requireContext()).show(image) { url ->
                image = url
                image?.let { link ->
                    if (link.isNotEmpty()) {
                        binding.imageviewInputNoteFragment.tryLoadImage(link)
                        binding.cardviewImageview.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setsUpFabDelete() {
        binding.fabDeleteNoteFragment.setOnClickListener {
            showDialog {
                noteId?.let {
                    lifecycleScope.launch {
                        val note = createNote()
                        note?.let {
                            repository.remove(note)
                        } ?: run {
                            Snackbar.make(
                                requireView(),
                                getString(R.string.default_error_message),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                        findNavController().navigate(R.id.action_formNoteFragment_to_notesListFragment)
                    }
                } ?: findNavController().popBackStack()
            }

        }
    }

    private fun checkHasArgs() {
        args.note?.let { noteId ->
            tryToGetNoteOnDb(noteId)
            setsUpFabDelete()
        } ?: setsUpFabDelete()
    }

    private fun tryToGetNoteOnDb(noteId: String) {
        lifecycleScope.launch {
            binding.progressbarFormNoteFragment.visibility = View.VISIBLE
            repository.getNoteById(noteId).firstOrNull()?.let { note ->
                fillFields(note)
                binding.progressbarFormNoteFragment.visibility = View.GONE
            } ?: findNavController().popBackStack()
        }
    }

    private fun fillFields(note: Note) {
        binding.apply {
            noteId = note.id
            noteKey = note.key
            image = note.image
            switchPublicInputNoteFragment.isChecked = note.public
            if(imageviewInputNoteFragment.tryLoadImage(note.image)){
                cardviewImageview.visibility = View.VISIBLE
            }
            textinputTitleInputNoteFragment.editText?.setText(note.title)
            textinputDescriptionInputNoteFragment.editText?.setText(note.description)
        }
    }

    private fun setsUpBackButton() {
        binding.imagebuttonBackNoteActivity.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setsUpSaveButton() {
        binding.imagebuttonConfirmNoteActivity.setOnClickListener {
            if (titleNotEmpty()) {
                val note = createNote()
                note?.let {
                    binding.progressbarFormNoteFragment.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        noteId?.let {
                            repository.update(note)
                            binding.progressbarFormNoteFragment.visibility = View.GONE
                            findNavController().navigate(R.id.action_formNoteFragment_to_notesListFragment)
                        } ?: run {
                            repository.save(note)
                            binding.progressbarFormNoteFragment.visibility = View.GONE
                            findNavController().navigate(R.id.action_formNoteFragment_to_notesListFragment)
                        }
                    }
                } ?: run {
                    binding.progressbarFormNoteFragment.visibility = View.GONE
                    Snackbar.make(
                        requireView(),
                        getString(R.string.default_error_message),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun createNote(): Note? {
        binding.apply {
            val title = textinputTitleInputNoteFragment.editText!!.text.toString().trim()
            val description = getDescription()
            public = switchPublicInputNoteFragment.isChecked

            return if (noteId != null) {
                return noteKey?.let {
                    return if (it.isNotEmpty()) {
                        Note(
                            id = noteId!!,
                            key = noteKey!!,
                            image = image ?: "",
                            user = UserRepository.getUser()?.displayName.toString(),
                            userId = UserRepository.getUser()!!.uid,
                            title = title,
                            description = description,
                            public = public ?: false
                        )
                    } else {
                        null
                    }
                }
            } else {
                Note(
                    image = image ?: "",
                    user = UserRepository.getUser()?.displayName.toString(),
                    userId = UserRepository.getUser()!!.uid,
                    title = title,
                    description = description,
                    public = public ?: false,
                    synchronized = true
                )
            }
        }
    }

    private fun getDescription(): String {
        val descriptionEdt = binding.textinputDescriptionInputNoteFragment.editText
        return descriptionEdt?.let { description ->
            return if (description.text.isNotEmpty()) {
                description.text.toString()
            } else {
                ""
            }
        } ?: ""
    }


    private fun titleNotEmpty(): Boolean =
        binding.textinputTitleInputNoteFragment.editText!!.text.isNotEmpty()


}