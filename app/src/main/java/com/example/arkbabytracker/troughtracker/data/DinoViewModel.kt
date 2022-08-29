package com.example.arkbabytracker.troughtracker.data

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.timers.Timer
import com.example.arkbabytracker.timers.TimerDao
import com.example.arkbabytracker.troughtracker.data.database.DinoDao
import com.example.arkbabytracker.troughtracker.data.database.DinoDatabase
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.example.arkbabytracker.troughtracker.dinos.data.*
import com.example.arkbabytracker.troughtracker.food.Food
import com.example.arkbabytracker.troughtracker.food.trough.Trough
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.time.Instant
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@HiltViewModel
class DinoViewModel @Inject constructor(var timerDao: TimerDao, val dinoDao: DinoDao):ViewModel() {

    var foodStacks:MutableLiveData<MutableMap<Food,Int>> = MutableLiveData(mutableMapOf())

    var babyList : MutableLiveData<MutableList<Dino>> = MutableLiveData(mutableListOf())

    var currentSimBabyList = MutableLiveData(mutableListOf<Dino>())

    var trough = Trough(foodStacks.value!!)




    var simTrough = MutableLiveData(Trough(foodStacks.value!!))

    val troughRefill = mutableMapOf<Long,Map<Food,Int>>()

    var remainingTime = MutableLiveData(0)
    var timerEndTime = 0L
    private var noTimer=true
    var updateJob: Job? = null


    fun getFromDatabase(env:EnvironmentViewModel,group:String):DinoViewModel{
        val dList = dinoDao.getAll().filter { it.groupName == group }.map { DinoEntity.toDino(it,env)!! }.toMutableList()
        val dListWithTimes = dList.map { it.elapsedTimeSec = (Instant.now().epochSecond).toDouble() - it.startTime.epochSecond;it }
        babyList.postValue(dListWithTimes.toMutableList())
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
        synchronized(this) {
            trough = Trough(foodStacks.value!!)
            while (run) {
                run = processSecond(tempBabyList, time,trough)
                time++
            }
            tempBabyList.forEach {
                    t-> babyList.value!!.forEach {
                        if(t.uniqueID == it.uniqueID){
                            it.hasEnoughFood = t.hasEnoughFood
                            it.food = t.food
                            it.finalMaxFood = t.currentMaxFood
                        }
                    }
            }
            babyList.value!!.forEach { dino -> if(dino.uniqueID !in tempBabyList.map { it.uniqueID }) dino.hasEnoughFood=true}
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

                troughRefill[simStartTime + time]?.let { foodMap ->
                    tempTrough = Trough(foodMap.toMutableMap())
                    dinoList.forEach { it.food = it.currentMaxFood }
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
                }
                dino.hasEnoughFood = if(dino.food > 0) null else false
                allGood = allGood && dino.food > 0
            }
            removeDinos.forEach{
                babyList.value!!.forEach {dino->
                    if(dino.uniqueID == it.uniqueID) {
                        dino.food = it.food
                        dino.finalMaxFood = it.currentMaxFood
                    }
                }
                tempBabyList.remove(it)
            }
            removeIfSpoiled(time,trough)
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

    fun clearOldTimers(){
        CoroutineScope(Dispatchers.IO).launch {
            for(t in timerDao.getAllTimersOnce()) {
                if ((t.startTime + t.length) <= Instant.now().epochSecond) {
                    timerDao.delete(t)
                }
            }
        }
    }

    fun insertTimer(timer:Timer):Single<Long>{
        return Single.create<Long>{
            it.onSuccess(timerDao.insert(timer))
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }



    fun launchUpdateThread(){
        synchronized(this) {
            if (updateJob==null){
                updateJob = CoroutineScope(Dispatchers.Default).launch {
                    while(true){
                        if(remainingTime.value!! > 0) {
//                            runSimFromStartToNow()
                            currentSimBabyList.postValue(babyList.value!!.onEach { it.elapsedTimeSec = (Instant.now().epochSecond - it.startTime.epochSecond).toDouble() })
                            clearOldTimers()
                        }
                        delay(1000)
                    }
                }
            }
        }

    }

    fun babyListAsString():String{
        var s = ""
        for(x in babyList.value?:listOf()){
            s += "${x.name}, "
        }
        return s.dropLast(2)
    }

}