package com.example.arkbabytracker.food.fragment

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.arkbabytracker.food.Food

class FoodBindingUtils {
    @BindingAdapter("foodName")
    fun setFoodName(view: TextView,foodPair: Pair<Food, Int>){
        view.text = foodPair.first.name
    }

    @BindingAdapter("foodValue")
    fun setFoodValue(view: EditText,foodPair: Pair<Food, Int>){
        view.setText(foodPair.second,TextView.BufferType.EDITABLE)
    }
}