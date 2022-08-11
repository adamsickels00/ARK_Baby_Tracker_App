package com.example.arkbabytracker.troughtracker.food.trough

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.arkbabytracker.troughtracker.BabyTroughFragment

class TroughFragmentAdapter(lc: Lifecycle, fm: FragmentManager, val groups:List<String>) : FragmentStateAdapter(fm,lc) {
    override fun getItemCount(): Int {
        return groups.size
    }

    override fun createFragment(position: Int): Fragment {
        return TroughFragment.newInstance(groups[position])
    }

}