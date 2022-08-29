package com.example.arkbabytracker.timers

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TimerRepository{
    //    @Insert
//    fun insert(t : Timer):Long
//
    fun insert(t:Timer):Long
//    @Insert
    suspend fun insertAll(t:List<Timer>)

//    @Delete
    suspend fun delete(t:Timer)
//
//    @Query("DELETE FROM Timer WHERE Timer.groupName=:groupName")
    suspend fun deleteTimersForGroup(groupName: String)
//
//    @Query("SELECT * FROM timer")
    fun getAllTimers(): Flow<List<Timer>>
//
//    @Query("SELECT * FROM timer")
    suspend fun getAllTimersOnce(): List<Timer>
}


class TimerRepositoryRoom @Inject constructor(val timerDao: TimerDao):TimerRepository{
    override fun insert(t: Timer): Long {
        return timerDao.insert(t)
    }

    override suspend fun insertAll(t: List<Timer>) {
        timerDao.insertAll(t)
    }

    override suspend fun delete(t: Timer) {
        timerDao.delete(t)
    }

    override suspend fun deleteTimersForGroup(groupName: String) {
        timerDao.deleteTimersForGroup(groupName)
    }

    override fun getAllTimers(): Flow<List<Timer>> {
        return timerDao.getAllTimers()
    }

    override suspend fun getAllTimersOnce(): List<Timer> {
        return timerDao.getAllTimersOnce()
    }


}