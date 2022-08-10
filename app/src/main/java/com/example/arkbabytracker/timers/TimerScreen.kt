package com.example.arkbabytracker.timers

import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.utils.TimeDisplayUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.logging.Handler

@Composable
fun TimerScreen(){
    val vm : TimerViewModel = viewModel()
    val timerList by vm.getAllTimers().collectAsState(initial = listOf())
    TimerList(timerList)
}

@Composable
fun TimerList(timerList:List<Timer>){
    LazyColumn() {
        items(timerList) { item -> SingleTimerScreen(t = item) }
    }
}

@Composable
fun SingleTimerScreen(t:Timer){
    var currentTime by remember { mutableStateOf(Instant.now().epochSecond)}
    Row {
        Text(
            TimeDisplayUtil.secondsToString((0-currentTime + (t.startTime+t.length)).toInt()),
            color = MaterialTheme.colors.onBackground
        )
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
fun PreviewTimerScreen(){
    MaterialTheme(colors = darkColors()){
        TimerScreen()
    }
}

@Preview
@Composable
fun PreviewSingleTimerScreen(){
    MaterialTheme(colors = darkColors()){
        SingleTimerScreen(Timer(Instant.now().epochSecond,60))
    }
}