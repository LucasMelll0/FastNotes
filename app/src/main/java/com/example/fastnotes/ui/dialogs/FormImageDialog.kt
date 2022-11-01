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
        val dialog = AlertDialog.Builder(context).create()
        val view = ImageFormDialogBinding.inflate(LayoutInflater.from(context), null, false)
        view.apply {
            urlDefault?.let{
                imageviewImageFormDialog.load(urlDefault)
                edittextLingImageInputDialog.editText?.setText(urlDefault)
            }
            fabLoadImageFormDialog.setOnClickListener {
                val url = edittextLingImageInputDialog.editText?.text.toString()
                imageviewImageFormDialog.load(url)
            }
            buttonConfirmImageForm.setOnClickListener {
                val url = edittextLingImageInputDialog.editText?.text.toString()
                getUrl(url)
                dialog.dismiss()
            }
            buttonCancelImageForm.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setView(view.root)
        dialog.show()
    }
}