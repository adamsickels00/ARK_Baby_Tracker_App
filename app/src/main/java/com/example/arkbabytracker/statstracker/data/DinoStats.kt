package com.example.arkbabytracker.statstracker.data

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.annotation.Nullable

enum class DinoGender(){
    Male,
    Female,
    Other
}

@BindingAdapter("dinoGender")
fun setDinoGender(t: TextView, g:DinoGender){
    t.text="Gender: ${g.name}"
}

@Entity
data class DinoStats(
    val type:String="",
    @ColumnInfo(defaultValue = "")
    val name:String="",
    val health:Int=0,
    val stamina:Int=0,
    val oxygen:Int=0,
    val food:Int=0,
    val weight:Int=0,
    val movementSpeed:Int=0,
    val damage:Int=0,
    val colorList: List<Int> = listOf(),
    @ColumnInfo(defaultValue = "Other")
    val gender:DinoGender = DinoGender.Other
){
    @PrimaryKey()
    var id:String = UUID.randomUUID().toString()
    override fun equals(other: Any?): Boolean {
        if (other !is DinoStats) return false
        return this.id == other.id
    }
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

    @TypeConverter
    fun genderToString(g:DinoGender):String{
        return g.name
    }

    @TypeConverter
    fun stringToGender(s:String):DinoGender{
        for(g in DinoGender.values()){
            if(s==g.name){
                return g
            }
        }
        return DinoGender.Other
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

    @Query("SELECT * FROM dinostats")
    fun getAllDinosFlow(): Flow<List<DinoStats>>

}

@Database(entities = [DinoStats::class], version = 5, exportSchema = true, autoMigrations = [AutoMigration(from=3,to=4, spec = DinoStatsAutoMigration::class),AutoMigration(from=4,to=5)])
@TypeConverters(Converters::class)
abstract class DinoStatsDatabase : RoomDatabase(){
    abstract fun dinoStatsDao(): DinoStatsDao
}

@DeleteColumn(tableName = "DinoStats", columnName = "torpor")
class DinoStatsAutoMigration:AutoMigrationSpec