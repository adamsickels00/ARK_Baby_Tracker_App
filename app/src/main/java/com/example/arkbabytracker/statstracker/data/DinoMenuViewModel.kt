package com.example.arkbabytracker.statstracker.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DinoMenuViewModel @Inject constructor(val dinoStatsDao: DinoStatsDao) : ViewModel() {
    var liveDinoList = MutableLiveData<MutableList<DinoStats>>(mutableListOf())
    val dinoList get() = liveDinoList.value!!
    val dinoByType:Map<String,List<DinoStats>> get() {
        var result = mutableMapOf<String,MutableList<DinoStats>>()
        dinoList.forEach {
            result.getOrPut(it.type){ mutableListOf()}.add(it)
        }
        return result
    }

    fun getFromDatabase(){
        CoroutineScope(Dispatchers.IO).launch {
            liveDinoList.postValue(dinoStatsDao.getAllDinos()?.toMutableList())
        }
    }

    fun removeDinoStat(d:DinoStats){
        val tempList = liveDinoList.value!!
        tempList.remove(d)
        liveDinoList.value = tempList
        CoroutineScope(Dispatchers.IO).launch {
            dinoStatsDao.delete(d)

        }
    }

}