package com.example.arkbabytracker.statstracker.qrcode.reader

import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.statstracker.data.DinoStatsDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportDinoViewModel @Inject constructor(val dinoStatsDao: DinoStatsDao): ViewModel() {
    fun acceptImport(dinoList:List<DinoStats>){
        dinoList.forEach{it.id = null} // No overlapping keys
        CoroutineScope(Dispatchers.IO).launch {
            dinoStatsDao.insertAll(dinoList)
        }
    }
}