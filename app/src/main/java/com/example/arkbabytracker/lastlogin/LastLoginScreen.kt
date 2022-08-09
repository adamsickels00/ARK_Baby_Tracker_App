package com.example.arkbabytracker.lastlogin

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.utils.TimeDisplayUtil
import java.time.Instant
import javax.inject.Inject



object LastLoginScreen {
    fun timeFromNowTo(instant: Long):String{
        val now = Instant.now().epochSecond
        return TimeDisplayUtil.secondsToDays((now - instant).toInt())
    }
}