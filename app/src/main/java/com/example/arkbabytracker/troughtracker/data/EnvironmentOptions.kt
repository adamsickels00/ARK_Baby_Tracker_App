package com.example.arkbabytracker.troughtracker.data

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.*
import androidx.lifecycle.MutableLiveData
import com.example.arkbabytracker.databinding.FragmentEnvironmentOptionsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [EnvironmentOptions.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnvironmentOptions : Fragment() {

    val data:DinoViewModel by viewModels(ownerProducer = {requireParentFragment()})
    val env : EnvironmentViewModel by viewModels(ownerProducer = {requireParentFragment()})
    private var fragmentNeeded = true



    //Keep this in sync with EnvironmentViewModel.kt properties
    lateinit var envVariables:Map<String,MutableLiveData<Double>>
    lateinit var binding:FragmentEnvironmentOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNeeded = savedInstanceState == null
        envVariables = mapOf(
            Pair("Event Multiplier",env.eventMultiplier),
            Pair("Maewing Effectiveness",env.maewingFoodMultiplier),
            "Hunger Multiplier" to env.hungerMultiplier,
            "Lag Correction Multiplier" to env.lagCorrection
        )
        childFragmentManager.setFragmentResultListener("newValue",requireActivity()){ _: String, bundle: Bundle ->
            val newDouble = bundle.getDouble("value")
            val name = bundle.getString("name")
            val liveData = envVariables[name]!!
            val temp = data.foodStacks.value
            liveData.value = newDouble
        }

    }


    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEnvironmentOptionsBinding.inflate(inflater, container, false)
        val ll = LinearLayout(context)
        ll.id = 1
        ll.layoutParams = binding.fragmentContainer.layoutParams
        if(fragmentNeeded) {
            envVariables.forEach {
                childFragmentManager.commit {
                    setReorderingAllowed(true)
                    val frag = createEnvFrag(it.key, it.value.value!!)
                    add(binding.fragmentContainer.id, frag)
                }
            }
            fragmentNeeded = false
        }
        binding.fragmentContainer.addView(ll,0)

        return binding.root
    }
    fun createEnvFrag(name:String,value:Double):EnvironmentEditableItem{
        return EnvironmentEditableItem.newInstance(name,value)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EnvironmentOptions().apply {

            }
    }
}

@BindingAdapter("quantity")
fun setQuantityString(v:EditText,value:Double){
    val formattedString = "%.2f".format(value)
    v.setText(formattedString,TextView.BufferType.EDITABLE)
}