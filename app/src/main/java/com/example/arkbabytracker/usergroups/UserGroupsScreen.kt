package com.example.arkbabytracker.usergroups

import android.widget.Toast
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.utils.MyTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun UserGroupsScreen(){
    val vm:UserGroupsViewModel = viewModel()
    val c = LocalContext.current
    val groups = vm.getGroupsObservable(c)
    Column {
        //Floating EditText and button
        UserInput(onSubmit = {s:String -> vm.addGroup(s,c)})
        //Scrolling List of groups with swipe delete
        GroupList(groups = groups, onSwipeAction = {s:String -> vm.removeGroup(s,c)})
    }
}


@Composable
fun UserInput(onSubmit:(String)->Unit){
    var text by remember{ mutableStateOf("")}
    Column {
        TextField(value = text, onValueChange = { newText: String -> text = newText },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onBackground))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()){
            Button(onClick = { onSubmit(text) }) {
                Text("Create Group")
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupList(groups:List<String>, onSwipeAction: (String) -> Unit){
    LazyColumn(){
        items(groups){
            key(it) {
                GroupItem(group = it, onSwipeAction)
            }
        }
    }
}

enum class SwipeDirection(val raw: Int) {
    Left(0),
    Initial(1),
    Right(2),
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupItem(group:String, onSwipeAction:(String)->Unit, swipeableState: SwipeableState<SwipeDirection> = rememberSwipeableState(initialValue = SwipeDirection.Initial)){

    if(swipeableState.currentValue == SwipeDirection.Right){
        onSwipeAction(group)
    }
    BoxWithConstraints() {
        val constraintsScope = this
        val maxWidth = with(LocalDensity.current) {
            constraintsScope.maxWidth.toPx()
        }
        Box(
            modifier = Modifier
                .swipeable(
                    swipeableState,
                    anchors = mapOf(0f to SwipeDirection.Initial, maxWidth to SwipeDirection.Right),
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(
                        x = swipeableState.offset.value.roundToInt(),
                        y = 0
                    )
                }
                .fillMaxWidth()
                .padding(12.dp)

        ) {
            Text(group, color = MaterialTheme.colors.onBackground, fontSize = 24.sp)
        }
    }
}

@Preview
@Composable
fun Preview(){
    MaterialTheme(colors = darkColors()) {
        UserGroupsScreen()
    }
}

@Preview
@Composable
fun LightPreview(){
    MaterialTheme(colors = lightColors()){
        UserGroupsScreen()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun GroupItemPreview(){
    MaterialTheme(colors = darkColors()){
        GroupItem(group = "Test",{})
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun GroupListPreview(){
    MyTheme {
        GroupList(groups = listOf("1","Group 2","3"), onSwipeAction = {})
    }
}