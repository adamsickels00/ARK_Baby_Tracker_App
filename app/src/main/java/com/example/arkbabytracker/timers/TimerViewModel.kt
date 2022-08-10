package com.example.arkbabytracker.timers

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(var timerDao: TimerDao) : ViewModel(){

    fun getAllTimers(): Flow<List<Timer>> {
        return timerDao.getAllTimers()
    }

    fun addTimer(t:Timer){
        CoroutineScope(Dispatchers.IO).launch { timerDao.insert(t) }
    }

    fun deleteTimer(t:Timer){
        CoroutineScope(Dispatchers.IO).launch { timerDao.delete(t) }
    }
}