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
    var length:Int
){
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
}

@Dao
interface TimerDao{
    @Insert
    suspend fun insert(t : Timer)

    @Insert
    suspend fun insertAll(t:List<Timer>)

    @Delete
    suspend fun delete(t:Timer)

    @Query("SELECT * FROM timer")
    fun getAllTimers(): Flow<List<Timer>>

    @Query("SELECT * FROM timer")
    fun getAllTimersOnce(): List<Timer>

}

@Database(entities = [Timer::class], version = 1, exportSchema = true,)
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


