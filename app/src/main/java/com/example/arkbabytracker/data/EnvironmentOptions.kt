package com.example.arkbabytracker.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.arkbabytracker.databinding.FragmentEnvironmentOptionsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [EnvironmentOptions.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnvironmentOptions : Fragment() {

    lateinit var data:DinoViewModel
    //Keep this in sync with EnvironmentViewModel.kt properties
    lateinit var envVariables:Map<String,MutableLiveData<Double>>

    lateinit var binding:FragmentEnvironmentOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val env : EnvironmentViewModel by viewModels()
        envVariables = mapOf(
            Pair("Event Multiplier",env.eventMultiplier),
            Pair("Maewing Effectiveness",env.maewingFoodMultiplier),
        )
        binding = FragmentEnvironmentOptionsBinding.inflate(inflater, container, false)
        if(savedInstanceState == null) {
            envVariables.forEach {
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add(binding.fragmentContainer.id, createEnvFrag(it.key, it.value))
                }
            }
        }

        return binding.root
    }
    fun createEnvFrag(name:String, data: MutableLiveData<Double>):EnvironmentEditableItem{
        return EnvironmentEditableItem.newInstance().registerParams(name,data)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EnvironmentOptions().apply {
            }
    }
}