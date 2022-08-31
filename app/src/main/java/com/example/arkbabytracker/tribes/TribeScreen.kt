package com.example.arkbabytracker.tribes

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.utils.MyTheme
import com.example.arkbabytracker.utils.SingleTextBoxWithButton

@Composable
fun TribeScreen(vm:TribeViewModel = viewModel()){
    var currentTribe:Tribe? by remember{ mutableStateOf(null)}
    vm.addTribeStateListener { newTribe-> currentTribe=newTribe }

    ShowTribeOrJoinOptions(currentTribe?.name,
        createTribeAction = {s->vm.createTribe(s)},
        joinTribeAction = {s->vm.joinTribe(s)},
        leaveTribeAction = {vm.leaveTribe(currentTribe?.name)}
    )
}

@Composable
fun ShowTribeOrJoinOptions(
    currentTribe: String?,
    createTribeAction: (String) -> Unit,
    joinTribeAction: (String) -> Unit,
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
fun ShowMakeOrJoinButtons(createTribeAction:(String)->Unit,joinTribeAction: (String) -> Unit) {
    var createPopupShowState by remember{ mutableStateOf(false)}
    var joinPopupShowState by remember{ mutableStateOf(false)}
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Not currently in a tribe", color = MaterialTheme.colors.onBackground)
        Button(onClick = {createPopupShowState=true}) {
            Text("Create Tribe")
        }
        Button(onClick = {joinPopupShowState=true}) {
            Text("Join Tribe")
        }
    }
    if(createPopupShowState){
        Dialog(
            onDismissRequest = {createPopupShowState=false},
        ) {
            TribePopupContent(onSubmitButtonPressed = {s->createPopupShowState=false;createTribeAction(s)})
        }
    }
    if(joinPopupShowState){
        Dialog(
            onDismissRequest = {joinPopupShowState=false},
        ) {
            JoinTribePopupContent(onSubmitButtonPressed = {s->joinPopupShowState=false;joinTribeAction(s)})
        }
    }
}

@Composable
fun TribePopupContent(onSubmitButtonPressed: (String) -> Unit){
        Card(elevation = 8.dp, backgroundColor = MaterialTheme.colors.background) {
            SingleTextBoxWithButton(
                onButtonPressed = onSubmitButtonPressed,
                modifier = Modifier.padding(64.dp),
                textBoxLabel = "Tribe Name"
            )
        }

}

@Composable
fun JoinTribePopupContent(onSubmitButtonPressed: (String) -> Unit){
    Card(elevation = 8.dp, backgroundColor = MaterialTheme.colors.background) {
        SingleTextBoxWithButton(
            onButtonPressed = onSubmitButtonPressed,
            modifier = Modifier.padding(64.dp),
            textBoxLabel = "Tribe Join Key",
            buttonText = "Join Tribe"
        )
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

@Preview
@Composable
fun JoinPopupPreview(){
    MyTheme(isDarkTheme = true) {
        JoinTribePopupContent(onSubmitButtonPressed = {})
    }
}