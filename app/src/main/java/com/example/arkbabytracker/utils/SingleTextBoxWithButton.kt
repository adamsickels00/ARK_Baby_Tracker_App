package com.example.arkbabytracker.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SingleTextBoxWithButton(buttonText:String="Submit", textBoxLabel:String="", modifier: Modifier = Modifier, onButtonPressed:(String)->Unit){
    var text by remember{ mutableStateOf("") }
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(value = text, onValueChange = {text=it}, label = { Text(textBoxLabel) })
        Spacer(modifier = Modifier.padding(10.dp))
        Button(onClick = { onButtonPressed(text) }) {
            Text(buttonText)
        }
    }
}

@Preview
@Composable
fun PreviewSingleTextBoxWithButton(){
    MyTheme(isDarkTheme = true) {
        SingleTextBoxWithButton(onButtonPressed = {})
    }
}