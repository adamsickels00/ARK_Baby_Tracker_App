package com.example.arkbabytracker.compose

import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.arkbabytracker.R

@Composable
fun ColoredTextBox(text:String,color:Int){
    val typed = TypedValue()
    LocalContext.current.theme.resolveAttribute(color,typed,true)
    @ColorInt val colorV = typed.data
    val c = Color(colorV)
    Text(text,color=c)
}