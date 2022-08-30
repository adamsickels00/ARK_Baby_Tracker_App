package com.example.arkbabytracker.di

import com.example.arkbabytracker.statstracker.data.DinoStatsRepository
import com.example.arkbabytracker.statstracker.data.DinoStatsRepositoryRoom
import com.example.arkbabytracker.timers.TimerRepository
import com.example.arkbabytracker.timers.TimerRepositoryRoom
import com.example.arkbabytracker.tribes.TribeRepository
import com.example.arkbabytracker.tribes.TribeRepositoryFirebase
import com.example.arkbabytracker.troughtracker.data.DinoRepository
import com.example.arkbabytracker.troughtracker.data.DinoRepositoryFirebase
import com.example.arkbabytracker.troughtracker.data.DinoRepositoryRoom
import com.example.arkbabytracker.troughtracker.data.database.DinoDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
//Repositories will live same as the activity that requires them
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun providesDinoRepository(impl: DinoRepositoryFirebase): DinoRepository

    @Binds
    abstract fun providesTimerRepository(impl:TimerRepositoryRoom):TimerRepository

    @Binds
    abstract fun providesDinoStatsRepo(impl: DinoStatsRepositoryRoom): DinoStatsRepository

    @Binds
    abstract fun providesTribeRepo(impl: TribeRepositoryFirebase): TribeRepository

}
