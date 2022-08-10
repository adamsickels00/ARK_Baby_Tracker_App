package com.example.arkbabytracker.troughtracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.arkbabytracker.R
import com.example.arkbabytracker.troughtracker.food.trough.Trough
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BabyTrackerGroupHolder.newInstance] factory method to
 * create an instance of this fragment.
 */
class BabyTrackerGroupHolder : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val groups = listOf("Default","Empty")
        val thisView = inflater.inflate(R.layout.fragment_baby_tracker_group_holder, container, false)
        val tabs = thisView.findViewById<TabLayout>(R.id.tabLayout)
        val vp = thisView.findViewById<ViewPager2>(R.id.viewPager)
        vp.adapter = TroughGroupAdapter(lifecycle,childFragmentManager,groups)
        TabLayoutMediator(tabs, vp
        ) { tab, position -> // Styling each tab here
            tab.text = groups[position]
        }.attach()
        // Inflate the layout for this fragment
        return thisView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BabyTrackerGroupHolder.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BabyTrackerGroupHolder().apply {

            }
    }
}