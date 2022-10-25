package com.example.fastnotes.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import coil.load
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ImageFormDialogBinding

class FormImageDialog(val context: Context) {

    fun show(
        urlDefault: String? = null,
        getUrl: (image: String) -> Unit
    ){
        ImageFormDialogBinding.inflate(LayoutInflater.from(context)).apply {
            urlDefault?.let{
                imageviewImageFormDialog.load(urlDefault)
                edittextLingImageInputDialog.editText?.setText(urlDefault)
            }
            fabLoadImageFormDialog.setOnClickListener {
                val url = edittextLingImageInputDialog.editText?.text.toString()
                imageviewImageFormDialog.load(url)
            }
           AlertDialog
               .Builder(context)
               .setView(root)
               .setPositiveButton(context.getText(R.string.confirm)) {_, _ ->
                   val url = edittextLingImageInputDialog.editText?.text.toString()
                   getUrl(url)
               }
               .setNegativeButton(context.getText(R.string.cancel)) { _, _ -> }
               .show()
        }
    }
}