package com.studentdetails

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

object Utils {
    fun showSnackbar(view: View, message: String, textColor: Int, duration: Int) {
        val snackbar = Snackbar.make(view, message, duration)
        val snackbarView = snackbar.view
        val textView =
            snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        // Change background color
        snackbar.setBackgroundTint(ContextCompat.getColor(view.context, R.color.colorAccent))

        // Set text alignment to center
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        // Change text color
        textView.setTextColor(ContextCompat.getColor(view.context, textColor))

        snackbar.show()
    }

}