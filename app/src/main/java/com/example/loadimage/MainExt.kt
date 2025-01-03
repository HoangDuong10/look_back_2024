package com.example.loadimage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun Int.isValid(
    list: List<Pair<Int, Int>>
): Boolean {
    for (element in list) {
        if (this >= element.first && this <= element.second) {
            return true
        }
    }
    return false
}

fun String.parseIndex(
    list: List<String>
): MutableList<Pair<Int, Int>> {
    val listIndex: MutableList<Pair<Int, Int>> = mutableListOf()
    list.forEach { text ->
        val index = this.indexOf(text, 0)
        if (index != -1) {
            listIndex.add(
                Pair(
                    index,
                    (index + text.length)
                )
            )
        }
    }
    return listIndex
}

fun Context.shareImageUri(
    uri: Uri
) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/*"
    }
    startActivity(Intent.createChooser(shareIntent, "Cùng nhìn lại"))
}

fun shareImage(context: Context, bitmap: Bitmap) {
    val contentResolver = context.contentResolver
    val imageUri = saveImageToCache(context, bitmap)

    if (imageUri != null) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"))
    }
}

fun saveImageToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_image.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        FileProvider.getUriForFile(
            context,
            "com.example.testcapture.fileprovider222",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun rememberShareLauncher(
    context: Context,
    onResult: (Boolean) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            onResult(false)
        } else {
            onResult(true)
        }
    }
}

fun shareImage(
    context: Context,
    shareLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    imageUri: Uri,
    title: String = "Chia sẻ qua"
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    shareLauncher.launch(Intent.createChooser(shareIntent, title))
}

