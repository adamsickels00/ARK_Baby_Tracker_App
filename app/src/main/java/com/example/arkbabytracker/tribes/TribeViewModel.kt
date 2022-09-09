package com.example.arkbabytracker.tribes

import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.statstracker.data.DinoStatsRepository
import com.example.arkbabytracker.troughtracker.data.DinoRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TribeViewModel @Inject constructor(val tribeRepository: TribeRepository, val dinoRepository: DinoRepository, val dinoStatsRepository: DinoStatsRepository):ViewModel() {

    val userId = Firebase.auth.currentUser!!.uid
    var isAdded = false

     fun addTribeStateListener(callback:(Tribe?)->Unit){
        tribeRepository.addTribeStateListener(callback)
    }

    fun leaveTribe(tribeKey:String?){
        CoroutineScope(Dispatchers.IO).launch {
            for(dino in dinoRepository.getAllUserDinos()){
                tribeRepository.removeDino(dino.id)
            }
            for(dinoStats in dinoStatsRepository.getAllUserDinos()){
                tribeRepository.removeStatsDino(dinoStats.id)
            }
            tribeKey?.let{tribeRepository.removeUserFromTribe(userId)}

        }
    }

    fun joinTribe(tribeKey: String){
        CoroutineScope(Dispatchers.IO).launch {
            val userDinos = dinoRepository.getAllDinos()
            tribeRepository.addThisUserToTribe(userId,tribeKey)
            for(dino in userDinos){
                //Add all user dinos to the tribe
                tribeRepository.addDinoToTribe(dino.id)
            }
            for(dinoStats in dinoStatsRepository.getAllDinos()){
                tribeRepository.addStatsDinoToTribe(dinoStats.id.toString())
            }
        }
    }

    fun createTribe(name:String){
        val tribe = tribeRepository.createNewTribe(name)
        CoroutineScope(Dispatchers.IO).launch {
            val userDinos = dinoRepository.getAllDinos()
            tribeRepository.addThisUserToTribe(userId,tribe)
            for(dino in userDinos){
                //Add all user dinos to the tribe
                tribeRepository.addDinoToTribe(dino.id)
            }
            for(dinoStats in dinoStatsRepository.getAllDinos()){
                tribeRepository.addStatsDinoToTribe(dinoStats.id.toString())
            }
        }
    }
}