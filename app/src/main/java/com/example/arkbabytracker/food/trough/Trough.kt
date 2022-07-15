package com.example.arkbabytracker.food.trough

import com.example.arkbabytracker.food.Food

class Trough(foodMap: MutableMap<Food,Int>) {
    private var troughContents : MutableList<Pair<Food, Int>> = mutableListOf()
    val size: Int
        get() = troughContents.size
    var foodSet:MutableSet<Food> = mutableSetOf()
    init {
        foodMap.forEach{
            for (stack in 1..it.value){
                troughContents.add(Pair(it.key,it.key.stackSize))
                foodSet.add(it.key)
            }
        }
    }
    fun hasFood(f: Food):Int{
        for((i,pair) in troughContents.withIndex()){
            if((pair.first == f) && (pair.second > 0)){
                return i
            }
        }
        return -1
    }

    fun eatFood(food: Food){
        val index = hasFood(food)
        if(index >=0){
            decrementAt(index)
        }
    }

    fun spoilFood(f: Food){

        val decList:MutableList<Int> = mutableListOf()
        for((i,pair) in troughContents.withIndex()){
            if(pair.first == f){
                decList.add(i-decList.size)
            }
        }
        for (index in decList){
            decrementAt(index)
        }
    }

    private fun decrementAt(index:Int){
        val newVal = troughContents[index].second-1
        if(newVal>0){
            troughContents[index] = Pair(troughContents[index].first,newVal)
        } else{
            troughContents.removeAt(index)
        }
    }

    fun get(index:Int):Pair<Food,Int>{
        return troughContents[index]
    }

    operator fun iterator():Iterator<Pair<Food,Int>>{
        return troughContents.iterator()
    }
}