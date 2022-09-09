package com.example.arkbabytracker.statstracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

interface DinoStatsRepository {

    fun insert(d : DinoStats)

    @Insert
    fun insertAll(d:List<DinoStats>)

    fun delete(d:DinoStats)

    fun update(d:DinoStats)

    fun getDinoById(id:Int):DinoStats

    fun getAllDinos():List<DinoStats>

    fun getAllUserDinos():List<DinoStats>

    fun getAllDinosFlow(): Flow<List<DinoStats>>
}