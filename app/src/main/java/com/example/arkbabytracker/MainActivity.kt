package com.example.arkbabytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.example.arkbabytracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.ActivityMainBinding
import com.example.arkbabytracker.dinos.adapter.DinoAdapter
import com.example.arkbabytracker.food.Food
import com.example.arkbabytracker.food.fragment.FoodItemFragment


class MainActivity : AppCompatActivity() {
    var currentId = 0

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val data: DinoViewModel by viewModels()
        data.populateLists()
        binding.dinoAdapter = DinoAdapter()
        binding.dinoAdapter!!.submitList(data.babyList)
        fillFoods(data.foodStacks)
        binding.executePendingBindings()
    }

    fun fillFoods(foodMap:Map<Food,Int>){
        for(pair in foodMap) {
            var ll = LinearLayout(this.baseContext)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.id = ++currentId

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(ll.id, FoodItemFragment.newInstance(pair.key.name, pair.value))
            }
            binding.foodListHolder.addView(ll)
        }
        binding.executePendingBindings()
    }
}