package com.example.arkbabytracker.food.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.arkbabytracker.databinding.FragmentFoodItemBinding
import com.example.arkbabytracker.food.Food

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_FOODNAME = "food"
private const val ARG_FOODVALUE = "val"

/**
 * A simple [Fragment] subclass.
 * Use the [FoodItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodItemFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var food: String? = null
    private var value:Int? = null
    private lateinit var binding:FragmentFoodItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            food = it.getString(ARG_FOODNAME)
            value = it.getInt(ARG_FOODVALUE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodItemBinding.inflate(inflater,container,false)
        binding.setFoodName(food)
        binding.quantity = value
        binding.executePendingBindings()
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(foodName:String, quantity:Int) =
            FoodItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FOODNAME,foodName)
                    putInt(ARG_FOODVALUE,quantity)
                }
            }
    }
}