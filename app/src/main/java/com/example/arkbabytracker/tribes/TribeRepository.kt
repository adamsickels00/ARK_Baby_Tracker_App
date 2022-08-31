package com.example.arkbabytracker.tribes

import androidx.compose.runtime.MutableState
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

interface TribeRepository {
    fun addTribeStateListener(callback:(Tribe?)->Unit)

    fun addDinoToTribe(dinoId:String)

    fun removeDino(dinoId: String)

    fun getTribeUUIDOnce(callback: (String?) -> Unit):Task<DataSnapshot>

    fun addThisUserToTribe(userId:String, tribeKey:String)

    fun removeUserFromTribe(userID:String)

    fun createNewTribe(name:String, firstMemberId:String)

    fun removeTribe(tribeId:String)
}

