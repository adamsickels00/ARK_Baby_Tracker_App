package com.example.arkbabytracker.tribes

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.arkbabytracker.di.USER_ID_INJECT_STRING
import com.example.arkbabytracker.troughtracker.data.DinoRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TribeRepositoryFirebase @Inject constructor(@Named(USER_ID_INJECT_STRING) val userId: String,val dinoRepository: DinoRepository):TribeRepository {
    val tribeDb = Firebase.database.reference.child("tribe")
    val userDb = Firebase.database.reference.child("users")



    var uuid:String? = null

    override fun addTribeStateListener(callback: (Tribe?) -> Unit) {
        userDb.child(userId).child("tribeUUID").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newUUID = snapshot.value
                if(newUUID == null){
                    callback(null)
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
                callback(null)
            } else{
                val id = it.result.value as String
                callback(id)
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
        userDb.child(userID).child("tribeUUID").removeValue()
        //TODO Remove that users Dinos from tribe and remove tribe owner from dino
        // Maybe add dino to users for faster lookup?
//        userDb.child(userID).child("dinos")

    }

    override fun createNewTribe(name:String, firstMemberId:String) {
        val newTribeLocation = tribeDb.push()
        newTribeLocation.child("name").setValue(name)
        userDb.child(firstMemberId).child("tribeUUID").setValue(newTribeLocation.key)
    }

    override fun removeTribe(tribeId: String) {
        tribeDb.child(tribeId).removeValue()
    }

}