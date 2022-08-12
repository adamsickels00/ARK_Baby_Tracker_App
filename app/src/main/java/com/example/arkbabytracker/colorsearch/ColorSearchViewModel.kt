package com.example.arkbabytracker.colorsearch

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.statstracker.data.DinoStatsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ColorSearchViewModel @Inject constructor(
    val dinoStatsDao: DinoStatsDao
):ViewModel() {

    val dinoList: Flow<List<DinoStats>> = dinoStatsDao.getAllDinosFlow()

    val defaultColorsFilter = listOf(-1,-1,-1,-1,-1,-1)

    val colorsFilter = mutableStateListOf(-1,-1,-1,-1,-1,-1)
    val selectedType = mutableStateOf("Rex")

    //Given a list of color ids, return only the dinos with matching colors
    fun getDinoListWithColors(currentDinoList:List<DinoStats>):List<DinoStats>{
        var filteredList = currentDinoList.filter { it.type == selectedType.value }
        for ((index, colorId) in colorsFilter.withIndex()){
            filteredList = filteredList.filter { colorId == -1 || it.colorList[index] == colorId}
        }
        return filteredList
    }

    fun getCurrentDinoTypes(currentDinoList:List<DinoStats>):Set<String>{
        val types = mutableSetOf<String>()
        for(d in currentDinoList){
            types.add(d.type)
        }
        return types
    }

    fun getColorLists(currentDinoList: List<DinoStats>):MutableList<MutableSet<Int>>{
        val result:MutableList<MutableSet<Int>> = mutableListOf()

        for(d in getDinoListWithColors(currentDinoList)){
            for((index,c) in d.colorList.withIndex()){
                if(result.size == index){
                    result.add(index,mutableSetOf())
                }
                result[index].add(c)
            }
        }
        return result
    }

    fun resetColorsFilter(){
        colorsFilter.clear()
        colorsFilter.addAll(defaultColorsFilter)
    }
}