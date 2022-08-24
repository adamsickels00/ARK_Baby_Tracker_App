package com.example.arkbabytracker.timers

import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.utils.SwipableBox
import com.example.arkbabytracker.utils.TimeDisplayUtil
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.logging.Handler

@Composable
fun TimerScreen(vm : TimerViewModel = viewModel()){
    val timerList by vm.getAllTimers().collectAsState(initial = listOf())
    val context = LocalContext.current
    TimerList(timerList, onSwipeRight = {t->vm.deleteTimer(context,t)})
}

@Composable
fun TimerList(timerList:List<Timer>,onSwipeRight:(t:Timer)->Unit){
    LazyColumn() {
        items(timerList, key = {it.id.toString()}) { item ->
            SwipableBox(onSwipeRight = { onSwipeRight(item) }) {
                SingleTimerScreen(t = item,modifier = it)
            }
        }
    }
}

@Composable
fun SingleTimerScreen(t:Timer, modifier: Modifier = Modifier){
    var currentTime by remember { mutableStateOf(Instant.now().epochSecond)}
    Card(modifier=modifier.fillMaxWidth().shadow(10.dp, ambientColor = MaterialTheme.colors.onBackground, spotColor = MaterialTheme.colors.onBackground )) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column {
                Text(
                    t.description,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 24.sp
                )
                Text(
                    "Time Remaining: " + TimeDisplayUtil.secondsToString((0 - currentTime + (t.startTime + t.length)).toInt()),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 24.sp
                )
            }
        }

    }

    LaunchedEffect(key1 = Unit ){
        while (true){
            currentTime = Instant.now().epochSecond
            delay(1000)
        }
    }

}

@Preview
@Composable
fun getDemoTimerList():List<Timer>{
    val demoTimer = Timer(Instant.now().epochSecond,60,"Group 1: Rex, Anky","Group 1")
    return listOf(demoTimer.apply { id=0 },demoTimer.copy().apply { id=1 },demoTimer.copy().apply { id=2 })
}

@Preview
@Composable
fun PreviewTimerScreen(){
    MaterialTheme(colors = darkColors()){
        val l = getDemoTimerList()
        TimerList(timerList = l, onSwipeRight = {})
    }
}

@Preview
@Composable
fun PreviewSingleTimerScreen(){
    MaterialTheme(colors = darkColors()){
        SingleTimerScreen(getDemoTimerList()[0])
    }
}