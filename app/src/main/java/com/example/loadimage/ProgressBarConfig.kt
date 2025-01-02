package com.example.loadimage

import kotlin.random.Random

data class ProgressBarConfig(
    var action: String = ReminderConstants.RESET,
    var configValue: Int? = Random.nextInt()
)

fun ProgressBarConfig.pause(): ProgressBarConfig {
    return this.copy(
        action = ReminderConstants.PAUSE
    )
}

fun ProgressBarConfig.resume(): ProgressBarConfig {
    return this.copy(
        action = ReminderConstants.RESUME
    )
}

fun ProgressBarConfig.reset(): ProgressBarConfig {
    return this.copy(
        action = ReminderConstants.RESET,
        configValue = Random.nextInt()
    )
}