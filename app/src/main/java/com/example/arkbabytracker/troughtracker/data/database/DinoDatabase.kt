package com.example.arkbabytracker.troughtracker.data.database

import android.content.Context
import androidx.room.*
import com.example.arkbabytracker.troughtracker.data.EnvironmentViewModel
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import com.example.arkbabytracker.troughtracker.dinos.data.allDinoList
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import javax.inject.Singleton
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
}

@Entity(tableName = "Dino")
data class DinoEntity(
    @PrimaryKey
    var id:String="",
    var name:String="",
    var startTime:Long= Instant.now().epochSecond,
    var maxFood:Double=0.0,
    @ColumnInfo(defaultValue = "Default")
    var groupName:String="Default",
    var owner:String=Firebase.auth.uid!!,
    var tribe:String?=null
){
    companion object{
        fun fromDino(dino:Dino):DinoEntity{
            return DinoEntity(
                dino.uniqueID,
                dino.name,
                dino.startTime.epochSecond,
                dino.maxFood,
                dino.groupName
            )
        }

        fun toDino(entity:DinoEntity,env:EnvironmentViewModel):Dino?{
            allDinoList.forEach{
                if(entity.name.replace(" ","") == it.simpleName){
                    val newDino = it.primaryConstructor!!.call(
                        entity.maxFood,
                        env
                    )
                    newDino.uniqueID = entity.id
                    newDino.startTime = Instant.ofEpochSecond(entity.startTime)
                    newDino.groupName = entity.groupName
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

    @Query("DELETE FROM Dino WHERE Dino.groupName=:groupName")
    fun deleteAllInGroup(groupName: String)

    @Query("SELECT * FROM Dino")
    fun getAll():List<DinoEntity>

}

@Database(entities = [DinoEntity::class], version = 3, autoMigrations = [AutoMigration(from = 1, to = 2)])
@TypeConverters(Converters::class)
abstract class DinoDatabase : RoomDatabase() {
    abstract fun dinoDao(): DinoDao
}

@InstallIn(SingletonComponent::class)
@Module
class DinoDatabaseModule {
    @Provides
    fun provideDinoDao(db: DinoDatabase): DinoDao {
        return db.dinoDao()
    }

    @Singleton
    @Provides
    fun provideDinoDb(@ApplicationContext context: Context): DinoDatabase {
        return Room.databaseBuilder(
            context,
            DinoDatabase::class.java, "dino-database"
        ).fallbackToDestructiveMigration().build()
    }
}


