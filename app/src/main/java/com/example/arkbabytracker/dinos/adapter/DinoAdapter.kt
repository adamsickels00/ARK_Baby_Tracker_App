package com.example.arkbabytracker.dinos.adapter

import com.example.arkbabytracker.dinos.data.Dino
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.DinoItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DinoAdapter(val data:DinoViewModel): ListAdapter<Dino,DinoAdapter.DinoViewHolder>(DinoDiff()) {
    class DinoViewHolder(private val binding: DinoItemBinding,val data: DinoViewModel) : RecyclerView.ViewHolder(binding.root){
        fun bind(dino: Dino){
            binding.dinoName = dino.name
            binding.progressInt = (100*dino.elapsedTimeSec / dino.maturationTimeSec).toInt()
            binding.timeRemaining = (dino.maturationTimeSec - dino.elapsedTimeSec).toString()
            binding.percentCompleteEditText.doAfterTextChanged {
                if(it.toString()!="") {
                    dino.setPercentMature(it.toString().toDouble())
                    data.babyList.value = data.babyList.value
                }
            }
            binding.executePendingBindings()
        }
    }

    class DinoDiff:DiffUtil.ItemCallback<Dino>(){
        override fun areItemsTheSame(oldItem: Dino, newItem: Dino): Boolean {
            return oldItem.uniqueID == newItem.uniqueID
        }

        override fun areContentsTheSame(oldItem: Dino, newItem: Dino): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoViewHolder {
        val binding = DinoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DinoViewHolder(binding,data)
    }

    override fun onBindViewHolder(holder: DinoViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
}