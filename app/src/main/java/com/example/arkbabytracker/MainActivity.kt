package com.example.arkbabytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.arkbabytracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.ActivityMainBinding
import com.example.arkbabytracker.databinding.DinoPopupBinding
import com.example.arkbabytracker.dinos.adapter.DinoAdapter
import com.example.arkbabytracker.dinos.data.*
import com.example.arkbabytracker.food.Food
import com.example.arkbabytracker.food.fragment.FoodItemFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.full.createInstance


class MainActivity : AppCompatActivity() {
    var currentId = 0
    private lateinit var binding : ActivityMainBinding
    private lateinit var data:DinoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        data= ViewModelProvider(this)[DinoViewModel::class.java]
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        data.populateLists()
        binding.dinoAdapter = DinoAdapter(data)
        binding.dinoAdapter!!.submitList(data.babyList.value!!)
        binding.addDinoButton.setOnClickListener{
            openPopupWindow()
        }
        fillFoods(data.foodStacks.value!!)

        binding.updateTimerButton.setOnClickListener{
            data.babyList.value = data.babyList.value
            CoroutineScope(Dispatchers.Main).launch {
                val time = data.runSim()
                binding.bigTimerTextView.text = time.toString()
            }
        }

        binding.executePendingBindings()
        data.foodStacks.observe(this) {
            CoroutineScope(Dispatchers.Main).launch {
                val time = data.runSim()
                binding.bigTimerTextView.text = time.toString()
            }
        }
        data.babyList.observe(this) {
            binding.dinoAdapter = DinoAdapter(data)
            binding.dinoAdapter!!.submitList(data.babyList.value!!)
            binding.executePendingBindings()
            CoroutineScope(Dispatchers.Main).launch {
                val time = data.runSim()
                binding.bigTimerTextView.text = time.toString()
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val time = data.runSim()
            binding.bigTimerTextView.text = time.toString()
        }
    }

    private fun openPopupWindow(){
        val popupBinding = DinoPopupBinding.inflate(layoutInflater,null,false)
        popupBinding.spinnerList = allDinoList.map {
            it.simpleName
        }
        popupBinding.submitDinoButton.setOnClickListener{
            val newDinoString = popupBinding.dinoTypeSelect.selectedItem
            for (c in allDinoList){
                if(c.simpleName == newDinoString){
                    val newList = data.babyList.value!!
                    newList.add(c.createInstance())
                    data.babyList.value = newList
                }
            }
        }

        val popup = PopupWindow(popupBinding.root,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,true)
        popup.showAtLocation(binding.root,Gravity.CENTER,0,0)
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
}