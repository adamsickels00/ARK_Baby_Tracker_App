package com.example.arkbabytracker.food.trough

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.TroughItemBinding
import com.example.arkbabytracker.food.Food

class TroughAdapter(val trough:Trough) : RecyclerView.Adapter<TroughAdapter.TroughViewHolder>() {
    class TroughViewHolder(val binding:TroughItemBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(item:Pair<Food,Int>){
            binding.foodName = item.first.name
            binding.foodQuantity = item.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TroughViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TroughItemBinding.inflate(inflater,parent,false)
        return TroughViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TroughViewHolder, position: Int) {
        holder.bind(trough.get(position))
    }

    override fun getItemCount(): Int {
        return trough.size
    }
}