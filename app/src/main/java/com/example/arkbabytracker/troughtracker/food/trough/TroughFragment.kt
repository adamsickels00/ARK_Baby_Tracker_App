package com.example.arkbabytracker.troughtracker.food.trough

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.arkbabytracker.troughtracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.FragmentTroughBinding

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
    private val data by activityViewModels<DinoViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTroughBinding.inflate(inflater,container,false)
        binding.adapter = TroughAdapter(data.trough)
        data.simTrough.observe(viewLifecycleOwner){
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
        fun newInstance() =
            TroughFragment().apply {

            }
    }
}