package com.example.arkbabytracker.tribes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.utils.MyTheme

@Composable
fun TribeScreen(vm:TribeViewModel = viewModel()){
    val currentTribe = vm.getUserTribe()
    ShowTribeOrJoinOptions(currentTribe,
        createTribeAction = {s->vm.createTribe(s)},
        joinTribeAction = {},
        leaveTribeAction = {vm.leaveTribe(currentTribe)}
    )
}

@Composable
fun ShowTribeOrJoinOptions(
    currentTribe: String?,
    createTribeAction: (String) -> Unit,
    joinTribeAction: () -> Unit,
    leaveTribeAction: () -> Unit
) {
    Box(modifier=Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (currentTribe == null) {
            ShowMakeOrJoinButtons(createTribeAction,joinTribeAction)
        } else {
            ShowTribe(currentTribe,leaveTribeAction)
        }
    }
}

@Composable
fun ShowTribe(currentTribe: String, leaveTribeAction: () -> Unit) {
    Column {
        Text("Currently a member of $currentTribe", color = MaterialTheme.colors.onBackground)
        Button(onClick = leaveTribeAction) {
            Text("Leave")
        }
    }
}

@Composable
fun ShowMakeOrJoinButtons(createTribeAction:(String)->Unit,joinTribeAction: () -> Unit,showPopup:Boolean=false) {
    var popupShowState by remember{ mutableStateOf(showPopup)}
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Not currently in a tribe", color = MaterialTheme.colors.onBackground)
        Button(onClick = {popupShowState=true}) {
            Text("Create Tribe")
        }
        Button(onClick = joinTribeAction) {
            Text("Join Tribe")
        }
    }
    if(popupShowState){
        Popup(onDismissRequest = {popupShowState=false}) {
            TribePopupContent(onSubmitButtonPressed = {s->popupShowState=false;createTribeAction(s)})

        }
    }
}

@Composable
fun TribePopupContent(onSubmitButtonPressed: (String) -> Unit){
        var text by remember{mutableStateOf("")}
        Card(elevation = 8.dp, backgroundColor = MaterialTheme.colors.background) {
            Column(Modifier.padding(64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(value = text, onValueChange = {text=it}, label = {Text("Tribe Name")})
                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = { onSubmitButtonPressed(text) }) {
                    Text("Submit")
                }
            }
        }

}

@Preview
@Composable
fun PreviewTribeScreen(){

    MyTheme(isDarkTheme = true) {
        ShowTribeOrJoinOptions(currentTribe = null, createTribeAction = {}, joinTribeAction = {}, leaveTribeAction = {})
    }

}

@Preview
@Composable
fun PopupPreview(){
    MyTheme(isDarkTheme = true) {
        TribePopupContent(onSubmitButtonPressed = {})
    }
}