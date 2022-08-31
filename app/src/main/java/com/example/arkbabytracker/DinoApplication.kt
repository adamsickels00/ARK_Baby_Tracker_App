package com.example.arkbabytracker

import android.app.Application
import android.content.Context
import com.example.arkbabytracker.statstracker.data.DinoStatsDatabaseModule
import com.example.arkbabytracker.tribes.TribeRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DinoApplication : Application() {

}