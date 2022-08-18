package com.example.arkbabytracker.colorsearch

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.statstracker.data.DinoGender
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.utils.DinoColorUtils
import com.example.arkbabytracker.utils.MyTheme

@Composable
fun ColorSearchScreen(colorViewModel: ColorSearchViewModel = viewModel()){
    val colorsFilter = remember{ colorViewModel.colorsFilter}
    var selectedType by remember { colorViewModel.selectedType }
    // Color select
    val dinoList by colorViewModel.dinoList.collectAsState(initial = listOf())
    Column {
        ColorSelect(colorLists = colorViewModel.getColorLists(dinoList),
            selectedType = selectedType,
            types = colorViewModel.getCurrentDinoTypes(dinoList),
            colorsFilter = colorsFilter,
            onColorChanged = { index: Int, colorId: Int -> colorsFilter[index] = colorId },
            onTypeChanged = { s: String -> selectedType = s; colorViewModel.resetColorsFilter() })

        //List of potential dinos
        val filteredDinoList = colorViewModel.getDinoListWithColors(dinoList)
        DinoList(dinoList = filteredDinoList)
    }
}

@Composable
fun ColorSelect(colorLists : List<Set<Int>>,selectedType:String,types:Set<String>, colorsFilter:List<Int>, onColorChanged:(Int,Int)->Unit,onTypeChanged:(String)->Unit){
    val scrollState = rememberScrollState()
    Column {
        TypeDropDown(
            types = types,
            selectedType = selectedType,
            saveSelectedItem = { s: String -> onTypeChanged(s) })
        Column() {
            var index = 0
            while (index<colorLists.size) {
                Row() {
                    for (i in index..index+2) {
                        Box(modifier = Modifier.weight(1f)) {
                            ColorDropDown(
                                colorLists[i],
                                colorsFilter[i],
                                label="Color Region ${i+1}",
                                onColorChanged = { colorId: Int -> onColorChanged(i, colorId)})
                        }
                    }
                    index+=3
                }
            }
        }
    }
}

@Composable
fun TypeDropDown(types: Set<String>, selectedType: String, saveSelectedItem: (String)->Unit){
    var text by rememberSaveable { mutableStateOf(selectedType) }
    var isExpanded by rememberSaveable{ mutableStateOf(false)}
    val focusManager = LocalFocusManager.current
    Column {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it; isExpanded = true },
            modifier = Modifier.onFocusChanged {
                if (it.isFocused) {
                    isExpanded = true; text = ""
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                textColor = MaterialTheme.colors.onBackground
            )
        )
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            properties = PopupProperties(focusable = false)
        ) {
            types.filter { it.startsWith(text, ignoreCase = true) }.forEach {
                DropdownMenuItem(onClick = {
                    if(it!=selectedType)saveSelectedItem(it);text = it;isExpanded = false; focusManager.clearFocus()
                }) {
                    Text(it)
                }
            }
        }
    }

}

@Composable
fun getColor(id: Int): Color {
    val colorResourceId = DinoColorUtils.getColorOfIdIfExists(id)
    return colorResourceId?.let { colorResource(id = it) } ?: MaterialTheme.colors.background
}

@Composable
fun ColorDropDown(colorList:Set<Int>,selectedItem:Int, label:String, onColorChanged: (Int) -> Unit){

    var isExpanded by remember{ mutableStateOf(false)}

    val initialColor = getColor(id = selectedItem)
    var buttonBackgroundColor:Color by remember(key1 = selectedItem) {
        mutableStateOf(initialColor)
    }
    Column() {
        Button(
            onClick = { isExpanded = !isExpanded },
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonBackgroundColor)
        ) {
            DropdownSelectedContent(id = selectedItem)
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            val newList = mutableListOf(-1)
            newList.addAll(colorList)
            for (id in newList) {
                DropdownMenuItem(
                    onClick = { isExpanded = false; onColorChanged(id) },
                    modifier = Modifier.background(color = getColor(id))
                ) {
                    DropdownSelectedContent(id = id)
                }
            }
        }
    }
}

@Composable
fun DropdownSelectedContent(id:Int){
    if(id==-1){
        Text("No Color")
    } else{
        Text(id.toString())
    }
}

@Composable
fun DinoList(dinoList : List<DinoStats>){
    LazyColumn(){
        for(x in dinoList){
            item(key=x.id){
                SingleDino(x)
            }
        }
    }
}

@Composable
fun SingleDino(dino:DinoStats){
    /*TODO Show the color regions of the Dino*/
    Column() {
        Text(dino.name,color=MaterialTheme.colors.onBackground)
        Text("Gender: ${dino.gender.name}",color=MaterialTheme.colors.onBackground)
        Row(modifier=Modifier.fillMaxWidth()){
            for(c in dino.colorList){
                val color = getColor(id = c)
                Text(c.toString(),modifier = Modifier
                    .background(color)
                    .weight(1f),color = Color(DinoColorUtils.getTextColorForBackgroundHex(color.toArgb(), LocalContext.current)))
            }
        }
    }
    
}

@Preview
@Composable
fun PreviewCSS(){
    MyTheme(isDarkTheme = true) {
        ColorSearchScreen()
    }
}


@Preview
@Composable
fun PreviewTypeDropDown(){
    MyTheme(isDarkTheme = true) {
        SingleDino(dino = DinoStats("Test","Name",1,2,3,4,5,6,7, listOf(1,2,3,4,5,6),DinoGender.Male))
    }
}