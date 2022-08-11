package com.example.arkbabytracker.statstracker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.databinding.DinoStatsItemBinding
import com.example.arkbabytracker.statstracker.DinoStatsFragmentDirections
import com.example.arkbabytracker.statstracker.data.DinoStats

class DinoStatsAdapter(private val dinoList:List<DinoStats>) : RecyclerView.Adapter<DinoStatsAdapter.DinoStatsViewHolder>() {
    class DinoStatsViewHolder(val binding: DinoStatsItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:DinoStats){
            binding.dinoStats = item
            binding.root.setOnClickListener{
                val action = DinoStatsFragmentDirections.actionDinoStatsFragmentToEditDinoStatsFragment(item.id!!)
                binding.root.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoStatsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DinoStatsItemBinding.inflate(inflater,parent,false)
        return DinoStatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DinoStatsViewHolder, position: Int) {
        holder.bind(dinoList[position])
    }

    override fun getItemCount(): Int {
        return dinoList.size
    }
}