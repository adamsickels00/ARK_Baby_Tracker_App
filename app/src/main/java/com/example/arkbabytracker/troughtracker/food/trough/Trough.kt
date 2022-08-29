package com.example.arkbabytracker.troughtracker.food.trough

import com.example.arkbabytracker.troughtracker.food.Food

class Trough(foodMap: MutableMap<Food,Double>) {
    private var troughContents : MutableList<Pair<Food, Int>> = mutableListOf()
    val size: Int
        get() = troughContents.size
    var foodSet:MutableSet<Food> = mutableSetOf()
    init {
        foodMap.forEach{
            val numFood = (it.value * it.key.stackSize).toInt()
            val numStacks = it.value.toInt()
            val numRemaining = numFood % it.key.stackSize
            for (stack in 1..numStacks){
                troughContents.add(Pair(it.key,it.key.stackSize))
                foodSet.add(it.key)
            }
            troughContents.add(it.key to numRemaining)
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
                decList.add(i)
            }
        }
        for (index in decList){
            decrementAt(index)
        }
        troughContents = troughContents.filter { it.second>0 } as MutableList<Pair<Food, Int>>
    }

    private fun decrementAt(index:Int){
        val newVal = troughContents[index].second-1
        troughContents[index] = Pair(troughContents[index].first,newVal)
    }

    fun get(index:Int):Pair<Food,Int>{
        return troughContents[index]
    }

    operator fun iterator():Iterator<Pair<Food,Int>>{
        return troughContents.iterator()
    }
}