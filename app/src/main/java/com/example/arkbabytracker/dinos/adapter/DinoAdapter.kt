package com.example.arkbabytracker.dinos.adapter

import com.example.arkbabytracker.dinos.data.Dino
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.databinding.DinoItemBinding

class DinoAdapter: ListAdapter<Dino,DinoAdapter.DinoViewHolder>(DinoDiff()) {
    class DinoViewHolder(private val binding: DinoItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(dino: Dino){
            binding.dinoName = dino.name
            binding.progress = dino.elapsedTimeSec / dino.getTotalSeconds()
            binding.progressString = (dino.elapsedTimeSec / dino.getTotalSeconds()).toString()
            binding.timeRemaining = (dino.getTotalSeconds() - dino.elapsedTimeSec).toString()
            binding.executePendingBindings()
        }
    }

    class DinoDiff():DiffUtil.ItemCallback<Dino>(){
        override fun areItemsTheSame(oldItem: Dino, newItem: Dino): Boolean {
            return oldItem.uniqueID == newItem.uniqueID
        }

        override fun areContentsTheSame(oldItem: Dino, newItem: Dino): Boolean {
            return oldItem.uniqueID == newItem.uniqueID
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoViewHolder {
        val binding = DinoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DinoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DinoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}