package com.example.arkbabytracker.troughtracker.food.trough

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.arkbabytracker.ActivityViewModel
import com.example.arkbabytracker.troughtracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.FragmentTroughBinding
import com.example.arkbabytracker.troughtracker.food.Food

/**
 * A simple [Fragment] subclass.
 * Use the [TroughFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TroughFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var _binding:FragmentTroughBinding
    private val binding:FragmentTroughBinding
        get() = _binding
    private val activityVm by activityViewModels<ActivityViewModel>()
    lateinit var group:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            group = it.getString("Group","Default")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTroughBinding.inflate(inflater,container,false)
        activityVm.troughMap.putIfAbsent(group, MutableLiveData(Trough(mutableMapOf())))
        binding.adapter = TroughAdapter(activityVm.troughMap[group]?.value!!)
        activityVm.troughMap[group]?.observe(viewLifecycleOwner){
            binding.adapter!!.setData(it)
            binding.executePendingBindings()
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment TroughFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(group:String) =
            TroughFragment().apply {
                arguments = Bundle().apply {
                    putString("Group",group)
                }
            }
    }
}