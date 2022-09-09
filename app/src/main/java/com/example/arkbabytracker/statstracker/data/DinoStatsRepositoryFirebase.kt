package com.example.arkbabytracker.statstracker.data

import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class DinoStatsRepositoryFirebase @Inject constructor(): DinoStatsRepository {
    val dinoStatsPath = "dinoStats"
    val userDinoStatsPath = "users/${Firebase.auth.uid!!}/dinoStats"
    val dinoStatsDatabase = Firebase.database.reference.child(dinoStatsPath)
    val userDinoStatsDatabase = Firebase.database.reference.child(userDinoStatsPath)
    override fun insert(d: DinoStats) {
        d.id = dinoStatsDatabase.push().key!!
        dinoStatsDatabase.child(d.id).setValue(d)
        userDinoStatsDatabase.child(d.id).setValue(true)
    }

    override fun insertAll(d: List<DinoStats>) {
        for(dino in d){
            insert(dino)
        }
    }

    override fun delete(d: DinoStats) {
        dinoStatsDatabase.child(d.id.toString()).removeValue()
        userDinoStatsDatabase.child(d.id.toString()).removeValue()
    }

    override fun update(d: DinoStats) {
        //TODO security concern here, anyone can delete dinos from this list by guessing other ids
        dinoStatsDatabase.child(d.id.toString()).setValue(d)
    }

    override fun getDinoById(id: Int): DinoStats {
        val result = Tasks.await(dinoStatsDatabase.child(id.toString()).get())
        return result.getValue(DinoStats::class.java) as DinoStats
    }

    override fun getAllUserDinos(): List<DinoStats> {
        val result = Tasks.await(userDinoStatsDatabase.get())
        val dinoList = mutableListOf<DinoStats>()
        result.children.forEach{
            val dinoKey = it.key!!
            val innerTask = dinoStatsDatabase.child(dinoKey).get()
            Tasks.await(innerTask)
            dinoList.add(innerTask.result.getValue(DinoStats::class.java) as DinoStats)
        }
        return dinoList
    }

    override fun getAllDinos(): List<DinoStats> {
        val result = Tasks.await(userDinoStatsDatabase.get())
        val list = mutableListOf<DinoStats>()
        for(dinoStat in result.children){
            val dinoid = dinoStat.key!!
            val iResult = Tasks.await(dinoStatsDatabase.child(dinoid).get())
            val dino = iResult.getValue(DinoStats::class.java) as DinoStats
            list.add(dino)
        }
        return list
    }

    override fun getAllDinosFlow(): Flow<List<DinoStats>> {
        return callbackFlow {
            userDinoStatsDatabase.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newList = mutableListOf<DinoStats>()
                    snapshot.children.forEach{
                        val dinoid = it.key!!
                        val iResult = Tasks.await(dinoStatsDatabase.child(dinoid).get())
                        val dino = iResult.getValue(DinoStats::class.java) as DinoStats
                        newList.add(dino)
                    }
                    CoroutineScope(Dispatchers.IO).launch { send(newList) }
                }

                override fun onCancelled(error: DatabaseError) {
                    close()
                }

            })
        }
    }
}