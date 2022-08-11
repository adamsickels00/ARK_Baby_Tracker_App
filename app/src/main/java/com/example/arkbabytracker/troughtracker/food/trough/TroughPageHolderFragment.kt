package com.example.arkbabytracker.troughtracker.food.trough

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.arkbabytracker.R
import com.example.arkbabytracker.troughtracker.TroughGroupAdapter
import com.example.arkbabytracker.usergroups.UserGroupsUtils
import com.example.arkbabytracker.usergroups.UserGroupsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass.
 * Use the [TroughPageHolderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TroughPageHolderFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val groups = UserGroupsUtils.loadGroups(context)
        val thisView = inflater.inflate(R.layout.fragment_trough_page_holder, container, false)
        val tabs = thisView.findViewById<TabLayout>(R.id.tabLayout)
        val vp = thisView.findViewById<ViewPager2>(R.id.viewPager)
        vp.adapter = TroughFragmentAdapter(lifecycle,childFragmentManager,groups)
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
         * @return A new instance of fragment TroughPageHolderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TroughPageHolderFragment().apply {

            }
    }
}