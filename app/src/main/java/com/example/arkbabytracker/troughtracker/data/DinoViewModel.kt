package com.example.arkbabytracker.troughtracker.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.troughtracker.data.database.DinoDao
import com.example.arkbabytracker.troughtracker.data.database.DinoDatabase
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.example.arkbabytracker.troughtracker.dinos.data.*
import com.example.arkbabytracker.troughtracker.food.Food
import com.example.arkbabytracker.troughtracker.food.trough.Trough
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import kotlin.concurrent.fixedRateTimer


class DinoViewModel:ViewModel() {

    var foodStacks:MutableLiveData<MutableMap<Food,Int>> = MutableLiveData(mutableMapOf())

    var babyList : MutableLiveData<MutableList<Dino>> = MutableLiveData(mutableListOf())

    var currentSimBabyList = MutableLiveData(mutableListOf<Dino>())

    var trough = Trough(foodStacks.value!!)



    var simTrough = MutableLiveData(Trough(foodStacks.value!!))

    val troughRefill = mutableMapOf<Long,Map<Food,Int>>()

    var remainingTime = MutableLiveData(0)
    var timerEndTime = 0L
    private var noTimer=true
    lateinit var dinoDao:DinoDao


    fun getFromDatabase(db:DinoDatabase,env:EnvironmentViewModel,group:String):DinoViewModel{
        babyList.postValue(db.dinoDao().getAll().filter { it.groupName == group }.map { DinoEntity.toDino(it,env)!! }.toMutableList())
        return this
    }

    fun deleteDino(d:Dino){
        dinoDao.delete(d.uniqueID)
    }

    suspend fun runSim():Int{
        var time = 0
        var run = true

        var tempBabyList:MutableList<Dino> = babyList.value?.map { it.copy() } as MutableList<Dino>
        tempBabyList.forEach { it.food = it.currentMaxFood }
        runSimFromStartToNow()
        tempBabyList.zip(currentSimBabyList.value!!){a:Dino,b:Dino-> a.food = b.food}
        synchronized(this) {
            trough = Trough(foodStacks.value!!)
            while (run) {
                run = processSecond(tempBabyList, time,trough)
                time++
            }
        }
        return time-1
    }

    private suspend fun runSimFromStartToNow():Int{
        var time = 0
        var run = true
        var dinoList = mutableListOf<Dino>()
        val babyAddTimesPair = getBabyAddTimes()
        val maxElapsedTime = babyAddTimesPair.second
        var babyAddTimes = babyAddTimesPair.first
        var tempTrough = Trough(foodStacks.value!!)
        babyAddTimes.forEach { it.first.food = it.first.currentMaxFood }


        synchronized(this) {
            //While we have food and are not yet to now
            while (time<=maxElapsedTime) {

                val simStartTime = Instant.now().epochSecond - maxElapsedTime.toInt()

                troughRefill[simStartTime + time]?.let {
                    tempTrough = Trough(it.toMutableMap())
                }

                //Add the dinos that exist at this time
                babyAddTimes.forEach {
                    if(it.second <= time){
                        //Add dino as if it started from 0 maturation
                        dinoList.add(it.first.blankCopy())
                    }
                }
                //Remove the dinos just added from the queue
                babyAddTimes = babyAddTimes.filter { it.second > time }

                //Run one second on each dino, preventing them from being removed

                processSecond(dinoList, time,tempTrough)
                dinoList.forEach { if(it.food<=0) it.food = 0.0 }
                        //Count that second
                time++
            }
        }

        currentSimBabyList.postValue(dinoList)
        simTrough.postValue(tempTrough)
        return time-1
    }

    private fun getBabyAddTimes(): Pair<List<Pair<Dino,Double>>,Double> {
        if (babyList.value!!.size <= 0)
            return listOf<Pair<Dino,Double>>() to 0.0
        val nnList = babyList.value!!
        var maxElapsedTime = nnList[0].elapsedTimeSec

        nnList.forEach {
            it.elapsedTimeSec = (Instant.now().epochSecond-it.startTime.epochSecond).toDouble()
            if (it.elapsedTimeSec > maxElapsedTime) {
                maxElapsedTime = it.elapsedTimeSec
            }
        }
        return nnList.map { it to maxElapsedTime - it.elapsedTimeSec } to maxElapsedTime
    }

    fun processSecond(tempBabyList:MutableList<Dino>,time:Int,trough:Trough):Boolean{
        var allGood = true
        if(tempBabyList.size == 0){
            return false
        } else {
            val removeDinos = mutableSetOf<Dino>()
            for (dino in tempBabyList) {
                dino.processSec()
                if (dino.elapsedTimeSec >= dino.maturationTimeSec) {
                    removeDinos.add(dino)
                } else {
                    feedIfHungry(dino,trough)
                    removeIfSpoiled(time,trough)
                }
                allGood = allGood && dino.food>0
            }
            removeDinos.forEach{tempBabyList.remove(it)}
        }
        return allGood
    }

    private fun feedIfHungry(d: Dino,trough: Trough):Boolean{
        var noFood = false
        val foodList = d.diet.eatOrder
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
    private fun removeIfSpoiled(time:Int,trough: Trough){
        for (food in trough.foodSet){
            if(time%food.SpoilTime==0){
                trough.spoilFood(food)
            }
        }
    }



    fun launchUpdateThread(){
        synchronized(this) {
            if (noTimer) {
                noTimer = false
                fixedRateTimer("Dino time left", true, period = 1000) {
                    CoroutineScope(Dispatchers.Default).launch {
                        if(remainingTime.value!! > 0) {
                            remainingTime.postValue((timerEndTime - Instant.now().epochSecond).toInt())
                            runSimFromStartToNow()
                        }
                    }
                }
            }
        }

    }

}