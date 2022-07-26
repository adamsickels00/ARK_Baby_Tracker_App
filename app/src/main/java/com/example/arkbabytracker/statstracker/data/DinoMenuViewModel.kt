package com.example.arkbabytracker.statstracker.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DinoMenuViewModel() : ViewModel() {
    var liveDinoList = MutableLiveData<MutableList<DinoStats>>(mutableListOf())
    val dinoList get() = liveDinoList.value!!
    val dinoByType:Map<String,List<DinoStats>> get() {
        var result = mutableMapOf<String,MutableList<DinoStats>>()
        dinoList.forEach {
            result.getOrPut(it.type){ mutableListOf()}.add(it)
        }
        return result
    }

    var db:DinoStatsDatabase? = null

    fun getFromDatabase(){
        CoroutineScope(Dispatchers.IO).launch {
            liveDinoList.postValue(db?.dinoStatsDao()?.getAllDinos()?.toMutableList())
        }
    }

    fun removeDinoStat(d:DinoStats){
        val tempList = liveDinoList.value!!
        tempList.remove(d)
        liveDinoList.value = tempList
        CoroutineScope(Dispatchers.IO).launch {
            db?.let {
                it.dinoStatsDao().delete(d)
            }
        }
    }

}