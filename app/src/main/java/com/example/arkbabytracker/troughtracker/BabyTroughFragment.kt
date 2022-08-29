package com.example.arkbabytracker.troughtracker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.room.Room
import com.example.arkbabytracker.ActivityViewModel
import com.example.arkbabytracker.DinoApplication
import com.example.arkbabytracker.databinding.DinoPopupBinding
import com.example.arkbabytracker.databinding.FragmentBabyTroughBinding
import com.example.arkbabytracker.timers.NotificationScheduler
import com.example.arkbabytracker.timers.Timer
import com.example.arkbabytracker.troughtracker.data.DinoViewModel
import com.example.arkbabytracker.troughtracker.data.EnvironmentViewModel
import com.example.arkbabytracker.troughtracker.data.database.DinoDatabase
import com.example.arkbabytracker.troughtracker.data.database.DinoEntity
import com.example.arkbabytracker.troughtracker.dinos.adapter.DinoAdapter
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import com.example.arkbabytracker.troughtracker.dinos.data.allDinoList
import com.example.arkbabytracker.troughtracker.food.Food
import com.example.arkbabytracker.troughtracker.food.fragment.FoodItemFragment
import com.example.arkbabytracker.utils.TimeDisplayUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.reflect.full.primaryConstructor

const val EVENT_MULT_KEY = "com.example.arkbabycalculator.eventMultiplier"
const val MAE_MULT_KEY = "com.example.arkbabycalculator.maewingMultiplier"
const val HUNGER_MULT_KEY = "com.example.arkbabycalculator.hungerMultiplier"
const val LAG_MULT_KEY = "com.example.arkbabycalculator.lagMultiplier"

const val TIMER_THRESHOLD = 0.9

/**
 * A simple [Fragment] subclass.
 * Use the [BabyTroughFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BabyTroughFragment : Fragment() {

    private var _binding: FragmentBabyTroughBinding? = null
    private val binding get() = _binding!!
    private lateinit var dinoAdapter: DinoAdapter
    private val data by viewModels<DinoViewModel>()
    private val activityVm by activityViewModels<ActivityViewModel>()
    private val env by viewModels<EnvironmentViewModel>()
    private lateinit var db: DinoDatabase
    private var needFoodFragment = true
    private var foodCache = mutableSetOf<Food>()
    private var thread: Job? = null

    lateinit var group:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { group = it.getString("Group").toString() }
        needFoodFragment = savedInstanceState == null
        foodCache.clear()
        NotificationScheduler.createNotificationChannel(requireContext())
        Log.d("LifecycleTests","Create")
        activityVm.troughMap.putIfAbsent(group, MutableLiveData())

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("LifecycleTests","Attach")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("LifecycleTests","Detach")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LifecycleTests","CreateView")
        // Inflate the layout for this fragment
        _binding = FragmentBabyTroughBinding.inflate(inflater,container,false)

        val activity = requireActivity()
        val pref = activity.getSharedPreferences(group,Context.MODE_PRIVATE)
        for(food in Food.values()){
            data.foodStacks.value!![food] = pref.getFloat(food.name,0f).toDouble()
        }

        initEnvVariables(pref)

        binding.bigTimerTextView.setOnClickListener {
            Log.d("Touch","Timer touched")
            val timerLengthInSeconds = (data.remainingTime.value!!*TIMER_THRESHOLD).toInt()
            val description = "$group: ${data.babyListAsString()}"
            val timer = Timer(Instant.now().epochSecond,timerLengthInSeconds,description,group)
            data.insertTimer(timer)
                .subscribe({
                    NotificationScheduler.scheduleNotification(requireContext(),timerLengthInSeconds.toLong(),it.toInt())
            },{})


        }

        db = Room.databaseBuilder(
            activity,
            DinoDatabase::class.java, "dino-database"
        ).build()
        CoroutineScope(Dispatchers.IO).launch { data.getFromDatabase(env,group) }

        dinoAdapter = DinoAdapter(data, context = requireContext())
        binding.dinoAdapter = dinoAdapter
        (binding.dinoHolder.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        dinoAdapter.submitList(data.babyList.value!!)
        binding.addDinoButton.setOnClickListener{
            openPopupWindow()
        }
        if(needFoodFragment)
            updateFoods()

        binding.executePendingBindings()
        data.foodStacks.observe(viewLifecycleOwner) {
            for(food in Food.values()){
                with(pref.edit()){
                    putFloat(food.name,(it[food]?:0f).toFloat())
                    apply()
                }
            }
            val newMap = mutableMapOf<Food,Double>()
            it.forEach{ oldMap -> newMap[oldMap.key] = oldMap.value}
            data.troughRefill[Instant.now().epochSecond] = newMap
            updateTime()
        }
        data.babyList.observe(viewLifecycleOwner) {
            dinoAdapter.submitList(it)
            updateFoods()
            updateTime()
        }

        data.simTrough.observe(viewLifecycleOwner){
            activityVm.troughMap[group]?.value = it
        }



        data.remainingTime.observe(viewLifecycleOwner){
            binding.bigTimerTextView.text = TimeDisplayUtil.secondsToString(it)
        }

        data.currentSimBabyList.observe(viewLifecycleOwner){ simResults ->

            // Get the list we display on the recyclerview
            val dinoList = data.babyList.value!!

            //Set the food of each one to the sim value and the elapsed time to current elapsed time
            dinoList.forEach{

                simResults.forEach { simDino ->
                    if(simDino.uniqueID == it.uniqueID){
                        it.food = simDino.food
                        it.elapsedTimeSec = simDino.elapsedTimeSec
                    }
                }
            }
            //Post the list containing the dinos that are not yet finished
            dinoAdapter.submitList(dinoList.filter { (it.elapsedTimeSec < it.maturationTimeSec) }.toMutableList())
            CoroutineScope(Dispatchers.IO).launch {
                dinoList.filter { it.elapsedTimeSec >= it.maturationTimeSec }.forEach { db.dinoDao().delete(it.uniqueID) }
            }

        }

        data.launchUpdateThread()
        return binding.root
    }

    private fun openPopupWindow(){

        val popupBinding = DinoPopupBinding.inflate(layoutInflater,null,false)

        popupBinding.adapter = ArrayAdapter<String>(requireContext(),android.R.layout.select_dialog_item,allDinoList.map {it.simpleName})
        popupBinding.autoCompleteTextView.threshold = 0

        val popup = PopupWindow(popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,true)
        popup.elevation = 20f
        popupBinding.submitDinoButton.setOnClickListener{
            val maxFood = popupBinding.maxFoodTextBox.text.toString().toDoubleOrNull()
            val newDinoString = popupBinding.autoCompleteTextView.text.toString()
            for (c in allDinoList){
                if(c.simpleName == newDinoString){
                    val newList = data.babyList.value!!
                    val newDino = c.primaryConstructor!!.call(
                        maxFood?:1000.0,
                        env
                    )
                    newDino.percentComplete = (popupBinding.percentMatureTextBox.text.toString().toDoubleOrNull()?:0.0)/100
                    newDino.groupName = group
                    newList.add(newDino)
                    CoroutineScope(Dispatchers.IO).launch {
                        db.dinoDao().add(DinoEntity.fromDino(newDino))
                    }
                    data.babyList.value = newList
                    popup.dismiss()
                    return@setOnClickListener
                }
            }
            Toast.makeText(context,"Invalid Dino Type, Try Again",Toast.LENGTH_SHORT).show()
        }

        popup.showAtLocation(binding.root, Gravity.CENTER,0,0)
    }

    private fun updateFoods(){
        if(isAdded) {
            var foodSet = mutableSetOf<Food>()
            for (food in Food.values()) {
                data.babyList.value!!.forEach {
                    if(it.diet.eatOrder.contains(food)) {
                        addIfDoesNotExist(food)
                        foodSet.add(food)
                    }
                }
            }
            for(food in Food.values()){
                if(food !in foodSet){
                    removeIfExists(food)
                }
            }
        }
            needFoodFragment = false
            binding.executePendingBindings()
    }

    private fun addIfDoesNotExist(food:Food){
        if(childFragmentManager.findFragmentByTag(food.name) == null && food !in foodCache) {
            childFragmentManager.commit {
                setReorderingAllowed(true)
                val frag = FoodItemFragment.newInstance(
                    food,
                    data.foodStacks.value!![food] ?: 0.0
                )
                add(binding.foodListHolder.id, frag, food.name)
                foodCache.add(food)
            }
        }
    }

    private fun removeIfExists(food:Food){
        childFragmentManager.findFragmentByTag(food.name)?.let{
            childFragmentManager.commit { remove(it)}
            foodCache.remove(food)
        }
    }

    fun initEnvVariables(pref:SharedPreferences){
        val maeMult = pref.getFloat(MAE_MULT_KEY,1f)
        val evMult = pref.getFloat(EVENT_MULT_KEY,1f)
        env.maewingFoodMultiplier.value = maeMult.toDouble()
        env.eventMultiplier.value = evMult.toDouble()
        env.hungerMultiplier.value = pref.getFloat(HUNGER_MULT_KEY,1f).toDouble()
        env.lagCorrection.value = pref.getFloat(LAG_MULT_KEY,1.04f).toDouble()
        env.eventMultiplier.observe(viewLifecycleOwner){ newVal ->
            val tempList: MutableList<Dino> = data.babyList.value!!
            pref.edit{
                putFloat(EVENT_MULT_KEY,newVal.toFloat())
                commit()
            }
            data.babyList.value = tempList
        }

        env.maewingFoodMultiplier.observe(viewLifecycleOwner){
            with(pref.edit()){
                putFloat(MAE_MULT_KEY,it.toFloat())
                apply()
            }

        }

        env.hungerMultiplier.observe(viewLifecycleOwner){
            with(pref.edit()){
                putFloat(HUNGER_MULT_KEY,it.toFloat())
                apply()
            }
        }

        env.lagCorrection.observe(viewLifecycleOwner){
            with(pref.edit()){
                putFloat(LAG_MULT_KEY,it.toFloat())
                apply()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifecycleTests","Paused")
    }
    override fun onStop() {
        super.onStop()

        Log.d("LifecycleTests","Stop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("LifecycleTests","On Save Instance State")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifecycleTests","Destroy")
    }

    private fun updateTime(){
        if(thread == null || !thread!!.isActive){
            thread = CoroutineScope(Dispatchers.Default).launch {
                val fullTime = data.runSim()
                data.timerEndTime = Instant.now().epochSecond + fullTime
                data.remainingTime.postValue(fullTime)
            }
        }

    }


    companion object {
        @JvmStatic
        fun newInstance(group:String) =
            BabyTroughFragment().apply {
                arguments = Bundle().apply {
                    putString("Group", group)
                }
            }
        const val CHANNEL_ID = "DinoChannel"

    }
}