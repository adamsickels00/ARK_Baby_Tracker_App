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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun TribeScreen(vm:TribeViewModel = viewModel()){
    val currentTribe = vm.getUserTribe()
    ShowTribeOrJoinOptions(currentTribe,
        createTribeAction = {vm.createTribe("Test Name",Firebase.auth.currentUser!!.uid)}
    )
}

@Composable
fun ShowTribeOrJoinOptions(currentTribe:String?,createTribeAction:()->Unit) {
    Box(modifier=Modifier.fillMaxSize()) {
        if (currentTribe == null) {
            ShowMakeOrJoinButtons(createTribeAction)
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
fun ShowMakeOrJoinButtons(createTribeAction:()->Unit) {
    Column {
        Text("Not currently in a tribe", color = MaterialTheme.colors.onBackground)
        Button(onClick = createTribeAction) {
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
        ShowTribeOrJoinOptions(currentTribe = null, createTribeAction = {})
    }

}