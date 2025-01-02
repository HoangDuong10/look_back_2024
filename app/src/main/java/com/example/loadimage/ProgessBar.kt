package com.example.loadimage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GSlicedProgressBar(
    modifier: Modifier,
    steps: Int,
    currentStep: Int,
    config: ProgressBarConfig,
    time: Int,
    onFinished: () -> Unit
) {
    val percent = remember { Animatable(0f) }

    LaunchedEffect(config) {
        var isAnimateTo = false
        when (config.action) {
            ReminderConstants.PAUSE -> {
                percent.stop()
            }

            ReminderConstants.RESUME -> {
                isAnimateTo = true
            }

            ReminderConstants.RESET -> {
                percent.stop()
                percent.snapTo(0f)
                isAnimateTo = true
            }
        }

        if (isAnimateTo) {
            percent.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (time * (1f - percent.value)).toInt(),
                    easing = LinearEasing
                )
            )
            onFinished()
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier

    ) {
        for (index in 1..steps) {
            Row(
                modifier = Modifier
                    .height(4.dp)
                    .clip(RoundedCornerShape(20, 20, 20, 20))
                    .weight(1f)
                    .background(Color.White.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxHeight()
                        .let {
                            when (index) {
                                currentStep -> it.fillMaxWidth(percent.value)
                                in 0..currentStep -> it.fillMaxWidth(1f)
                                else -> it
                            }
                        }
                ) {}
            }
            if (index != steps) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}
