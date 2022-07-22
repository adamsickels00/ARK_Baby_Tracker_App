package com.example.arkbabytracker.troughtracker.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EnvironmentViewModel : ViewModel(){
    var eventMultiplier = MutableLiveData(1.0)
    var maewingFoodMultiplier = MutableLiveData(1.0)
}