package com.example.arkbabytracker.usergroups

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.usergroups.UserGroupsUtils.GROUP_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserGroupsViewModel : ViewModel(){
    private val groupList = mutableStateListOf<String>()
    fun addGroup(name:String,context: Context){
        if(name !in groupList) {
            groupList.add(name)
            updateSavedList(context)
        }
    }

    // TODO actually delete the whole group from database. this just hides it from user
    fun removeGroup(name:String,context:Context){
        groupList.remove(name)
        updateSavedList(context)
    }

    fun getGroupsObservable(context: Context):SnapshotStateList<String> {
        val pref = context.getSharedPreferences(GROUP_KEY,Context.MODE_PRIVATE)
        val jsonString = pref.getString(GROUP_KEY,"")
        val type = object: TypeToken<List<String>>(){}.type
        val _groupList = Gson().fromJson<MutableList<String>>(jsonString,type)?: mutableListOf()
        groupList.clear()
        groupList.addAll(_groupList)
        return groupList
    }

    private fun updateSavedList(context:Context){
        val pref = context.getSharedPreferences(GROUP_KEY,Context.MODE_PRIVATE)
        pref.edit{
            putString(GROUP_KEY, Gson().toJson(groupList.toList()))
        }
    }


}