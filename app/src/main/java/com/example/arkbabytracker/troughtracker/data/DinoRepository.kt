package com.example.arkbabytracker.troughtracker.data

import android.util.Log
import com.example.arkbabytracker.troughtracker.data.database.DinoDao
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import java.time.Instant
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

@Singleton
class DinoRepositoryFirebase @Inject constructor():DinoRepository {
    val database = Firebase.database.reference
    val dinoPath = "dinos"
    var allDinosList = listOf<DinoEntity>()



    override fun addDino(d: DinoEntity) {
        val loc = database.child(dinoPath).child(d.id).setValue(d)
    }

    override fun addAllDinos(d: List<DinoEntity>) {
        d.forEach {
            addDino(it)
        }
    }

    override fun deleteDino(id: String) {
        database.child(dinoPath).child(id).removeValue()
    }

    override fun deleteAllDinos() {
        database.child(dinoPath).removeValue()
    }

    override fun deleteAllDinosInGroup(name:String) {
        database.child(dinoPath).get().addOnCompleteListener {
            it.result.children.forEach{
                if(it.child("groupName").value == name){
                    it.ref.removeValue()
                }
            }
        }
    }

    override fun getAllDinos(): List<DinoEntity> {
        val task = database.child(dinoPath).get()
        Tasks.await(task)
        val res = mutableListOf<DinoEntity>()
        task.result.children.forEach {
            it.getValue(DinoEntity::class.java)?.let { it1 -> res.add(it1) }
        }
        return res
    }

}