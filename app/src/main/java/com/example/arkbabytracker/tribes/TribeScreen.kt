package com.example.arkbabytracker.tribes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.utils.MyTheme

@Composable
fun TribeScreen(vm:TribeViewModel = viewModel()){
    val currentTribe = null // TODO figure this out
    ShowTribeOrJoinOptions(currentTribe)
}

@Composable
fun ShowTribeOrJoinOptions(currentTribe:String?) {
    Box(modifier=Modifier.fillMaxSize()) {
        if (currentTribe == null) {
            ShowMakeOrJoinButtons()
        } else {
            ShowTribe(currentTribe)
        }
    }
}

@Composable
fun ShowTribe(currentTribe: String) {
    Column {
        Text("Currently a member of $currentTribe", color = MaterialTheme.colors.onBackground)
        Button(onClick = { /*TODO*/ }) {
            Text("Leave")
        }
    }
}

@Composable
fun ShowMakeOrJoinButtons() {
    Column {
        Text("Not currently in a tribe", color = MaterialTheme.colors.onBackground)
        Button(onClick = { /*TODO*/ }) {
            Text("Create Tribe")
        }
        Button(onClick = { /*TODO*/ }) {
            Text("Join Tribe")
        }
    }
}

@Preview
@Composable
fun PreviewTribeScreen(){
    MyTheme(isDarkTheme = true) {
        ShowTribeOrJoinOptions(currentTribe = null)
    }

}