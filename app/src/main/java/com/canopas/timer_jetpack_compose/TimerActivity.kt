package com.canopas.timer_jetpack_compose

import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowDropUp
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.canopas.timer_jetpack_compose.TimerViewModel.Companion.TimeUnit.*
import com.canopas.timer_jetpack_compose.ui.theme.TimerjetpackcomposeTheme

class MainActivity : ComponentActivity() {

    private val timerViewModel by viewModels<TimerViewModel>()

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerjetpackcomposeTheme {
                // A surface container using the 'background' color from the theme
                TimeApp(timerViewModel)

            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimeApp(viewmodel: TimerViewModel) {

    val hr = viewmodel.hours.observeAsState()
    val minute = viewmodel.minutes.observeAsState()
    val secs = viewmodel.seconds.observeAsState()

    val isRunning = viewmodel.isRunning.observeAsState()
    val progress = viewmodel.progress.observeAsState(1f)
    val displayTime = viewmodel.time.observeAsState(initial = "00:00:00")

    Surface(color = MaterialTheme.colors.background) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Black)) {
            Text(
                text = "Timer",
                fontSize = 42.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(CenterHorizontally),
                style = typography.h6,
                color = Color.White,
                fontStyle = FontStyle.Normal
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(Modifier.padding(40.dp), contentAlignment = Alignment.Center) {
                    WaveProgressBar(progress = progress.value*100,isRunning = isRunning.value?:false) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            ReusableHeaderText(
                                text = displayTime.value,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp, top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = HOUR.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = typography.caption
                )
                Text(
                    text = MIN.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = typography.caption
                )
                Text(
                    text = SEC.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    style = typography.caption
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(4.dp))
                    .padding(12.dp, top = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TimerView(
                    value = hr.value,
                    timeUnit = HOUR,
                    enabled = isRunning.value != true
                ) {
                    viewmodel.updateTime(HOUR, it)
                }

                Text(text = " : ", fontSize = 36.sp)

                TimerView(
                    value = minute.value,
                    timeUnit = MIN,
                    enabled = isRunning.value != true
                ) {
                    viewmodel.updateTime(MIN, it)
                }

                Text(text = " : ", fontSize = 36.sp)

                TimerView(
                    value = secs.value,
                    timeUnit = SEC,
                    enabled = isRunning.value != true
                ) {
                    viewmodel.updateTime(SEC, it)
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, end = 40.dp, top = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        if (!((secs.value ?: 0) == 0 && (minute.value ?: 0) == 0 && (hr.value
                                ?: 0) == 0)
                        ) {
                            if (isRunning.value != true) {
                                viewmodel.startTimer()
                            } else {
                                viewmodel.stopTimer()
                            }
                        }
                    }, modifier = Modifier
                        .padding(16.dp)
                        .height(48.dp)
                        .widthIn(min = 48.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Yellow,
                        contentColor = Color.Black
                    )
                ) {

                        Text(
                            text = if (isRunning.value == true) {
                                "Pause"
                            } else {
                                "Count Down!"
                            }
                        )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun TimerButton(
    isEnabled: Boolean,
    timeOperator: TimerViewModel.Companion.TimeOperator,
    onClick: (TimerViewModel.Companion.TimeOperator) -> Unit
) {

    AnimatedVisibility(
        visible = isEnabled
    ) {
        Button(
            onClick = { onClick.invoke(timeOperator) },
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                //    backgroundColor = MaterialTheme.colors.background,
                disabledBackgroundColor = MaterialTheme.colors.background
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {

            when (timeOperator) {
                TimerViewModel.Companion.TimeOperator.INCREASE -> Icon(
                    Icons.Outlined.ArrowDropUp,
                    null,
                    Modifier.size(24.dp)
                )
                TimerViewModel.Companion.TimeOperator.DECREASE -> Icon(
                    Icons.Outlined.ArrowDropDown,
                    null,
                    Modifier.size(24.dp)
                )
            }
        }

    }
}


@ExperimentalAnimationApi
@Composable
fun TimerView(
    value: Int?,
    timeUnit: TimerViewModel.Companion.TimeUnit,
    enabled: Boolean,
    onClick: (TimerViewModel.Companion.TimeOperator) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val typography = MaterialTheme.typography

        Spacer(modifier = Modifier.height(8.dp))

        TimerButton(
            timeOperator = TimerViewModel.Companion.TimeOperator.INCREASE,
            isEnabled = enabled,
            onClick = onClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = String.format("%02d", value ?: 0),
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        TimerButton(
            timeOperator = TimerViewModel.Companion.TimeOperator.DECREASE,
            isEnabled = enabled,
            onClick = onClick
        )
    }

}

@Composable
fun ReusableHeaderText(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 42.sp,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h1,
        color = color
    )
}

