package com.example.arkbabytracker.statstracker.adapter

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.arkbabytracker.databinding.DinoGroupItemBinding
import com.example.arkbabytracker.statstracker.data.DinoMenuViewModel
import com.example.arkbabytracker.statstracker.data.DinoStats

class DinoGroupStatsAdapter(var groupMap:Map<String,List<DinoStats>>,val viewModel: DinoMenuViewModel) : RecyclerView.Adapter<DinoGroupStatsAdapter.DinoGroupStatsViewHolder>() {

    var mExpandedPosition = -1
    class DinoGroupStatsViewHolder(val binding:DinoGroupItemBinding,val viewModel: DinoMenuViewModel):RecyclerView.ViewHolder(binding.root){
        var recyclerList = listOf<DinoStats>()
        fun bind(item : Pair<String,List<DinoStats>>){
            recyclerList = item.second
            binding.type = item.first
            binding.adapter = DinoStatsAdapter(item.second)
            addItemSwipe()
        }

        fun addItemSwipe(){
            val touch = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    viewModel.removeDinoStat(recyclerList[position])
                }

            })
            touch.attachToRecyclerView(binding.childRecyclerView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DinoGroupStatsViewHolder {
        val binding = DinoGroupItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DinoGroupStatsViewHolder(binding,viewModel)
    }

    override fun onBindViewHolder(holder: DinoGroupStatsViewHolder, position: Int) {

        val key = groupMap.keys.toList()[position]
        val isExpanded = position === mExpandedPosition
        holder.binding.childRecyclerView.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
        holder.binding.labelLinearLayout.visibility = (if (isExpanded) View.VISIBLE else View.GONE)
        holder.binding.imageView.setImageResource(if(isExpanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float)
        holder.itemView.isActivated = isExpanded
        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(position)
        }
        holder.bind(key to groupMap[key]!!)
    }

    override fun getItemCount(): Int {
        return groupMap.size
    }
}