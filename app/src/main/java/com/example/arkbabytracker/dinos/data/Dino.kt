package com.example.arkbabytracker.dinos.data

import com.example.arkbabytracker.food.Food
import java.util.*
import kotlin.math.min

enum class Diet{
    HERB, OMNI, CARN
}

val BASE_MIN_FOOD_RATE = 0.000155

val herbEatOrder = listOf(Food.Berries, Food.Mejoberries)
val carnEatOrder = listOf(Food.RawMeat)
val omniEatOrder = listOf(Food.Berries, Food.Mejoberries, Food.RawMeat)

abstract class Dino {
    val uniqueID = UUID.randomUUID().toString()
    val minFoodDrainPerSec:Double = 0.000155
    abstract val baseFoodRate:Double
    abstract val babyFoodRate:Double
    abstract val extraBabyFoodRate:Double
    abstract val ageSpeed:Double
    abstract val ageSpeedMult:Double

    var elapsedTimeSec = 0.0
    var food = 0.0
    val maturationTimeSec = 1/ageSpeed/ageSpeedMult
    private val maxFoodRate = baseFoodRate*extraBabyFoodRate*babyFoodRate
    private val minFoodRate = BASE_MIN_FOOD_RATE*babyFoodRate*extraBabyFoodRate
    private var percentComplete = 0.0
    private var currentFoodRate = 0.0
    abstract val name:String
    abstract val diet: Diet

    fun eat(item:Food) {
        food += item.value
    }
    fun processSec(){
        currentFoodRate =maxFoodRate*(1-percentComplete) + minFoodRate*(percentComplete)
        elapsedTimeSec+=1
        food -= currentFoodRate
        percentComplete = elapsedTimeSec/maturationTimeSec

    }

    fun canEat(food:Food):Boolean{
        return (this.food + food.value) < 0
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
        currentFoodRate = maxFoodRate*(1-percentComplete) + minFoodRate*(percentComplete)
    }

    abstract fun newInstance():Dino

    fun copy():Dino{
        var newDino = this.newInstance()
        newDino.elapsedTimeSec = this.elapsedTimeSec
        newDino.food = this.food
        newDino.currentFoodRate = this.currentFoodRate
        newDino.percentComplete = this.percentComplete
        return newDino
    }
}

class Carbonemys: Dino(){
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

    override fun newInstance():Dino {
        return Carbonemys()
    }
}