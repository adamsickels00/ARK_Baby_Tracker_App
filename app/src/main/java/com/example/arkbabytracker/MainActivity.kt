package com.example.arkbabytracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.arkbabytracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.ActivityMainBinding
import com.example.arkbabytracker.dinos.adapter.DinoAdapter
import com.example.arkbabytracker.food.adapter.FoodAdapter


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val data: DinoViewModel by viewModels()
        data.populateLists()
        binding.dinoAdapter = DinoAdapter()
        binding.dinoAdapter!!.submitList(data.babyList)
        binding.foodAdapter = FoodAdapter(data.foodStacks)
        binding.executePendingBindings()
    }
}