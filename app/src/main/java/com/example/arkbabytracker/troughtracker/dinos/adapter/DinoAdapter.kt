package com.example.arkbabytracker.troughtracker.dinos.adapter

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.R
import com.example.arkbabytracker.databinding.DinoItemBinding
import com.example.arkbabytracker.troughtracker.data.DinoViewModel
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import com.example.arkbabytracker.utils.TimeDisplayUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class DinoAdapter(val data:DinoViewModel,val context: Context): ListAdapter<Dino,DinoAdapter.DinoViewHolder>(DinoDiff()) {

    override fun submitList(list: MutableList<Dino>?) {
        super.submitList(list?.let{ArrayList(it)})
    }

    class DinoViewHolder(val binding: DinoItemBinding,val data: DinoViewModel, val context: Context) : RecyclerView.ViewHolder(binding.root){
        fun bind(dino: Dino){
            binding.dinoName = dino.name
            binding.progress = (100*dino.elapsedTimeSec / dino.maturationTimeSec)
            binding.timeRemaining = TimeDisplayUtil.secondsToString((dino.maturationTimeSec-dino.elapsedTimeSec).roundToInt())
            binding.group = dino.groupName
            binding.food = "%.2f".format(if(dino.food<0) 0f else dino.food)
            binding.maxFood = "%.2f".format(dino.finalMaxFood)

            val typedValue = TypedValue()
            val theme = context.theme
            theme.resolveAttribute(R.attr.customTextColor, typedValue, true)
            @ColorInt val color = typedValue.data

            if(dino.food<=0){
                //Make the box red
                binding.dinoNameTextbox.setTextColor(Color.RED)
            } else{
                binding.dinoNameTextbox.setTextColor(color)
            }


            when (dino.hasEnoughFood) {
                true -> {
                    binding.dinoNameTextbox.setTextColor(Color.GREEN)
                }
                false -> {
                    binding.dinoNameTextbox.setTextColor(Color.YELLOW)
                }
                else -> {
                    binding.dinoNameTextbox.setTextColor(color)
                }
            }

            binding.deleteCreatureButton.setOnClickListener {
                val currentList = data.babyList.value!!
                currentList.remove(dino)
                CoroutineScope(Dispatchers.IO).launch { data.deleteDino(dino) }
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
        return DinoViewHolder(binding,data, context = context)
    }

    override fun onBindViewHolder(holder: DinoViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
}