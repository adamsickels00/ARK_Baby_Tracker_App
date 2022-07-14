package com.example.arkbabytracker.data.database

import androidx.room.*
import com.example.arkbabytracker.data.EnvironmentViewModel
import com.example.arkbabytracker.dinos.data.Diet
import com.example.arkbabytracker.dinos.data.Dino
import com.example.arkbabytracker.dinos.data.allDinoList
import java.sql.Time
import java.time.Instant
import kotlin.reflect.full.primaryConstructor

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Instant {
        return Instant.ofEpochSecond(value)
    }

    @TypeConverter
    fun dateToLong(date: Instant): Long {
        return date.epochSecond
    }

    @TypeConverter
    fun dietToString(diet:Diet):String{
        return diet.name
    }

    @TypeConverter
    fun stringToDiet(diet:String):Diet{
        return when(diet){
            "HERB"->Diet.HERB
            "CARN"->Diet.CARN
            "OMNI"->Diet.OMNI
            else-> Diet.OMNI
        }
    }
}

@Entity(tableName = "Dino")
data class DinoEntity(
    @PrimaryKey
    val id:String,
    val name:String,
    val startTime:Instant,
    val maxFood:Double
){
    companion object{
        fun fromDino(dino:Dino):DinoEntity{
            return DinoEntity(
                dino.uniqueID,
                dino.name,
                dino.startTime,
                dino.maxFood
            )
        }

        fun toDino(entity:DinoEntity,env:EnvironmentViewModel):Dino?{
            allDinoList.forEach{
                if(entity.name == it.simpleName){
                    val newDino = it.primaryConstructor!!.call(
                        entity.maxFood,
                        env
                    )
                    newDino.uniqueID = entity.id
                    newDino.startTime = entity.startTime
                    return newDino
                }
            }
            return null
        }
    }
}

@Dao
interface DinoDao{
    @Insert
    fun add(d:DinoEntity)

    @Insert
    fun addAll(d:List<DinoEntity>)

    @Query("DELETE FROM Dino WHERE Dino.id=:id")
    fun delete(id:String)

    @Query("DELETE FROM Dino WHERE 1=1")
    fun deleteAll()

    @Query("SELECT * FROM Dino")
    fun getAll():List<DinoEntity>

}

@Database(entities = [DinoEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class DinoDatabase : RoomDatabase() {
    abstract fun dinoDao(): DinoDao
}


