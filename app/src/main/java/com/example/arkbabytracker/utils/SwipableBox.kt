package com.example.arkbabytracker.utils

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

enum class SwipeDirection(val raw: Int) {
    Left(0),
    Initial(1),
    Right(2),
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipableBox(modifier:Modifier = Modifier, onSwipeRight:()->Unit, content:@Composable ()->Unit){
    val swipeableState = rememberSwipeableState(initialValue =SwipeDirection.Initial )
    if(swipeableState.currentValue == SwipeDirection.Right){
        onSwipeRight()
    }
    BoxWithConstraints(modifier = modifier) {
        val constraintsScope = this
        val maxWidth = with(LocalDensity.current) {
            constraintsScope.maxWidth.toPx()
        }
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
            content()
        }
    }
}

/*
fun GroupItem(group:String, onSwipeAction:(String)->Unit, swipeableState: SwipeableState<SwipeDirection> = rememberSwipeableState(initialValue = SwipeDirection.Initial)){

    if(swipeableState.currentValue == SwipeDirection.Right){
        onSwipeAction(group)
    }

    }
}

 */