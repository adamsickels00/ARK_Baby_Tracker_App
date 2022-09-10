package com.example.arkbabytracker.statstracker.qrcode.reader

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.example.arkbabytracker.R
import com.example.arkbabytracker.colorsearch.DinoList
import com.example.arkbabytracker.statstracker.data.DinoStats
import com.example.arkbabytracker.troughtracker.dinos.data.Dino
import com.example.arkbabytracker.utils.MyTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ReadQrFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ReadQrFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val json = arguments?.getString("dino")?:"No Luck"
        val token = object : TypeToken<DinoStats>(){}.type
        val dino = Gson().fromJson<DinoStats>(json,token)

        val navigation = ReadQrFragmentDirections.actionReadQrFragmentToDinoStatsFragment()
        val navFunc = {findNavController().navigate(navigation)}
        // Inflate the layout for this fragment
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MyTheme() {
                    ImportDinoScreen(dino = dino, onImport = navFunc)
                }

            }
        }
    }

}