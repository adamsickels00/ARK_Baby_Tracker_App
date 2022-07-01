package com.example.arkbabytracker.food.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.R
import com.example.arkbabytracker.databinding.FoodItemBinding
import com.example.arkbabytracker.food.Food

class FoodAdapter(var foodMap:Map<Food,Int>): RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    var keys = foodMap.keys.toList()
    class FoodViewHolder(var binding:FoodItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(pair:Pair<Food,Int?>){
            binding.foodNameXml = pair.first.name
            binding.numFood = pair.second.toString()
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FoodItemBinding.inflate(inflater,parent,false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val pair = Pair(keys[position],foodMap[keys[position]])
        holder.bind(pair)
    }

    override fun getItemCount(): Int {
        return foodMap.count()
    }
}