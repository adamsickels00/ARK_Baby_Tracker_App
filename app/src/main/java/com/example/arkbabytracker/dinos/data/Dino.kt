package com.example.arkbabytracker.dinos.data

import com.example.arkbabytracker.data.EnvironmentViewModel
import com.example.arkbabytracker.food.Food
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

enum class Diet{
    HERB, OMNI, CARN
}

const val BASE_MIN_FOOD_RATE = 0.000155

val herbEatOrder = listOf(Food.Berries, Food.Mejoberries)
val carnEatOrder = listOf(Food.RawMeat)
val omniEatOrder = listOf(Food.Berries, Food.Mejoberries, Food.RawMeat)
val allDinoList: List<KClass<out Dino>> = Dino::class.sealedSubclasses

sealed class Dino(val maxFood: Double,val env:EnvironmentViewModel) {
    var uniqueID = UUID.randomUUID().toString()
    val minFoodDrainPerSec:Double = 0.000155
    abstract val baseFoodRate:Double
    abstract val babyFoodRate:Double
    abstract val extraBabyFoodRate:Double
    abstract val ageSpeed:Double
    abstract val ageSpeedMult:Double
    abstract val percentMaxStarting:Double
    val minFood = maxFood*percentMaxStarting
    var elapsedTimeSec = 0.0
    var food = 0.0
    var maturationTimeSec = 1/ this.ageSpeed /ageSpeedMult/env.eventMultiplier.value!!
    private val maxFoodRate = baseFoodRate*extraBabyFoodRate*babyFoodRate
    private val minFoodRate = BASE_MIN_FOOD_RATE*babyFoodRate*extraBabyFoodRate
    private var percentComplete = 0.0
    private var currentFoodRate = 0.0
    var startTime: Instant = Instant.now()
    abstract val name:String
    abstract val diet: Diet

    fun setEventMultiplier(mult:Double){
        maturationTimeSec = 1/ageSpeed/ageSpeedMult/mult
    }

    fun eat(item:Food) {
        food += item.value * env.maewingFoodMultiplier.value!!
    }
    fun processSec(){
        currentFoodRate =maxFoodRate*(1-percentComplete) + minFoodRate*(percentComplete)
        elapsedTimeSec+=1
        food -= currentFoodRate
        percentComplete = elapsedTimeSec/maturationTimeSec
        val foodChange = (1/maturationTimeSec)*(maxFood-minFood)
        food -= foodChange
    }

    fun canEat(food:Food):Boolean{
        return (this.food + food.value*env.maewingFoodMultiplier.value!!) < 0
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            !is Dino -> false
            else -> {
                var res = true
                res = res && (other.name == this.name)
                res = res && (other.elapsedTimeSec == this.elapsedTimeSec)
                res = res && (other.food == this.food)
                res
            }
        }
    }

    fun setPercentMature(percent:Double){
        percentComplete = percent/100
        elapsedTimeSec = percent*maturationTimeSec/100
        startTime = startTime.minusSeconds(elapsedTimeSec.toLong())
        currentFoodRate = maxFoodRate*(1-percentComplete) + minFoodRate*(percentComplete)
    }

    abstract fun newInstance():Dino

    fun copy():Dino{
        val newDino = this.newInstance()
        newDino.elapsedTimeSec = this.elapsedTimeSec
        newDino.food = this.food
        newDino.currentFoodRate = this.currentFoodRate
        newDino.percentComplete = this.percentComplete
        return newDino
    }

    override fun hashCode(): Int {
        var result = uniqueID.hashCode()
        result = 31 * result + minFoodDrainPerSec.hashCode()
        result = 31 * result + baseFoodRate.hashCode()
        result = 31 * result + babyFoodRate.hashCode()
        result = 31 * result + extraBabyFoodRate.hashCode()
        result = 31 * result + ageSpeed.hashCode()
        result = 31 * result + ageSpeedMult.hashCode()
        result = 31 * result + elapsedTimeSec.hashCode()
        result = 31 * result + food.hashCode()
        result = 31 * result + maturationTimeSec.hashCode()
        result = 31 * result + maxFoodRate.hashCode()
        result = 31 * result + minFoodRate.hashCode()
        result = 31 * result + percentComplete.hashCode()
        result = 31 * result + currentFoodRate.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + diet.hashCode()
        return result
    }
}

class Carbonemys(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.0
    override val name: String
        get() = "Carbonemys"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = 0.10

    override fun newInstance():Dino {
        return Carbonemys(maxFood,env)
    }
}