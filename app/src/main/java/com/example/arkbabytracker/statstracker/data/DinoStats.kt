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
    val colorList: List<Int>
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}
class Converters {
    @TypeConverter
    fun listToString(list: List<Int>): String {
        var str = ""
        list.forEach {
            str += "$it "
        }
        return str.trimEnd()
    }

    @TypeConverter
    fun stringToList(string: String): List<Int> {
        return string.split(' ').map { it.toInt() }
    }
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

@Database(entities = [DinoStats::class], version = 3)
@TypeConverters(Converters::class)
abstract class DinoStatsDatabase : RoomDatabase(){
    abstract fun dinoStatsDao(): DinoStatsDao
}