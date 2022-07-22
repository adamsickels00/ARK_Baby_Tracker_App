package com.example.arkbabytracker.troughtracker.food.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.arkbabytracker.troughtracker.data.DinoViewModel
import com.example.arkbabytracker.databinding.FragmentFoodItemBinding
import com.example.arkbabytracker.troughtracker.food.Food
import com.example.arkbabytracker.troughtracker.food.trough.Trough

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_FOOD = "food"
private const val ARG_FOODVALUE = "val"

/**
 * A simple [Fragment] subclass.
 * Use the [FoodItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    var food: Food? = null
    private var value:Int? = null
    private lateinit var binding:FragmentFoodItemBinding
    private val data:DinoViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            food = it.get(ARG_FOOD) as Food?
            value = it.getInt(ARG_FOODVALUE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodItemBinding.inflate(inflater,container,false)
        binding.foodName = food?.name ?: "No food"
        binding.quantity = value
        binding.executePendingBindings()
        binding.numStacks.doAfterTextChanged { text ->
            if (text.toString() != "") {
                val newValue = text.toString().toInt()
                val currentList = data.foodStacks.value!!
                currentList[food as Food] = newValue

                data.trough = Trough(currentList)
                data.foodStacks.value = currentList
            }
        }
        return binding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment FoodItemFragment.
         */
        @JvmStatic
        fun newInstance(foodName:Food, quantity:Int) =
            FoodItemFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_FOOD,foodName)
                    putInt(ARG_FOODVALUE,quantity)
                }
            }
    }
}