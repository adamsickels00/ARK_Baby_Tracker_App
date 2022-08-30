package com.example.arkbabytracker.tribes

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TribeViewModel @Inject constructor(val tribeRepository: TribeRepository):ViewModel() {

    val userId = Firebase.auth.currentUser!!.uid
    fun createTribe(tribeName:String){
        tribeRepository.createNewTribe(tribeName,userId)
    }

     fun getUserTribe():String?{
        return tribeRepository.getUserTribe()
    }

    fun leaveTribe(tribeKey:String?){
        tribeKey?.let{tribeRepository.removeUserFromTribe(userId)}
    }
}