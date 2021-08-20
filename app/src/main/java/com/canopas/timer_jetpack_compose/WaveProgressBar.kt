package com.canopas.timer_jetpack_compose

import androidx.compose.runtime.Composable

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@Composable
fun WaveProgressBar(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    isRunning: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val radius: Dp = 110.dp
    val durationInMilliSecond = 500
    val strokeWidth: Dp = 10.dp
    val strokeBackgroundWidth: Dp = 10.dp
    val progressColor: Color = Color.Yellow
    val progressBackgroundColor: Color = Color.DarkGray

    val stroke = with(LocalDensity.current) {
        Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
    }

    val strokeBackground = with(LocalDensity.current) {
        Stroke(width = strokeBackgroundWidth.toPx())
    }

    val strokeReverse = Stroke(strokeBackground.width / 4)

    val currentState = remember {
        MutableTransitionState(AnimatedArcState.START)
            .apply { targetState = AnimatedArcState.END }
    }

    val animatedProgress = updateTransition(currentState, label = "")
    val animatedCircle = rememberInfiniteTransition()

    val progress by animatedProgress.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = durationInMilliSecond,
                easing = LinearEasing,
                delayMillis = 0
            )
        }, label = ""
    ) { state ->
        when (state) {
            AnimatedArcState.START -> 0f
            AnimatedArcState.END -> progress
        }
    }

    val animatedReverse by animatedCircle.animateFloat(
        initialValue = 1.40f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    val animatedColor by animatedCircle.animateColor(
        initialValue = progressBackgroundColor.copy(0.5f),
        targetValue = progressColor.copy(0.8f),
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2)
    ) {
        Canvas(modifier = modifier.size(radius * 2)) {
            val higherStrokeWidth =
                if (stroke.width > strokeBackground.width) stroke.width else strokeBackground.width
            val radius = (size.minDimension - higherStrokeWidth) / 2
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - radius,
                halfSize.height - radius
            )
            val size = Size(radius * 2, radius * 2)
            val sweep = progress * 360 / 100

            drawArc(
                startAngle = 0f, sweepAngle = 360f,
                color = progressBackgroundColor, useCenter = false,
                topLeft = topLeft, size = size, style = strokeBackground
            )

            if (isRunning) {
                drawCircle(
                    color = animatedColor,
                    style = strokeReverse,
                    radius = radius * animatedReverse,
                )
            }

            drawArc(
                color = progressColor,
                startAngle = 270f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = stroke
            )
        }
        content()
    }
}

private enum class AnimatedArcState {
    START,
    END
}