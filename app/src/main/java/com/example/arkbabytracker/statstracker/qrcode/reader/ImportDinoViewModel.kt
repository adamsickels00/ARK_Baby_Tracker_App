package com.example.arkbabytracker.statstracker.qrcode.reader

import androidx.lifecycle.ViewModel
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.statstracker.data.DinoStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportDinoViewModel @Inject constructor(val dinoStatsRepository: DinoStatsRepository): ViewModel() {
    fun acceptImport(dinoList:List<DinoStats>){
        CoroutineScope(Dispatchers.IO).launch {
            dinoStatsRepository.insertAll(dinoList)
        }
    }
}