package com.example.arkbabytracker.statstracker.qrcode.reader

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.colorsearch.DinoList
import com.example.arkbabytracker.colorsearch.SingleDino
import com.example.arkbabytracker.statstracker.data.DinoStats

@Composable
fun ImportDinoScreen(vm:ImportDinoViewModel = viewModel(),dino:DinoStats, onImport:()->Unit){
    Column{
        SingleDino(dino = dino)
        Button(onClick = {vm.acceptImport(dino); onImport() }) {
            Text("Accept Import")
        }
    }
}