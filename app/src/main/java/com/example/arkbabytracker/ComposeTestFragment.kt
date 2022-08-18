package com.example.arkbabytracker

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Color
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.arkbabytracker.compose.ColoredTextBox
import com.example.arkbabytracker.compose.LastLoginScreenCompose
import com.example.arkbabytracker.lastlogin.LastLoginScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.format.TextStyle

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ComposeTestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ComposeTestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme(
                    colors = if(isSystemInDarkTheme()) darkColors() else lightColors()
                ){

                    val servers = remember() { mutableStateListOf<String>()}
                    var text by remember() { mutableStateOf("")}

                    val context = LocalContext.current
                    val serverJson = context.getSharedPreferences("Servers",0).getString("ServerList","[]")
                    val type = object : TypeToken<List<String>>(){}.type
                    val serverListInit = Gson().fromJson<List<String>>(serverJson,type)?:listOf()
                    servers.addAll(serverListInit)


                    LazyColumn() {
                        items(servers) { LastLoginScreenCompose(it) {
                            servers.remove(it)
                            context.getSharedPreferences("Servers",0).edit(){
                                putString("ServerList", Gson().toJson(servers))
                                commit()
                            }
                        } }
                        item{TextField(value = text, onValueChange = { text = it })}
                        item {
                            Button(onClick = {
                                servers.add(text)
                                context.getSharedPreferences("Servers",0).edit(){
                                    putString("ServerList", Gson().toJson(servers))
                                    commit()
                                }
                            }) {
                                Text("Add Server")
                            }
                        }
                    }

                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ComposeTestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ComposeTestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}