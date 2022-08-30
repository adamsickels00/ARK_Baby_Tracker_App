package com.example.arkbabytracker.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
class ApplicationModule {
    @Provides
    @Named(USER_ID_INJECT_STRING)
    fun getUserID():String{
        return Firebase.auth.currentUser!!.uid
    }
}

const val USER_ID_INJECT_STRING = "USER_ID"