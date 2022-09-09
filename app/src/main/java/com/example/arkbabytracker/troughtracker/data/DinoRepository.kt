package com.example.arkbabytracker.troughtracker.data

import com.example.arkbabytracker.tribes.TribeRepository
import com.example.arkbabytracker.troughtracker.data.database.DinoDao
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

//    @Query("DELETE FROM Dino WHERE Dino.groupName=:groupName")
//    fun deleteAllInGroup(groupName: String)
    fun deleteAllDinosInGroup(name:String)
//    @Query("SELECT * FROM Dino")
//    fun getAll():List<DinoEntity>
suspend fun getAllDinos():List<DinoEntity>
suspend fun getAllUserDinos():List<DinoEntity>
}

@Singleton
class DinoRepositoryRoom @Inject constructor(
    val dinoDao: DinoDao
):DinoRepository {
    override fun addDino(d: DinoEntity) {
        dinoDao.add(d)
    }

    override suspend fun getAllUserDinos(): List<DinoEntity> {
        TODO("Not yet implemented")
    }

    override fun addAllDinos(d: List<DinoEntity>) {
        dinoDao.addAll(d)
    }

    override fun deleteDino(id: String) {
        dinoDao.delete(id)
    }

    override fun deleteAllDinosInGroup(name:String) {
        dinoDao.deleteAllInGroup(name)
    }

    override suspend fun getAllDinos(): List<DinoEntity> {
        return dinoDao.getAll()
    }

}

@Singleton
class DinoRepositoryFirebase @Inject constructor(val tribeRepository: TribeRepository):DinoRepository {
    val database = Firebase.database.reference
    val dinoPath = "dinos"
    val userPath = "users/${Firebase.auth.uid}"
    var allDinosList = listOf<DinoEntity>()


    override suspend fun getAllUserDinos(): List<DinoEntity> {
        val result = Tasks.await(database.child(userPath).child(dinoPath).get())
        val dinoList = mutableListOf<DinoEntity>()
        result.children.forEach{
            val dinoKey = it.key!!
            val innerTask = database.child(dinoPath).child(dinoKey).get()
            Tasks.await(innerTask)
            dinoList.add(innerTask.result.getValue(DinoEntity::class.java) as DinoEntity)
        }
        return dinoList
    }

    override fun addDino(d: DinoEntity) {
        database.child(dinoPath).child(d.id).setValue(d)
        database.child(userPath).child("dinos").child(d.id).setValue(true)
    }

    override fun addAllDinos(d: List<DinoEntity>) {
        d.forEach {
            addDino(it)
        }
    }

    override fun deleteDino(id: String) {
        database.child(dinoPath).child(id).removeValue()
        database.child(userPath).child(dinoPath).child(id).removeValue()
        tribeRepository.removeDino(id)
    }

    override fun deleteAllDinosInGroup(name:String) {
        database.child(dinoPath).get().addOnCompleteListener {
            it.result.children.forEach{
                if(it.child("groupName").value == name){
                    it.key?.let { it1 -> deleteDino(it1) }
                }
            }
        }
    }

    override suspend fun getAllDinos(): List<DinoEntity> {
        //Get dinos from user only
        val dinoList:MutableList<DinoEntity> = mutableListOf()
        val task = database.child(userPath).child("dinos").get()
        val result = Tasks.await(task)
        result.children.forEach{
            val dinoKey = it.key!!
            val innerTask = database.child(dinoPath).child(dinoKey).get()
            Tasks.await(innerTask)
            dinoList.add(innerTask.result.getValue(DinoEntity::class.java) as DinoEntity)
        }
        return dinoList
    }

}