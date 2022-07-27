package com.example.arkbabytracker

import android.app.Application
import com.example.arkbabytracker.statstracker.data.DinoStatsDatabaseModule
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DinoApplication : Application() {
}