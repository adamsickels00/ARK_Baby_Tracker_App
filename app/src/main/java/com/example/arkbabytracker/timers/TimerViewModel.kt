package com.example.arkbabytracker.timers

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(val timerRepo: TimerRepository) : ViewModel(){

    fun getAllTimers(): Flow<List<Timer>> {
        return timerRepo.getAllTimers()
    }

    fun addTimer(t:Timer){
        CoroutineScope(Dispatchers.IO).launch { timerRepo.insert(t) }
    }

    fun deleteTimer(context: Context, t:Timer){
        CoroutineScope(Dispatchers.IO).launch { timerRepo.delete(t) }
        NotificationScheduler.cancelNotification(context,t.id!!.toInt())
    }
}