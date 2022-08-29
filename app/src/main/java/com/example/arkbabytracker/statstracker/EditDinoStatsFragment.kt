package com.example.arkbabytracker.statstracker

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.example.arkbabytracker.databinding.FragmentEditDinoStatsBinding
import com.example.arkbabytracker.statstracker.data.DinoGender
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.statstracker.data.DinoStatsRepository
import com.example.arkbabytracker.troughtracker.dinos.data.allDinoList
import com.example.arkbabytracker.utils.DinoColorUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [EditDinoStatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class EditDinoStatsFragment : Fragment() {

    @Inject lateinit var dinoStatsRepository: DinoStatsRepository

    private var _binding: FragmentEditDinoStatsBinding? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDinoStatsBinding.inflate(inflater,container,false)
        CoroutineScope(Dispatchers.IO).launch {
            val dino = dinoStatsRepository.getDinoById(requireArguments().getInt("dinoId"))
            binding.dino = dino
            binding.submitDinoStatsButton.setOnClickListener {
                updateDino(dino)
                requireActivity().onBackPressed()
            }
            binding.genderSpinner.setSelection(DinoGender.values().map { it.name }.indexOf(dino.gender.name))
        }
        binding.genderSpinner.adapter = ArrayAdapter(requireContext(),android.R.layout.select_dialog_item,DinoGender.values().map { it.name })

        val adapter = ArrayAdapter<String>(requireContext(),android.R.layout.select_dialog_item, allDinoList.map{it.simpleName})
        binding.typeAutoComplete.setAdapter(adapter)
        binding.typeAutoComplete.threshold = 0
        setProperColorsAfterTextChanged(binding.color0EditText)
        setProperColorsAfterTextChanged(binding.color1EditText)
        setProperColorsAfterTextChanged(binding.color2EditText)
        setProperColorsAfterTextChanged(binding.color3EditText)
        setProperColorsAfterTextChanged(binding.color4EditText)
        setProperColorsAfterTextChanged(binding.color5EditText)
        return binding.root
    }

    fun updateDino(d:DinoStats){

        val mapping = mutableMapOf<String,DinoGender>()
        DinoGender.values().forEach { mapping[it.name] = it }


        val type = binding.typeAutoComplete.text.toString()
        val name = binding.nameEditText.text.toString()
        val health = binding.healthEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val stamina = binding.staminaEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val weight = binding.weightEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val damage = binding.damageEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val oxygen = binding.oxygenEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val food = binding.foodEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val move = binding.moveSpeedEditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0}
        val gender = mapping[binding.genderSpinner.selectedItem]!!
        val colorList = listOf(
            binding.color0EditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0},
            binding.color1EditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0},
            binding.color2EditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0},
            binding.color3EditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0},
            binding.color4EditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0},
            binding.color5EditText.text.toString().let{if(it.isNotEmpty()) it.toInt() else 0},
        )

        val dino = DinoStats(
            type,
            name,
            health,
            stamina,
            oxygen,
            food,
            weight,
            move,
            damage,
            colorList,
            gender
        )

        dino.id = d.id

        CoroutineScope(Dispatchers.IO).launch {
            dinoStatsRepository.update(dino)
        }


    }

    fun setProperColorsAfterTextChanged(textBox: EditText){
        textBox.doAfterTextChanged { editable ->
            var colorID:Int? = null
            try {
                colorID = editable.toString().toInt()
            } catch(x:java.lang.NumberFormatException){
                Log.d("NumberFormatCatch","Caught number format exception")
            }
            val colorResource = colorID?.let { DinoColorUtils.getColorOfIdIfExists(it) }
            if(colorResource != null) {
                textBox.setBackgroundResource(colorResource)
                context?.let {
                    textBox.setTextColor(DinoColorUtils.getTextColorForBackgroundHex(it.getColor(colorResource),it) )
                }
            } else{
                textBox.setBackgroundColor(Color.TRANSPARENT)
                context?.let {
                    val x = TypedValue()
                    it.theme.resolveAttribute(com.google.android.material.R.attr.colorOnBackground,x,true)
                    textBox.setTextColor(x.data)
                }
            }
        }
    }
}