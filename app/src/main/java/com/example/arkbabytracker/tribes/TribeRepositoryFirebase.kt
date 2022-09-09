package com.example.arkbabytracker.tribes

import com.example.arkbabytracker.di.USER_ID_INJECT_STRING
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TribeRepositoryFirebase @Inject constructor(@Named(USER_ID_INJECT_STRING) val userId: String):TribeRepository {
    val tribeDb = Firebase.database.reference.child("tribe")
    val userDb = Firebase.database.reference.child("users")
    val dinoDb = Firebase.database.reference.child("dinos")
    val dinoStatsDb = Firebase.database.reference.child("dinoStats")



    var uuid:String? = null

    override fun addTribeStateListener(callback: (Tribe?) -> Unit) {
        userDb.child(userId).addChildEventListener(object:ChildEventListener{


            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key == "tribeUUID"){
                    onDataChange(snapshot,callback)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key == "tribeUUID"){
                    onDataChange(snapshot,callback)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(snapshot.key == "tribeUUID"){
                    onDataChange(snapshot.child("tribeUUID"),callback)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.key == "tribeUUID"){
                    onDataChange(snapshot,callback)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }

        })
    }

    override fun addDinoToTribe(dinoId: String) {
        getTribeUUIDOnce { uuidOrNull->
            uuidOrNull?.let {
                tribeDb.child(it).child("dinos").child(dinoId).setValue(true)
            }
        }
    }

    override fun removeDino(dinoId: String) {
        doWithTribeUUID {
            tribeDb.child(it).child("dinos").child(dinoId).removeValue()
        }
    }

    override fun getTribeUUIDOnce(callback: (String?) -> Unit): Task<DataSnapshot> {
        return userDb.child(userId).child("tribeUUID").get().addOnCompleteListener {
            if(it.result.value == null){
                CoroutineScope(Dispatchers.IO).launch{callback(null)}
            } else{
                val id = it.result.value as String
                CoroutineScope(Dispatchers.IO).launch{callback(id)}
            }
        }
    }

    override fun getTribeOnce(callback: (Tribe?) -> Unit): Task<DataSnapshot> {
        return userDb.child(userId).child("tribeUUID").get().addOnCompleteListener {

            if(it.result.value == null){
                CoroutineScope(Dispatchers.IO).launch{callback(null)}
            } else{
                val id = it.result.value as String
                tribeDb.child(id).get().addOnCompleteListener {
                    val tribe = it.result.getValue(Tribe::class.java) as Tribe
                    tribe.uuid = it.result.key!!
                    CoroutineScope(Dispatchers.IO).launch{callback(tribe)}
                }

            }
        }
    }

    private fun doWithTribeUUID(block:(String)->Unit){
        getTribeUUIDOnce { uuidOrNull->
            uuidOrNull?.let(block)
        }
    }

    override fun addThisUserToTribe(userId:String, tribeKey: String) {
        userDb.child(userId).child("tribeUUID").setValue(tribeKey)
    }

    override fun removeUserFromTribe(userID: String) {
        userDb.child(userID).child("tribeUUID").setValue(null)
    }

    override fun addStatsDinoToTribe(dinoId: String) {
        doWithTribeUUID {
            tribeDb.child(it).child("dinoStats").child(dinoId).setValue(true)
        }
    }

    override suspend fun getAllDinos() : List<DinoEntity> {
        val dinoList = mutableListOf<DinoEntity>()
        doWithTribeUUID {
            val result = Tasks.await(tribeDb.child(it).child("dinos").get())
            result.children.forEach {
                val dinoId = it.key!!
                val result = Tasks.await(dinoDb.child(dinoId).get())
                val dino = result.getValue(DinoEntity::class.java) as DinoEntity
                dinoList.add(dino)
            }
        }
        return dinoList
    }

    override fun getAllDinoStats():List<DinoStats> {
        val dinoList = mutableListOf<DinoStats>()
        doWithTribeUUID {
            val result = Tasks.await(tribeDb.child(it).child("dinoStats").get())
            result.children.forEach {
                val dinoId = it.key!!
                val result = Tasks.await(dinoStatsDb.child(dinoId).get())
                val dino = result.getValue(DinoStats::class.java) as DinoStats
                dinoList.add(dino)
            }
        }
        return dinoList
    }

    override fun removeStatsDino(dinoId: String) {
        doWithTribeUUID {
            tribeDb.child(it).child("dinoStats").child(dinoId).removeValue()
        }
    }

    override fun createNewTribe(name:String): String {
        val newTribeLocation = tribeDb.push()
        newTribeLocation.child("name").setValue(name)
        return newTribeLocation.key!!
    }

    override fun removeTribe(tribeId: String) {
        tribeDb.child(tribeId).removeValue()
    }

    private fun onDataChange(snapshot: DataSnapshot,callback: (Tribe?) -> Unit) {
        val newUUID = snapshot.value
        if(newUUID == null){
            callback(null)
            uuid=null
        } else {
            if (newUUID != uuid) {
                uuid=newUUID as String
                tribeDb.child(newUUID as String).child("name").get().addOnCompleteListener {
                    val newTribe = Tribe()
                    newTribe.name = it.result.value as String
                    newTribe.uuid = newUUID
                    callback(newTribe)
                }

            }
        }
    }
}