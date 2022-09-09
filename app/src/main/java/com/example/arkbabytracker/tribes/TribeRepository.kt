package com.example.arkbabytracker.tribes

import androidx.compose.runtime.MutableState
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

interface TribeRepository {
    fun addTribeStateListener(callback:(Tribe?)->Unit)

    fun addDinoToTribe(dinoId:String)

    suspend fun getAllDinos():List<DinoEntity>

    fun getAllDinoStats():List<DinoStats>

    fun addStatsDinoToTribe(dinoId:String)

    fun removeDino(dinoId: String)

    fun removeStatsDino(dinoId: String)

    fun getTribeUUIDOnce(callback: (String?) -> Unit):Task<DataSnapshot>
    fun getTribeOnce(callback: (Tribe?) -> Unit):Task<DataSnapshot>

    fun addThisUserToTribe(userId:String, tribeKey:String)

    fun removeUserFromTribe(userID:String)

    fun createNewTribe(name:String):String

    fun removeTribe(tribeId:String)
}

