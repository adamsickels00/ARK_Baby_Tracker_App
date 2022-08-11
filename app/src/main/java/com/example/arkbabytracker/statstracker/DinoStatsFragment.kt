package com.example.arkbabytracker.statstracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.arkbabytracker.R
import com.example.arkbabytracker.databinding.FragmentDinoStatsBinding
import com.example.arkbabytracker.statstracker.adapter.DinoGroupStatsAdapter
import com.example.arkbabytracker.statstracker.adapter.DinoStatsAdapter
import com.example.arkbabytracker.statstracker.data.DinoMenuViewModel
import com.example.arkbabytracker.statstracker.data.DinoStatsDatabase
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DinoStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DinoStatsFragment : Fragment() {

    private var _binding:FragmentDinoStatsBinding? = null
    val binding : FragmentDinoStatsBinding get() = _binding!!

    val dinoStatsViewModel by viewModels<DinoMenuViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDinoStatsBinding.inflate(inflater, container, false)



        dinoStatsViewModel.liveDinoList.observe(viewLifecycleOwner){
            binding.adapter = DinoGroupStatsAdapter(dinoStatsViewModel.dinoByType,dinoStatsViewModel)
        }

        dinoStatsViewModel.getFromDatabase()

        binding.addDinoStatsButton.setOnClickListener {
            val action = DinoStatsFragmentDirections.actionDinoStatsFragmentToAddDinoStatsFragment()

            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        dinoStatsViewModel.clearDb(db)
    }

    companion object {

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TurtleMenu.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DinoStatsFragment()
    }
}