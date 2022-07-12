package com.example.arkbabytracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.room.Room
import com.example.arkbabytracker.data.DinoViewModel
import com.example.arkbabytracker.data.Environment
import com.example.arkbabytracker.data.database.DinoDatabase
import com.example.arkbabytracker.databinding.ActivityMainBinding
import com.example.arkbabytracker.databinding.DinoPopupBinding
import com.example.arkbabytracker.dinos.adapter.DinoAdapter
import com.example.arkbabytracker.dinos.data.*
import com.example.arkbabytracker.food.Food
import com.example.arkbabytracker.food.fragment.FoodItemFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor
// TODO add a menu to different apps (eg. colors tracker)

const val EVENT_MULT_KEY = "com.example.arkbabycalculator.eventMultiplier"
const val MAE_MULT_KEY = "com.example.arkbabycalculator.maewingMultiplier"

class MainActivity : AppCompatActivity() {
    var currentId = 0
    private lateinit var binding : ActivityMainBinding
    private lateinit var dinoAdapter: DinoAdapter
    private val data by viewModels<DinoViewModel>()
    private lateinit var db:DinoDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = getPreferences(Context.MODE_PRIVATE)
        val maeMult = pref.getFloat(MAE_MULT_KEY,1f)
        val evMult = pref.getFloat(EVENT_MULT_KEY,1f)

        db = Room.databaseBuilder(
            applicationContext,
            DinoDatabase::class.java, "dino-database"
        ).build()
        Environment.maewingFoodMultiplier.value = maeMult.toDouble()
        Environment.eventMultiplier.value = evMult.toDouble()
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        dinoAdapter = DinoAdapter(data)
        binding.dinoAdapter = dinoAdapter
        (binding.dinoHolder.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        dinoAdapter.submitList(data.babyList.value!!)
        binding.addDinoButton.setOnClickListener{
            openPopupWindow()
        }
        fillFoods(data.foodStacks.value!!)

        binding.executePendingBindings()
        data.foodStacks.observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                val time = data.runSim()
                binding.bigTimerTextView.text = time.toString()
            }
        }
        data.babyList.observe(this) {
            val runSim = dinoAdapter.currentList.size != it.size
            dinoAdapter.submitList(it)
            binding.executePendingBindings()

            if(runSim)
            CoroutineScope(Dispatchers.Main).launch {
                val time = data.runSim()
                binding.bigTimerTextView.text = time.toString()
            }
        }

        Environment.eventMultiplier.observe(this){ newVal ->
            val tempList: MutableList<Dino> = data.babyList.value!!
            with(pref.edit()){
                putFloat(EVENT_MULT_KEY,newVal.toFloat())
                apply()
            }
            tempList.forEach{
                it.setEventMultiplier(newVal)
            }
            data.babyList.value = tempList
        }

        Environment.maewingFoodMultiplier.observe(this){
            with(pref.edit()){
                putFloat(MAE_MULT_KEY,it.toFloat())
                apply()
            }
            CoroutineScope(Dispatchers.IO).launch {
                data.getFromDatabase(db)
                val time = data.runSim()
                binding.bigTimerTextView.text = time.toString()
            }

        }

        CoroutineScope(Dispatchers.Main).launch {
            val time = data.runSim()
            binding.bigTimerTextView.text = time.toString()
        }
        fixedRateTimer("Dino time left",true, period = 1000){
            updateDinoTimer()
        }
    }

    private fun openPopupWindow(){
        val popupBinding = DinoPopupBinding.inflate(layoutInflater,null,false)
        popupBinding.spinnerList = allDinoList.map {
            it.simpleName
        }
        val popup = PopupWindow(popupBinding.root,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true)
        popup.elevation = 20f
        popupBinding.submitDinoButton.setOnClickListener{
            val newDinoString = popupBinding.dinoTypeSelect.selectedItem
            for (c in allDinoList){
                if(c.simpleName == newDinoString){
                    val newList = data.babyList.value!!
                    val newDino = c.primaryConstructor!!.call(
                        popupBinding.maxFoodTextBox.text.toString().toDouble()
                    )
                    newDino.setPercentMature(popupBinding.percentMatureTextBox.text.toString().toDouble())
                    newList.add(newDino)
                    data.babyList.value = newList
                    popup.dismiss()
                }
            }
        }

        popup.showAtLocation(binding.root,Gravity.CENTER,0,0)
    }

    private fun updateDinoTimer(){
        var dinoList = data.babyList.value!!
        dinoList.forEach{
            it.elapsedTimeSec = (Instant.now().epochSecond-it.startTime.epochSecond).toDouble()
        }
        dinoList = dinoList.filter { it.elapsedTimeSec < it.maturationTimeSec }.toMutableList()
        data.babyList.postValue(dinoList)
    }

    private fun fillFoods(foodMap:Map<Food,Int>){
        for(pair in foodMap) {
            var ll = LinearLayout(this.baseContext)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.id = ++currentId

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(ll.id, FoodItemFragment.newInstance(pair.key, pair.value))
            }
            binding.foodListHolder.addView(ll)
        }
        binding.executePendingBindings()
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifecycleTests","Paused")
    }
    override fun onStop() {
        super.onStop()
        Log.d("LifecycleTests","Stop")
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            data.saveToDatabase(db)
        }

        Log.d("LifecycleTests","Destroy")
    }
}