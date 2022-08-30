package com.example.arkbabytracker.tribes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.arkbabytracker.di.USER_ID_INJECT_STRING
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TribeRepositoryFirebase @Inject constructor(@Named(USER_ID_INJECT_STRING) userId: String):TribeRepository {
    val tribeDb = Firebase.database.reference.child("tribe")
    val userDb = Firebase.database.reference.child("users")

    var userTribeNameCache by mutableStateOf<String?>(null)

    init{
        userDb.child(userId).child("tribeUUID").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == null){
                    //Create user
                    userDb.child(userId).setValue(User().apply { name="Survivor"; tribeUUID = null })
                    userTribeNameCache = null
                } else{
                    val tribeUUID = try{snapshot.value as String} catch (e:Exception){null}

                    tribeUUID?.let {
                        tribeDb.child(it).child("name")
                            .get().addOnCompleteListener { userTribeNameCache = it.result.value as String }
                    }
                    if(tribeUUID == null){
                        userTribeNameCache = null
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }


    override fun getUserTribe(): String? {
        return userTribeNameCache
    }

    override fun addThisUserToTribe(userId:String, tribeKey: String) {
        userDb.child(userId).child("tribeUUID").setValue(tribeKey)
    }

    override fun removeUserFromTribe(userID: String, tribeKey: String) {
        userDb.child(userID).child("tribeUUID").setValue(tribeKey)
    }

    override fun createNewTribe(name:String, firstMemberId:String) {
        val newTribe = Tribe()
        newTribe.name = name
        val newTribeLocation = tribeDb.push()
        newTribeLocation.setValue(newTribe)
        userDb.child(firstMemberId).child("tribeUUID").setValue(newTribeLocation.key)
    }

    override fun removeTribe(tribeId: String) {
        tribeDb.child(tribeId).removeValue()
    }

}