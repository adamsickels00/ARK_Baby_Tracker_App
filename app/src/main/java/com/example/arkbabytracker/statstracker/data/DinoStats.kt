package com.example.arkbabytracker.statstracker.data

import androidx.room.*
import com.example.arkbabytracker.troughtracker.dinos.data.Dino

@Entity
data class DinoStats(
    val type:String,
    val health:Int,
    val stamina:Int,
    val oxygen:Int,
    val food:Int,
    val weight:Int,
    val movementSpeed:Int,
    val torpor:Int,
    val damage:Int,
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}

@Dao
interface DinoStatsDao{
    @Insert
    fun insert(d : DinoStats)

    @Insert
    fun insertAll(d:List<DinoStats>)

    @Delete
    fun delete(d:DinoStats)

    @Query("SELECT * FROM dinostats")
    fun getAllDinos():List<DinoStats>

    @MapInfo(keyColumn = "type")
    @Query("SELECT * FROM dinostats GROUP BY type")
    fun getDinosByType():Map<String,List<DinoStats>>
}

@Database(entities = [DinoStats::class], version = 2)
abstract class DinoStatsDatabase : RoomDatabase(){
    abstract fun dinoStatsDao(): DinoStatsDao
}