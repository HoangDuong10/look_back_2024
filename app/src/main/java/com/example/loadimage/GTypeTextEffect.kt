package com.example.loadimage

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun TypewriterEffectWithVideoControl(
    text: String,
    isVideoPlaying: Boolean,
    delayMillis: Long = 50L,
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isVideoPlaying) {
        if (isVideoPlaying) {
            while (currentIndex < text.length) {
                displayedText += text[currentIndex]
                currentIndex++
                delay(delayMillis)
            }
        }
    }

    Text(
        text = displayedText,
        modifier = modifier,
        fontSize = 30.sp
    )
}

//@Composable
//fun GTypewriterTextEffect(
//    text: String,
//    minCharacterChunk: Int = 1,
//    maxCharacterChunk: Int = 1,
//    onEffectCompleted: () -> Unit = {},
//    displayTextComposable: @Composable (displayedText: String) -> Unit
//) {
//    require(minCharacterChunk <= maxCharacterChunk)
//    var displayedText by remember { mutableStateOf("") }
//    displayTextComposable(displayedText)
//    LaunchedEffect(text) {
//        val textLength = text.length
//        var endIndex = 0
//
//        while (endIndex < textLength) {
//            Log.d("lifecycle11","${displayedText}")
//            endIndex = minOf(
//                endIndex + Random.nextInt(minCharacterChunk, maxCharacterChunk + 1),
//                textLength
//            )
//            displayedText = text.substring(startIndex = 0, endIndex = endIndex)
//            delay(30.toLong())
//        }
//        onEffectCompleted()
//    }
//}
@Composable
fun GTypewriterTextEffect(
    text: String,
    minCharacterChunk: Int = 1,
    isVideoPlaying: Boolean,
    maxCharacterChunk: Int = 1,
    onEffectCompleted: () -> Unit = {},
    displayTextComposable: @Composable (displayedText: String) -> Unit
) {
    require(minCharacterChunk <= maxCharacterChunk)
    var displayedText by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }
    displayTextComposable(displayedText)
    LaunchedEffect(isVideoPlaying) {
        if (isVideoPlaying) {
            while (currentIndex < text.length) {
                displayedText += text[currentIndex]
                currentIndex++
                delay(25)
            }
        }
        onEffectCompleted()
    }
}


@Composable
fun MultipleStyleText(
    displayTextComposable: String,
    listIndexHighLight: List<Pair<Int, Int>>,
    configTextNormal: ConfigTextWriter,
    configTextHighLight: ConfigTextWriter,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = buildAnnotatedString {
            displayTextComposable.forEachIndexed { index, text ->
                val isValid = index.isValid(listIndexHighLight)
                if (isValid) {
                    withStyle(
                        style = SpanStyle(
                            color = configTextHighLight.color,
                            fontSize = configTextHighLight.textSize,
                            fontWeight = configTextHighLight.font
                        )
                    ) {
                        append("$text")
                    }
                } else {
                    withStyle(
                        style = SpanStyle(
                            color = configTextNormal.color,
                            fontSize = configTextNormal.textSize,
                            fontWeight = configTextNormal.font
                        )
                    ) {
                        append("$text")
                    }
                }
            }
        },
        textAlign = textAlign
    )
}


@Composable
fun TypewriterTextEffectView(
    modifier: Modifier,
    textData: String,
    textAlign: TextAlign = TextAlign.Center,
    textHighLight: List<String>,
    configTextNormal: ConfigTextWriter,
    configTextHighLight: ConfigTextWriter,
    isShowFull: Boolean = false,
    isVideoPlaying: Boolean,
    onFinish: @Composable (isFinish: Boolean) -> Unit,
) {
    val listIndex = textData.parseIndex(textHighLight)
    Box(
        modifier = modifier
    ) {
        if (isShowFull) {
            MultipleStyleText(
                textData,
                listIndex,
                configTextNormal,
                configTextHighLight
            )
        } else {
            GTypewriterTextEffect(
                text = textData,
                isVideoPlaying = isVideoPlaying
            ) { displayedText ->
                MultipleStyleText(
                    displayedText,
                    listIndex,
                    configTextNormal,
                    configTextHighLight,
                    textAlign = textAlign
                )
                if (displayedText == textData) {
                    onFinish(true)
                }
            }
        }
    }
}