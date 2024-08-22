package com.proyecto.sistemaventaspropat.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.proyecto.sistemaventaspropat.R
import java.text.NumberFormat
import java.util.Locale

@BindingAdapter("formattedCostWithIva")
fun bindFormattedCostWithIva(textView: TextView, costWithIva: Double?) {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CR"))
    textView.text = costWithIva?.let { format.format(it) } ?: ""
}


@BindingAdapter("imageFromBase64")
fun bindImageFromBase64(imageView: ImageView, base64String: String?) {
    if (!base64String.isNullOrEmpty()) {
        val bitmap = decodeBase64ToBitmap(base64String)
        imageView.setImageBitmap(bitmap)
    }
}

@BindingAdapter("intValue")
fun bindIntValue(textView: TextView, value: Int) {
    textView.text = value.toString()
}


private fun decodeBase64ToBitmap(base64String: String): Bitmap? {
    return try {
        val decodedString = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

