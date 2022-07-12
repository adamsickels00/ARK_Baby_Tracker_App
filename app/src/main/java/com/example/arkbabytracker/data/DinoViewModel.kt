package com.example.arkbabytracker.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.data.database.DinoDao
import com.example.arkbabytracker.data.database.DinoDatabase
import com.example.arkbabytracker.data.database.DinoEntity
import com.example.arkbabytracker.dinos.data.*
import com.example.arkbabytracker.food.Food
import com.example.arkbabytracker.food.Trough


class DinoViewModel:ViewModel() {

    var foodStacks:MutableLiveData<MutableMap<Food,Int>> = MutableLiveData(mutableMapOf(
        Pair(Food.RawMeat,0),
        Pair(Food.Mejoberries,0),
        Pair(Food.Berries,0))
    )

    var babyList : MutableLiveData<MutableList<Dino>> = MutableLiveData(mutableListOf())

    var trough = Trough(foodStacks.value!!)

    fun getFromDatabase(db:DinoDatabase):DinoViewModel{
        babyList.postValue(db.dinoDao().getAll().map { DinoEntity.toDino(it)!! }.toMutableList())
        return this
    }

    fun saveToDatabase(db:DinoDatabase){
        db.dinoDao().deleteAll()
        db.dinoDao().addAll(babyList.value!!.map{DinoEntity.fromDino (it)})
    }

    suspend fun runSim():Int{
        var time = 0
        var run = true
        trough = Trough(foodStacks.value!!)
        var tempBabyList:MutableList<Dino> = babyList.value?.map { it.copy() } as MutableList<Dino>
        while(run){
            run = processSecond(tempBabyList,time)
            time++
        }
        return time-1
    }

    fun processSecond(tempBabyList:MutableList<Dino>,time:Int):Boolean{
        if(tempBabyList.size == 0){
            return false
        } else {
            for (dino in tempBabyList) {
                dino.processSec()
                if (dino.elapsedTimeSec >= dino.maturationTimeSec) {
                    tempBabyList.remove(dino)
                } else {
                    if (
                        !when (dino.diet) {
                            Diet.CARN -> {
                                feedIfHungry(dino, carnEatOrder)
                            }
                            Diet.HERB -> {
                                feedIfHungry(dino, herbEatOrder)
                            }
                            Diet.OMNI -> {
                                feedIfHungry(dino, omniEatOrder)
                            }
                        }
                    ) {

                        return false
                    }
                    removeIfSpoiled(time)
                }
            }
        }
        return true
    }

    private fun feedIfHungry(d: Dino, foodList:List<Food>):Boolean{
        var noFood = false
        for (f in foodList){
            if(d.canEat(f)){
                if((trough.hasFood(f))>=0){
                    d.eat(f)
                    trough.eatFood(f)
                }
            }
        }
        for (food in foodList) {
            noFood = noFood || (trough.hasFood(food)) >=0
        }
        return noFood
    }
    private fun removeIfSpoiled(time:Int){
        for (food in trough.foodSet){
            if(time%food.SpoilTime==0){
                trough.spoilFood(food)
            }
        }
    }


}