package com.example.arkbabytracker.statstracker.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DinoStatsRepositoryRoom @Inject constructor(val dinoStatsDao: DinoStatsDao):DinoStatsRepository {
    override fun insert(d: DinoStats): Long {
        return dinoStatsDao.insert(d)
    }

    override fun insertAll(d: List<DinoStats>) {
        dinoStatsDao.insertAll(d)
    }

    override fun delete(d: DinoStats) {
        dinoStatsDao.delete(d)
    }

    override fun update(d: DinoStats) {
        dinoStatsDao.update(d)
    }

    override fun getDinoById(id: Int): DinoStats {
        return dinoStatsDao.getDinoById(id)
    }

    override fun getAllDinos(): List<DinoStats> {
        return dinoStatsDao.getAllDinos()
    }

    override fun getAllDinosFlow(): Flow<List<DinoStats>> {
        return dinoStatsDao.getAllDinosFlow()
    }

}