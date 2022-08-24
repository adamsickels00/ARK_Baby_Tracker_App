package com.example.arkbabytracker.timers

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.example.arkbabytracker.statstracker.data.DinoStatsDao
import com.example.arkbabytracker.statstracker.data.DinoStatsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Entity
data class Timer(
    var startTime:Long,
    var length:Int,
    @ColumnInfo(defaultValue = "")
    var description:String,
    @ColumnInfo(defaultValue = "")
    var groupName:String,
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}

@Dao
interface TimerDao{
    @Insert
    fun insert(t : Timer):Long

    @Insert
    suspend fun insertAll(t:List<Timer>)

    @Delete
    suspend fun delete(t:Timer)

    @Query("DELETE FROM Timer WHERE Timer.groupName=:groupName")
    fun deleteTimersForGroup(groupName: String)

    @Query("SELECT * FROM timer")
    fun getAllTimers(): Flow<List<Timer>>

    @Query("SELECT * FROM timer")
    fun getAllTimersOnce(): List<Timer>

}

@Database(entities = [Timer::class], version = 3, exportSchema = true, autoMigrations = [AutoMigration(from=1,to=2),AutoMigration(from=2,to=3)])
abstract class TimerDatabase : RoomDatabase(){
    abstract fun timerDao(): TimerDao
}

@InstallIn(SingletonComponent::class)
@Module
class TimerDatabaseModule {
    @Provides
    fun provideTimerDao(db: TimerDatabase): TimerDao {
        return db.timerDao()
    }

    @Singleton
    @Provides
    fun provideTimerDb(@ApplicationContext context: Context): TimerDatabase {
        return Room.databaseBuilder(
            context,
            TimerDatabase::class.java, "timer-database"
        ).fallbackToDestructiveMigration().build()
    }
}



