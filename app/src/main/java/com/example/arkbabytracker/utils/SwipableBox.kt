package com.example.arkbabytracker.utils

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class SwipeDirection(val raw: Int) {
    Left(0),
    Initial(1),
    Right(2),
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipableBox(modifier:Modifier = Modifier, onSwipeRight:()->Unit, content:@Composable (modifier:Modifier)->Unit){
    val swipeableState = rememberSwipeableState(initialValue =SwipeDirection.Initial )
    if(swipeableState.currentValue == SwipeDirection.Right){
        onSwipeRight()
        LaunchedEffect(Unit){swipeableState.snapTo(SwipeDirection.Initial)}
    }
    val initColor = MaterialTheme.colors.background
    var color by remember{ mutableStateOf(initColor)}
    BoxWithConstraints(modifier = modifier.background(color)) {
        val constraintsScope = this
        val maxWidth = with(LocalDensity.current) {
            constraintsScope.maxWidth.toPx()
        }
        color = lerp(MaterialTheme.colors.background, Color.Red,swipeableState.offset.value/maxWidth)
        Box(
            modifier = Modifier
                .swipeable(
                    swipeableState,
                    anchors = mapOf(0f to SwipeDirection.Initial, maxWidth to SwipeDirection.Right),
                    orientation = Orientation.Horizontal
                )
                .offset {
                    IntOffset(
                        x = swipeableState.offset.value.roundToInt(),
                        y = 0
                    )
                }
                .fillMaxWidth()
                .padding(12.dp)

        ) {
            content(Modifier.background(color))
        }
    }
}

@Preview
@Composable
fun SwipableBoxPreview(){

    MaterialTheme(colors = darkColors()) {
        SwipableBox(onSwipeRight = { /*TODO*/ }) {
            Card(modifier = it){
                Text("Test", modifier = it)
            }
        }
    }
}