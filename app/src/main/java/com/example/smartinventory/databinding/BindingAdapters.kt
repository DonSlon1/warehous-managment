package com.example.smartinventory.databinding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.smartinventory.R

@BindingAdapter("quantityText")
fun setQuantityText(view: TextView, quantity: Int) {
    val context = view.context
    val formattedText = context.getString(R.string.quantity_text, quantity)
    view.text = formattedText
}