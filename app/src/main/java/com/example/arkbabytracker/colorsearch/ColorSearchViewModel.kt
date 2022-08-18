package com.example.arkbabytracker.colorsearch

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.statstracker.data.DinoGender
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

    private fun dinosCanProduce(d1:DinoStats,d2:DinoStats,colors:List<Int>):Boolean{
        var canProduce = (d1.gender != d2.gender) || (d1.gender == DinoGender.Other && d2.gender == DinoGender.Other)
        for((index,color) in d1.colorList.withIndex()){
            canProduce = canProduce && ((color in colors || d2.colorList[index] in colors ) || colors[index] == -1)
        }
        return canProduce
    }

    //Given a list of color ids, return only the dinos with matching colors
    fun getDinoListWithColors(currentDinoList:List<DinoStats>):List<DinoStats>{
        var filteredList = currentDinoList.filter { it.type == selectedType.value }
        val resultsSet = mutableSetOf<DinoStats>()
        for((index,d1) in filteredList.withIndex()){
            for(innerIndex in index until filteredList.size){
                if(dinosCanProduce(d1,filteredList[innerIndex],colorsFilter)){
                    resultsSet.addAll(listOf(d1,filteredList[innerIndex]))
                }
            }
        }

        return resultsSet.toList()
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