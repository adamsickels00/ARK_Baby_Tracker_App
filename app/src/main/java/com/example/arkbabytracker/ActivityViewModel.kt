package com.example.arkbabytracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.troughtracker.food.trough.Trough

class ActivityViewModel : ViewModel() {
    val troughMap = (mutableMapOf<String, MutableLiveData<Trough>>())

}