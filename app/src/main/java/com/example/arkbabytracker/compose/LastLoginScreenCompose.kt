package com.example.arkbabytracker.compose

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.arkbabytracker.R
import com.example.arkbabytracker.lastlogin.LastLoginScreen
import com.example.arkbabytracker.statstracker.data.DinoStatsDao
import com.example.arkbabytracker.utils.TimeDisplayUtil
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject


class LastLoginScreenState(
    var lastLogin:Long = Instant.now().epochSecond
)


@Composable
fun LastLoginScreenCompose(server:String, onDeletePressed : ()->Unit){

    val pref = LocalContext.current.getSharedPreferences("Log",0)
    var lastLogin by remember {
        mutableStateOf(pref.getLong("StartTime$server",0))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box() {
            Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    server,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    "Time since last login: ${lastLogin?.let { LastLoginScreen.timeFromNowTo(it) }}",
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 24.sp
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(48.dp)
                        .fillMaxWidth()
                ) {
                    Button(onClick = {
                        val time = Instant.now().epochSecond
                        pref.edit {
                            putLong("StartTime$server", time)
                            apply()
                        }
                        lastLogin = time

                    }) {
                        Text("Login")
                    }
                }
            }
            FloatingActionButton(onClick = onDeletePressed,
                modifier = Modifier.align(Alignment.TopEnd),
                backgroundColor = MaterialTheme.colors.background,
            ) {
                Text("X", fontSize = 20.sp)
            }
        }
    }
}

@Preview
@Composable
fun LastLoginScreenPreview(){
    MaterialTheme(
        colors = darkColors()
    ) {

        LastLoginScreenCompose("Test",{})
    }

}