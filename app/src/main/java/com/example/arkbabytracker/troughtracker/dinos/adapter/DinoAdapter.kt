package com.example.arkbabytracker.troughtracker.dinos.adapter

import android.graphics.Color
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.troughtracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.DinoItemBinding
import com.example.arkbabytracker.utils.TimeDisplayUtil
import kotlin.math.roundToInt

class DinoAdapter(val data:DinoViewModel): ListAdapter<Dino,DinoAdapter.DinoViewHolder>(DinoDiff()) {

    override fun submitList(list: MutableList<Dino>?) {
        super.submitList(list?.let{ArrayList(it)})
    }

    class DinoViewHolder(val binding: DinoItemBinding,val data: DinoViewModel) : RecyclerView.ViewHolder(binding.root){
        fun bind(dino: Dino){
            binding.dinoName = dino.name
            binding.progress = (100*dino.elapsedTimeSec / dino.maturationTimeSec)
            binding.timeRemaining = TimeDisplayUtil.secondsToString((dino.maturationTimeSec-dino.elapsedTimeSec).roundToInt())
            binding.food = "%.2f/%.2f".format(dino.food,dino.maxFood)

            if(dino.food<=0){
                //Make the box red
                binding.dinoNameTextbox.setTextColor(Color.RED)
            } else{
                binding.dinoNameTextbox.setTextColor(Color.WHITE)
            }
            binding.deleteCreatureButton.setOnClickListener {
                val currentList = data.babyList.value!!
                currentList.remove(dino)
                data.babyList.value = currentList
            }
            binding.executePendingBindings()
        }
    }

    class DinoDiff:DiffUtil.ItemCallback<Dino>(){
        override fun areItemsTheSame(oldItem: Dino, newItem: Dino): Boolean {
            return oldItem.uniqueID == newItem.uniqueID
        }

        override fun areContentsTheSame(oldItem: Dino, newItem: Dino): Boolean {
            return false
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