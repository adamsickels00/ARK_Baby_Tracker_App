package com.example.arkbabytracker.statstracker.qrcode.writer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import com.example.arkbabytracker.ActivityViewModel
import com.example.arkbabytracker.utils.MyTheme


class QrDisplayFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activityVm by activityViewModels<ActivityViewModel>()
        // Inflate the layout for this fragment
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyTheme {
                    QrDisplayScreen(dinoList = activityVm.dinoStatsList.value!!)
                }
            }
        }
    }

}