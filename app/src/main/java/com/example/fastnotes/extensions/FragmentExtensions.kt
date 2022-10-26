package com.example.fastnotes.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fastnotes.R
import com.example.fastnotes.databinding.ConfirmationBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

fun Fragment.showDialog(
    message: Int? = null,
    confirmation: () -> Unit = {}
){
    val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    val view = ConfirmationBottomSheetBinding.inflate(layoutInflater, null, false)
    message?.let {
        view.textviewConfirmationQuestionBottomSheet.text = getString(message)
    }
    view.buttonConfirmationBottomSheet.setOnClickListener {
        confirmation()
        dialog.dismiss()
    }
    dialog.setCancelable(true)
    dialog.setContentView(view.root)
    dialog.show()
}

fun Fragment.goTo(destination: Int){
    findNavController().navigate(destination)
}