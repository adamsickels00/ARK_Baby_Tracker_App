package com.example.arkbabytracker.troughtracker

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.arkbabytracker.troughtracker.data.DinoViewModel

class TroughGroupAdapter(lc: Lifecycle, fm:FragmentManager, val groups:List<String>) : FragmentStateAdapter(fm,lc) {
    override fun getItemCount(): Int {
        return groups.size
    }

    override fun createFragment(position: Int): Fragment {
        return BabyTroughFragment.newInstance(groups[position])
    }

}