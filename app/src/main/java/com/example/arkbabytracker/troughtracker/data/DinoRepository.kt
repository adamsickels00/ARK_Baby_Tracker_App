package com.example.arkbabytracker.troughtracker.data

import com.example.arkbabytracker.troughtracker.data.database.DinoDao
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import javax.inject.Inject
import javax.inject.Singleton

interface DinoRepository{
//    @Insert
//    fun add(d:DinoEntity)
    fun addDino(d:DinoEntity)
//    @Insert
//    fun addAll(d:List<DinoEntity>)
    fun addAllDinos(d:List<DinoEntity>)
//    @Query("DELETE FROM Dino WHERE Dino.id=:id")
//    fun delete(id:String)
    fun deleteDino(id:String)
//    @Query("DELETE FROM Dino WHERE 1=1")
//    fun deleteAll()
    fun deleteAllDinos()
//    @Query("DELETE FROM Dino WHERE Dino.groupName=:groupName")
//    fun deleteAllInGroup(groupName: String)
    fun deleteAllDinosInGroup(name:String)
//    @Query("SELECT * FROM Dino")
//    fun getAll():List<DinoEntity>
    fun getAllDinos():List<DinoEntity>
}

@Singleton
class DinoRepositoryRoom @Inject constructor(
    val dinoDao: DinoDao
):DinoRepository {
    override fun addDino(d: DinoEntity) {
        dinoDao.add(d)
    }

    override fun addAllDinos(d: List<DinoEntity>) {
        dinoDao.addAll(d)
    }

    override fun deleteDino(id: String) {
        dinoDao.delete(id)
    }

    override fun deleteAllDinos() {
        dinoDao.deleteAll()
    }

    override fun deleteAllDinosInGroup(name:String) {
        dinoDao.deleteAllInGroup(name)
    }

    override fun getAllDinos(): List<DinoEntity> {
        return dinoDao.getAll()
    }

}