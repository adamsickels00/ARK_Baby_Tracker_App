package com.example.arkbabytracker.statstracker.data

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.example.arkbabytracker.troughtracker.dinos.data.Dino


@Entity
data class DinoStats(
    val type:String,
    @ColumnInfo(defaultValue = "")
    val name:String,
    val health:Int,
    val stamina:Int,
    val oxygen:Int,
    val food:Int,
    val weight:Int,
    val movementSpeed:Int,
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
    fun insert(d : DinoStats):Long

    @Insert
    fun insertAll(d:List<DinoStats>)

    @Delete
    fun delete(d:DinoStats)

    @Update
    fun update(d:DinoStats)

    @Query("SELECT * FROM dinostats WHERE id=:id")
    fun getDinoById(id:Int):DinoStats

    @Query("SELECT * FROM dinostats")
    fun getAllDinos():List<DinoStats>

    @MapInfo(keyColumn = "type")
    @Query("SELECT * FROM dinostats GROUP BY type")
    fun getDinosByType():Map<String,List<DinoStats>>
}

@Database(entities = [DinoStats::class], version = 4, exportSchema = true, autoMigrations = [AutoMigration(from=3,to=4, spec = DinoStatsAutoMigration::class)])
@TypeConverters(Converters::class)
abstract class DinoStatsDatabase : RoomDatabase(){
    abstract fun dinoStatsDao(): DinoStatsDao
}

@DeleteColumn(tableName = "DinoStats", columnName = "torpor")
class DinoStatsAutoMigration:AutoMigrationSpec