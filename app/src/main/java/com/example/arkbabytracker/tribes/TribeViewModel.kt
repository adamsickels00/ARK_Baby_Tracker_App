package com.example.arkbabytracker.tribes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TribeViewModel @Inject constructor(val tribeRepository: TribeRepository):ViewModel() {


    fun createTribe(tribeName:String,firstUserId:String){
        tribeRepository.createNewTribe(tribeName,firstUserId)
    }

     fun getUserTribe():String?{
        return tribeRepository.getUserTribe()
    }
}