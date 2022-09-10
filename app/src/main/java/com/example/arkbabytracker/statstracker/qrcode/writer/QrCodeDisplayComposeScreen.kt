package com.example.arkbabytracker.statstracker.qrcode.writer

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.statstracker.data.DinoGender
import com.example.arkbabytracker.statstracker.data.DinoStats

@Composable
fun QrDisplayScreen(dinoList:List<DinoStats>, vm: QrDisplayViewModel = viewModel()){

    Column(){
        QrTitle(modifier = Modifier.weight(.2f))
        //QR display
        QrDisplay(modifier=Modifier.weight(1f), bitmapList = vm.createQrBitmapList(dinoList,1500,1500))
    }
}

@Composable
fun QrTitle(modifier: Modifier=Modifier){
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Scan this code on another device within the app to import all the dinos", style = TextStyle(fontSize = 24.sp, color = MaterialTheme.colors.onBackground))
    }
}

@Composable
fun QrDisplay(modifier: Modifier = Modifier,bitmapList: List<ImageBitmap>){
    Column(modifier = Modifier.verticalScroll(rememberScrollState(),enabled = true)) {
        for(bitmap in bitmapList) {
                Image(bitmap = bitmap, contentDescription = "QR Code")
        }
    }

}

@Preview
@Composable
fun QrDisplayPreview(){
    val dinoStatsList = listOf(
        DinoStats("Rex","Bob",1,1,2,2,1,1,1,listOf(1,2,3,4,5,6),DinoGender.Male),
        DinoStats("Rex","Bob's Twin",1,1,2,2,1,1,1,listOf(1,2,3,4,5,6),DinoGender.Male),
        DinoStats("Raptor","Blue",1,1,2,2,1,1,1,listOf(2,2,2,2,2,2),DinoGender.Female),
    )
    MaterialTheme(colors= darkColors()) {
        QrDisplayScreen(dinoStatsList)
    }
}