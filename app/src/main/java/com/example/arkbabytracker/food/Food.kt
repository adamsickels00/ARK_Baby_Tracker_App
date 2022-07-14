package com.example.arkbabytracker.food

enum class Food(var value: Int, val stackSize:Int, val SpoilTime:Int){
    RawMeat(50,40,2400),
    Mejoberries(30,100,2400),
    Berries(20,100,2400),
    FishMeat(25,40,4800),
    SpoiledMeat(50,100,14400),
    Chitin(50,100,99999999),
    RareFlower(60,100,24*60*60*3),
    WyvernMilk(1200,1,7200),
    Mutagen(1000,1,7200),
    PrimalCrystal(350,1,21600),
    Ambergris(500,1,4800),
    NamelessVenom(400,1,7200),
    BloodPack(200,100,7200),

}