package com.example.arkbabytracker.troughtracker.food.trough

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.databinding.TroughItemBinding
import com.example.arkbabytracker.troughtracker.food.Food

class TroughAdapter(var trough:Trough) : RecyclerView.Adapter<TroughAdapter.TroughViewHolder>() {


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

    fun setData(newTrough : Trough){
        val oldTrough = trough
        trough = newTrough
        var index=0
        if(newTrough.size != oldTrough.size) {
            notifyDataSetChanged()
        } else {
            for (p in newTrough) {
                if (p.second != oldTrough.get(index).second) {
                    notifyItemChanged(index)
                }
                index++
            }
        }
    }
}