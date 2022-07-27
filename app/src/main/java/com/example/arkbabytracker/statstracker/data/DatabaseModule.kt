package com.example.arkbabytracker.statstracker.data

import android.content.Context
import androidx.room.Room
import com.example.arkbabytracker.statstracker.DinoStatsFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DinoStatsDatabaseModule {
    @Provides
    fun provideDinoStatsDao(db:DinoStatsDatabase):DinoStatsDao{
        return db.dinoStatsDao()
    }

    @Singleton
    @Provides
    fun provideDinoStatsDb(@ApplicationContext context: Context):DinoStatsDatabase{
        return Room.databaseBuilder(
            context,
            DinoStatsDatabase::class.java, "dino-stats-database"
        ).fallbackToDestructiveMigration().build()
    }
}