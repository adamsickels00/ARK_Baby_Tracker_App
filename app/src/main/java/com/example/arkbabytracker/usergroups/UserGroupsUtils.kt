package com.example.arkbabytracker.usergroups

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UserGroupsUtils {
    const val GROUP_KEY = "GroupList"

    fun loadGroups(c: Context?):List<String>{
        if(c == null) {
            return listOf()
        }
        val jsonString = c.getSharedPreferences(GROUP_KEY,Context.MODE_PRIVATE).getString(GROUP_KEY,"")
        val type = object:TypeToken<List<String>>(){}.type
        return Gson().fromJson(jsonString,type)
    }
}