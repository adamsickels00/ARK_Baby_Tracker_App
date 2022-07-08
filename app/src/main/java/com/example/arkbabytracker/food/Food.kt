package com.example.arkbabytracker.food

enum class Food(var value: Int, val stackSize:Int, val SpoilTime:Int){
    RawMeat(50,40,2400), Mejoberries(30,100,2400), Berries(20,100,2400)
}