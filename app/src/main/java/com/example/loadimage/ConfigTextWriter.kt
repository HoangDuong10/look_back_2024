package com.example.loadimage

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class ConfigTextWriter(
    var color: Color = Color.Black,
    var textSize: TextUnit = 16.sp,
    var font: FontWeight = FontWeight.Bold
)