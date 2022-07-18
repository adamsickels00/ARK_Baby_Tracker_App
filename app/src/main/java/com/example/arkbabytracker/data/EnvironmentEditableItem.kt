package com.example.arkbabytracker.data

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.arkbabytracker.R
import com.example.arkbabytracker.databinding.FragmentEnvironmentEdittableItemBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_NAME = "name"
private const val ARG_VAL = "value"

/**
 * A simple [Fragment] subclass.
 * Use the [EnvironmentEditableItem.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnvironmentEditableItem : Fragment() {

    var name:String? = null
    lateinit var binding:FragmentEnvironmentEdittableItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnvironmentEdittableItemBinding.inflate(
            inflater,container,false
        )
        val itemName = arguments?.getString(ARG_NAME)
        val initialValue = arguments?.getDouble(ARG_VAL)
        // Inflate the layout for this fragment
        binding.item = itemName
        binding.quantity = initialValue

        binding.valueTextBox.doAfterTextChanged{
            if(it.toString() != "") {
                val newValue = it.toString().toDouble()
                val bundle = bundleOf("name" to itemName, "value" to newValue)
                setFragmentResult("newValue",bundle)
            }
        }

        return binding.root
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EnvironmentEdittableItem.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(name:String, initialValue:Double) =
            EnvironmentEditableItem().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME,name)
                    putDouble(ARG_VAL,initialValue)
                }
            }
    }
}