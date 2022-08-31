package com.example.arkbabytracker.tribes

import androidx.compose.runtime.MutableState
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

     fun addTribeStateListener(callback:(Tribe?)->Unit){
        return tribeRepository.addTribeStateListener(callback)
    }

    fun leaveTribe(tribeKey:String?){
        tribeKey?.let{tribeRepository.removeUserFromTribe(userId)}
    }

    fun joinTribe(tribeKey: String){
        tribeRepository.addThisUserToTribe(userId,tribeKey)
    }
}