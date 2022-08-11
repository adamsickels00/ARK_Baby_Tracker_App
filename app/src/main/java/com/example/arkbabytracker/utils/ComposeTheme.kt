package com.example.arkbabytracker.utils

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

@Composable
fun MyTheme( block:@Composable ()->Unit){
    MaterialTheme(if(isSystemInDarkTheme()) darkColors() else lightColors()){
       block ()
    }
}