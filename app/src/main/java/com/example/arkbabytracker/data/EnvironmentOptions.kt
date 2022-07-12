package com.example.arkbabytracker.data

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.arkbabytracker.databinding.FragmentEnvironmentOptionsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [EnvironmentOptions.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnvironmentOptions : Fragment() {

    lateinit var data:DinoViewModel

    //Keep this in sync with EnvironmentViewModel.kt properties
    var envVariables:Map<String,MutableLiveData<Double>> = mapOf(
        Pair("Event Multiplier",Environment.eventMultiplier),
        Pair("Maewing Effectiveness",Environment.maewingFoodMultiplier),
    )

    lateinit var binding:FragmentEnvironmentOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnvironmentOptionsBinding.inflate(inflater, container, false)
        envVariables.forEach{
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(binding.fragmentContainer.id,createEnvFrag(it.key,it.value))
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