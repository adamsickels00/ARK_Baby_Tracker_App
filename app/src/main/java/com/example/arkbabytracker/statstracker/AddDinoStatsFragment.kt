package com.example.arkbabytracker.statstracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.room.Room
import com.example.arkbabytracker.R
import com.example.arkbabytracker.databinding.FragmentAddDinoStatsBinding
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.statstracker.data.DinoStatsDao
import com.example.arkbabytracker.statstracker.data.DinoStatsDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [AddDinoStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AddDinoStatsFragment () : Fragment() {

    @Inject lateinit var dinoStatsDao: DinoStatsDao
    private var _binding: FragmentAddDinoStatsBinding? = null
    val binding get()=_binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddDinoStatsBinding.inflate(inflater, container, false)
        binding.submitDinoStatsButton.setOnClickListener {
            addDino()
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    fun addDino(){

        val type = binding.typeEditText.text.toString()
        val health = binding.healthEditText.text.toString().toInt()
        val stamina = binding.staminaEditText.text.toString().toInt()
        val weight = binding.weightEditText.text.toString().toInt()
        val damage = binding.damageEditText.text.toString().toInt()
        val oxygen = binding.oxygenEditText.text.toString().toInt()
        val food = binding.foodEditText.text.toString().toInt()
        val move = binding.moveSpeedEditText.text.toString().toInt()
        val torpor = binding.torporEditText.text.toString().toInt()

        val dino = DinoStats(type,health,stamina,oxygen,food,weight,move,torpor,damage,listOf(1,2,56,4,1,1))

        CoroutineScope(Dispatchers.IO).launch {
            dinoStatsDao.insert(dino)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment AddDinoStatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            AddDinoStatsFragment()
    }
}