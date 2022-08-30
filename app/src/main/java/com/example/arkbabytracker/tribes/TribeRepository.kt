package com.example.arkbabytracker.tribes

import io.reactivex.rxjava3.core.Observable

interface TribeRepository {
    fun getUserTribe(): String?

    fun addThisUserToTribe(userId:String, tribeKey:String)

    fun removeUserFromTribe(userID:String)

    fun createNewTribe(name:String, firstMemberId:String)

    fun removeTribe(tribeId:String)
}

