package com.example.arkbabytracker.dinos.data
//Values here are inspired by Crumplecorn (https://github.com/Crumplecorn/ARK-Breeding-Calculator/blob/master/controller.js)
import com.example.arkbabytracker.data.EnvironmentViewModel
import com.example.arkbabytracker.food.Food
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

enum class Diet(val eatOrder:List<Food>){
    HERB(listOf(Food.Berries, Food.Mejoberries)),
    OMNI(listOf(Food.Berries, Food.Mejoberries, Food.RawMeat)),
    CARN(listOf(Food.RawMeat)),
    CARRION(listOf(Food.SpoiledMeat)),
    PISCIVORE(listOf(Food.FishMeat)),
    ARCHAEOPTERYX(listOf(Food.Chitin)),
    BLOODSTALKER(listOf(Food.BloodPack)), // Could add raw meat carrion
    CRYSTALWYVERN(listOf(Food.PrimalCrystal)),
    MAGMASAUR(listOf(Food.Ambergris)),
    MICRORAPTOR(listOf(Food.RawMeat,Food.RareFlower)),
    ROCKDRAKE(listOf(Food.NamelessVenom)),
    WYVERN(listOf(Food.WyvernMilk))
}

const val BASE_MIN_FOOD_RATE = 0.000155

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
    var food = minFood
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
        return (this.food + food.value*env.maewingFoodMultiplier.value!!) < (minFood * (1-percentComplete))+ (maxFood * percentComplete)
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


class Allosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Allosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Allosaurus(maxFood,env)
    }
}
class Anglerfish(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Anglerfish"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Anglerfish(maxFood,env)
    }
}
class Ankylosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Ankylosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Ankylosaurus(maxFood,env)
    }
}
class Araneo(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001736
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.7
    override val name: String
        get() = "Araneo"
    override val diet: Diet
        get() = Diet.CARRION
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Araneo(maxFood,env)
    }
}
class Archaeopteryx(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001302
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 6.0
    override val name: String
        get() = "Archaeopteryx"
    override val diet: Diet
        get() = Diet.ARCHAEOPTERYX
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Archaeopteryx(maxFood,env)
    }
}
class Argentavis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Argentavis"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Argentavis(maxFood,env)
    }
}
class Arthropluera(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.8
    override val name: String
        get() = "Arthropluera"
    override val diet: Diet
        get() = Diet.CARRION
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Arthropluera(maxFood,env)
    }
}
class Astrodelphis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Astrodelphis"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Astrodelphis(maxFood,env)
    }
}
class Baryonyx(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Baryonyx"
    override val diet: Diet
        get() = Diet.PISCIVORE
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Baryonyx(maxFood,env)
    }
}
class Basilosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.8
    override val name: String
        get() = "Basilosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Basilosaurus(maxFood,env)
    }
}
class Beelzebufo(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Beelzebufo"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Beelzebufo(maxFood,env)
    }
}
class Bloodstalker(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Bloodstalker"
    override val diet: Diet
        get() = Diet.BLOODSTALKER
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Bloodstalker(maxFood,env)
    }
}
class Brontosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.007716
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Brontosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Brontosaurus(maxFood,env)
    }
}
class Bulbdog(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Bulbdog"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Bulbdog(maxFood,env)
    }
}
class Carnotaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Carnotaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Carnotaurus(maxFood,env)
    }
}
class Castoroides(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.5
    override val name: String
        get() = "Castoroides"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Castoroides(maxFood,env)
    }
}
class Chalicotherium(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.125
    override val name: String
        get() = "Chalicotherium"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Chalicotherium(maxFood,env)
    }
}
class Compsognathus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.4
    override val name: String
        get() = "Compsognathus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Compsognathus(maxFood,env)
    }
}
class CrystalWyvern(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.000185
    override val babyFoodRate: Double
    get() = 19.25
    override val extraBabyFoodRate: Double
    get() = 6.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 1.0
    override val name: String
    get() = "Crystal Wyvern"
    override val diet: Diet
    get() = Diet.CRYSTALWYVERN
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return CrystalWyvern(maxFood,env)
    }
}
class Daeodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.01
    override val babyFoodRate: Double
        get() = 5.0
    override val extraBabyFoodRate: Double
        get() = 8.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Daeodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Daeodon(maxFood,env)
    }
}
class Deinonychus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Deinonychus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Deinonychus(maxFood,env)
    }
}
class Dilophosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.4
    override val name: String
        get() = "Dilophosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Dilophosaurus(maxFood,env)
    }
}
class Dimetrodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001736
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Dimetrodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Dimetrodon(maxFood,env)
    }
}
class Dimorphodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001302
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.7
    override val name: String
        get() = "Dimorphodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Dimorphodon(maxFood,env)
    }
}
class Diplocaulus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Diplocaulus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Diplocaulus(maxFood,env)
    }
}
class Diplodocus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.007716
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Diplodocus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Diplodocus(maxFood,env)
    }
}
class Direbear(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Direbear"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Direbear(maxFood,env)
    }
}
class Direwolf(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Direwolf"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Direwolf(maxFood,env)
    }
}
class Dodo(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 6.0
    override val name: String
        get() = "Dodo"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Dodo(maxFood,env)
    }
}
class Doedicurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.6
    override val name: String
        get() = "Doedicurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Doedicurus(maxFood,env)
    }
}
class Dunkleosteus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.125
    override val name: String
        get() = "Dunkleosteus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Dunkleosteus(maxFood,env)
    }
}
class Electrophorus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Electrophorus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Electrophorus(maxFood,env)
    }
}
class Equus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Equus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Equus(maxFood,env)
    }
}
class Featherlight(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Featherlight"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Featherlight(maxFood,env)
    }
}
class Ferox(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Ferox"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Ferox(maxFood,env)
    }
}
class Gacha(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.01
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.8
    override val name: String
        get() = "Gacha"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Gacha(maxFood,env)
    }
}
class Gallimimus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.5
    override val name: String
        get() = "Gallimimus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Gallimimus(maxFood,env)
    }
}
class Gasbag(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002066
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Gasbag"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Gasbag(maxFood,env)
    }
}
class Giganotosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 45.0
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.39
    override val name: String
        get() = "Giganotosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Giganotosaurus(maxFood,env)
    }
}
class Gigantopithecus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.004156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.2
    override val name: String
        get() = "Gigantopithecus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Gigantopithecus(maxFood,env)
    }
}
class Glowtail(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Glowtail"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Glowtail(maxFood,env)
    }
}
class Hesperornis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001389
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.3
    override val name: String
        get() = "Hesperornis"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Hesperornis(maxFood,env)
    }
}
class Hyaenodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Hyaenodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Hyaenodon(maxFood,env)
    }
}
class Ichthyornis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Ichthyornis"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Ichthyornis(maxFood,env)
    }
}
class Ichthyosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.6
    override val name: String
        get() = "Ichthyosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Ichthyosaurus(maxFood,env)
    }
}
class Iguanodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Iguanodon"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Iguanodon(maxFood,env)
    }
}
class Jerboa(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.4
    override val name: String
        get() = "Jerboa"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Jerboa(maxFood,env)
    }
}
class Kairuku(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001389
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.3
    override val name: String
        get() = "Kairuku"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Kairuku(maxFood,env)
    }
}
class Kaprosuchus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Kaprosuchus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Kaprosuchus(maxFood,env)
    }
}
class Kentrosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.005341
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.8
    override val name: String
        get() = "Kentrosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Kentrosaurus(maxFood,env)
    }
}
class Lymantria(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.0
    override val name: String
        get() = "Lymantria"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Lymantria(maxFood,env)
    }
}
class Lystrosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 6.0
    override val name: String
        get() = "Lystrosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Lystrosaurus(maxFood,env)
    }
}
class Maewing(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.01
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Maewing"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Maewing(maxFood,env)
    }
}
class Magmasaur(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000385
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.5
    override val name: String
        get() = "Magmasaur"
    override val diet: Diet
        get() = Diet.MAGMASAUR
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Magmasaur(maxFood,env)
    }
}
class Mammoth(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.004133
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.125
    override val name: String
        get() = "Mammoth"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Mammoth(maxFood,env)
    }
}
class Managarmr(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Managarmr"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Managarmr(maxFood,env)
    }
}
class Manta(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Manta"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Manta(maxFood,env)
    }
}
class Mantis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Mantis"
    override val diet: Diet
        get() = Diet.CARRION
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Mantis(maxFood,env)
    }
}
class Megachelon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.01
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Megachelon"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Megachelon(maxFood,env)
    }
}
class Megalania(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001736
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Megalania"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Megalania(maxFood,env)
    }
}
class Megaloceros(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.3
    override val name: String
        get() = "Megaloceros"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Megaloceros(maxFood,env)
    }
}
class Megalodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Megalodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Megalodon(maxFood,env)
    }
}
class Megalosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001852
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Megalosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Megalosaurus(maxFood,env)
    }
}
class Megatherium(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Megatherium"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Megatherium(maxFood,env)
    }
}
class Mesopithecus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.0
    override val name: String
        get() = "Mesopithecus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Mesopithecus(maxFood,env)
    }
}
class Microraptor(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Microraptor"
    override val diet: Diet
        get() = Diet.MICRORAPTOR
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Microraptor(maxFood,env)
    }
}
class Morellatops(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.005341
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.0
    override val name: String
        get() = "Morellatops"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Morellatops(maxFood,env)
    }
}
class Mosasaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.005
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.5
    override val name: String
        get() = "Mosasaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Mosasaurus(maxFood,env)
    }
}
class Moschops(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001736
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Moschops"
    override val diet: Diet
        get() = Diet.OMNI
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Moschops(maxFood,env)
    }
}
class Onyc(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002893
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.3
    override val name: String
        get() = "Onyc"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Onyc(maxFood,env)
    }
}
class Otter(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.4
    override val name: String
        get() = "Otter"
    override val diet: Diet
        get() = Diet.PISCIVORE
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Otter(maxFood,env)
    }
}
class Oviraptor(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001302
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.4
    override val name: String
        get() = "Oviraptor"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Oviraptor(maxFood,env)
    }
}
class Ovis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Ovis"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Ovis(maxFood,env)
    }
}
class Pachycephalosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.5
    override val name: String
        get() = "Pachycephalosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Pachycephalosaurus(maxFood,env)
    }
}
class Pachyrhinosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Pachyrhinosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Pachyrhinosaurus(maxFood,env)
    }
}
class Paraceratherium(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.0035
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Paraceratherium"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Paraceratherium(maxFood,env)
    }
}
class Parasaurolophus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.5
    override val name: String
        get() = "Parasaurolophus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Parasaurolophus(maxFood,env)
    }
}
class Pegomastax(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.0
    override val name: String
        get() = "Pegomastax"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Pegomastax(maxFood,env)
    }
}
class Pelagornis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Pelagornis"
    override val diet: Diet
        get() = Diet.PISCIVORE
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Pelagornis(maxFood,env)
    }
}
class Phiomia(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Phiomia"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Phiomia(maxFood,env)
    }
}
class Plesiosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003858
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.8
    override val name: String
        get() = "Plesiosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Plesiosaurus(maxFood,env)
    }
}
class Procoptodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Procoptodon"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Procoptodon(maxFood,env)
    }
}
class Pteranodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Pteranodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Pteranodon(maxFood,env)
    }
}
class Pulmonoscorpius(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001929
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Pulmonoscorpius"
    override val diet: Diet
        get() = Diet.CARRION
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Pulmonoscorpius(maxFood,env)
    }
}
class Purlovia(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Purlovia"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Purlovia(maxFood,env)
    }
}
class Quetzalcoatlus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.0035
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.7
    override val name: String
        get() = "Quetzalcoatlus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Quetzalcoatlus(maxFood,env)
    }
}
class Raptor(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.5
    override val name: String
        get() = "Raptor"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Raptor(maxFood,env)
    }
}
class Ravager(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Ravager"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Ravager(maxFood,env)
    }
}
class Rex(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Rex"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Rex(maxFood,env)
    }
}
class RockDrake(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.000185
    override val babyFoodRate: Double
    get() = 25.5
    override val extraBabyFoodRate: Double
    get() = 20.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 1.0
    override val name: String
    get() = "Rock Drake"
    override val diet: Diet
    get() = Diet.ROCKDRAKE
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return RockDrake(maxFood,env)
    }
}
class RollRat(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.003156
    override val babyFoodRate: Double
    get() = 25.5
    override val extraBabyFoodRate: Double
    get() = 20.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 1.6
    override val name: String
    get() = "Roll Rat"
    override val diet: Diet
    get() = Diet.HERB
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return RollRat(maxFood,env)
    }
}
class Sabertooth(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Sabertooth"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Sabertooth(maxFood,env)
    }
}
class Sarcosuchus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001578
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Sarcosuchus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Sarcosuchus(maxFood,env)
    }
}
class Shinehorn(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000868
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Shinehorn"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Shinehorn(maxFood,env)
    }
}
class Shadowmane(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001157
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Shadowmane"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Shadowmane(maxFood,env)
    }
}
class SnowOwl(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.01
    override val babyFoodRate: Double
    get() = 4.72
    override val extraBabyFoodRate: Double
    get() = 20.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 1.7
    override val name: String
    get() = "Snow Owl"
    override val diet: Diet
    get() = Diet.CARN
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return SnowOwl(maxFood,env)
    }
}
class Spinosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002066
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.3
    override val name: String
        get() = "Spinosaurus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Spinosaurus(maxFood,env)
    }
}
class Stegosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.005341
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.8
    override val name: String
        get() = "Stegosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Stegosaurus(maxFood,env)
    }
}
class Tapejara(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Tapejara"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Tapejara(maxFood,env)
    }
}
class TerrorBird(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.001578
    override val babyFoodRate: Double
    get() = 25.5
    override val extraBabyFoodRate: Double
    get() = 20.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 2.0
    override val name: String
    get() = "Terror Bird"
    override val diet: Diet
    get() = Diet.CARN
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return TerrorBird(maxFood,env)
    }
}
class Therizinosaurus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.8
    override val name: String
        get() = "Therizinosaurus"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Therizinosaurus(maxFood,env)
    }
}
class ThornyDragon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.001543
    override val babyFoodRate: Double
    get() = 25.5
    override val extraBabyFoodRate: Double
    get() = 20.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 1.9
    override val name: String
    get() = "Thorny Dragon"
    override val diet: Diet
    get() = Diet.CARN
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return ThornyDragon(maxFood,env)
    }
}
class Thylacoleo(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.9
    override val name: String
        get() = "Thylacoleo"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Thylacoleo(maxFood,env)
    }
}
class Triceratops(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.003156
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Triceratops"
    override val diet: Diet
        get() = Diet.HERB
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Triceratops(maxFood,env)
    }
}
class Troodon(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 4.4
    override val name: String
        get() = "Troodon"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Troodon(maxFood,env)
    }
}
class Tropeognathus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.7
    override val name: String
        get() = "Tropeognathus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Tropeognathus(maxFood,env)
    }
}
class Tusoteuthis(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.005
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.5
    override val name: String
        get() = "Tusoteuthis"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Tusoteuthis(maxFood,env)
    }
}
class Velonasaur(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001543
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 2.0
    override val name: String
        get() = "Velonasaur"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Velonasaur(maxFood,env)
    }
}
class Vulture(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.001302
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 3.7
    override val name: String
        get() = "Vulture"
    override val diet: Diet
        get() = Diet.CARRION
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Vulture(maxFood,env)
    }
}
class Voidwyrm(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000185
    override val babyFoodRate: Double
        get() = 13.0
    override val extraBabyFoodRate: Double
        get() = 3.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Voidwyrm"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Voidwyrm(maxFood,env)
    }
}
class WoollyRhino(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
    get() = 0.003156
    override val babyFoodRate: Double
    get() = 25.5
    override val extraBabyFoodRate: Double
    get() = 20.0
    override val ageSpeed: Double
    get() = 0.000003
    override val ageSpeedMult: Double
    get() = 1.6
    override val name: String
    get() = "Woolly Rhino"
    override val diet: Diet
    get() = Diet.HERB
    override val percentMaxStarting: Double
    get() = .1

    override fun newInstance():Dino {
        return WoollyRhino(maxFood,env)
    }
}
class Wyvern(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.000185
    override val babyFoodRate: Double
        get() = 13.0
    override val extraBabyFoodRate: Double
        get() = 3.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 1.0
    override val name: String
        get() = "Wyvern"
    override val diet: Diet
        get() = Diet.WYVERN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Wyvern(maxFood,env)
    }
}
class Yutyrannus(maxFood: Double,env:EnvironmentViewModel): Dino(maxFood,env){
    override val baseFoodRate: Double
        get() = 0.002314
    override val babyFoodRate: Double
        get() = 25.5
    override val extraBabyFoodRate: Double
        get() = 20.0
    override val ageSpeed: Double
        get() = 0.000003
    override val ageSpeedMult: Double
        get() = 0.5
    override val name: String
        get() = "Yutyrannus"
    override val diet: Diet
        get() = Diet.CARN
    override val percentMaxStarting: Double
        get() = .1

    override fun newInstance():Dino {
        return Yutyrannus(maxFood,env)
    }
}