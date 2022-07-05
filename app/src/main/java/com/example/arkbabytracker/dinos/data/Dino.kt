package com.example.arkbabytracker.dinos.data

import com.example.arkbabytracker.food.Food
import java.util.*

enum class Diet{
    HERB, OMNI, CARN
}

val herbEatOrder = listOf(Food.Berries, Food.Mejoberries)
val carnEatOrder = listOf(Food.RawMeat)
val omniEatOrder = listOf(Food.Berries, Food.Mejoberries, Food.RawMeat)

abstract class Dino {
    val uniqueID = UUID.randomUUID().toString()
    var food:Float = 0.0f
    var elapsedTimeSec = 0
    abstract val name:String
    abstract val maturationHours: Int
    abstract val maturationMinutes: Int
    abstract val maturationSecs: Int
    abstract val diet: Diet

    fun eat(item:Food) {
        food += item.value
    }

    fun getTotalSeconds() : Int{
        var sec = 0
        sec += (maturationHours * 60 * 60)
        sec += (maturationMinutes * 60)
        sec +=  maturationSecs
        return sec
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
}

class Carbonemys: Dino(){
    override val name: String
        get() = "Carbonemys"
    override val maturationHours: Int
        get() = 23
    override val maturationMinutes: Int
        get() = 8
    override val maturationSecs: Int
        get() = 53
    override val diet: Diet
        get() = Diet.HERB
}