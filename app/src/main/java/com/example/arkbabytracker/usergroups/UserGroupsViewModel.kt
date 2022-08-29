package com.example.arkbabytracker.usergroups

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.timers.TimerRepository
import com.example.arkbabytracker.troughtracker.data.DinoRepository
import com.example.arkbabytracker.usergroups.UserGroupsUtils.GROUP_KEY
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserGroupsViewModel @Inject constructor(val dinoRepository: DinoRepository, val timerRepository:TimerRepository) : ViewModel(){
    private val groupList = mutableStateListOf<String>()
    fun addGroup(name:String,context: Context){
        if(name !in groupList) {
            groupList.add(name)
            updateSavedList(context)
        }
    }

    fun removeGroup(name:String,context:Context){
        groupList.remove(name)
        updateSavedList(context)
        context.getSharedPreferences(name,Context.MODE_PRIVATE).edit{
            clear()
            commit()
        }
        CoroutineScope(Dispatchers.IO).launch {
            dinoRepository.deleteAllDinosInGroup(name)
            timerRepository.deleteTimersForGroup(name)
        }
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