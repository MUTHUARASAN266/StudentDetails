package com.studentdetails

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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


    fun getCircularBitmap(bitmap: Bitmap, size: Int): Bitmap {
        // Scale down the bitmap to the desired size
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, false)
        val radius = size / 2f

        // Create a circular bitmap
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val shader = BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader
        paint.isAntiAlias = true

        // Draw a circle with the bitmap shader
        canvas.drawCircle(radius, radius, radius, paint)

        // Cleanup
        scaledBitmap.recycle()
        return output
    }
    fun ImageView.loadImage(url: String?) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.student)
            .into(this)
    }

}