package com.example.arkbabytracker.data

import com.example.arkbabytracker.dinos.data.Carbonemys
import com.example.arkbabytracker.dinos.data.Dino
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.food.Food


class DinoViewModel:ViewModel() {
    var foodStacks:MutableMap<Food,Int> = mutableMapOf(
        Pair(Food.RawMeat,0),
        Pair(Food.Mejoberries,0),
        Pair(Food.Berries,0))

    var babyList : MutableList<Dino> = mutableListOf()

    fun populateLists():DinoViewModel{
        foodStacks[Food.RawMeat] = 30
        foodStacks[Food.Berries] = 20

        babyList.add(Carbonemys())
        return this
    }
}