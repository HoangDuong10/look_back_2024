package com.example.loadimage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
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

private val _uriFlow = MutableStateFlow<Uri?>(null)
val uriFlow: StateFlow<Uri?> = _uriFlow
@OptIn(ExperimentalCoroutinesApi::class)
private fun saveImageToSpecialApp(
    bitmap: Bitmap,
    context: Context,
    onResult: (Boolean) -> Unit
): Flow<Uri> {
    return flow {
        // Bước 1: Lưu ảnh vào bộ nhớ cache và nhận về Uri
        val uri = saveImageToCache(context, bitmap)
        if (uri != null) {
            // Bước 2: Emit uri sau khi lưu thành công
            emit(uri)
        } else {
            // Nếu không thể lưu ảnh, phát tín hiệu lỗi hoặc trả về null
            throw Exception("Không thể lưu ảnh vào bộ nhớ cache.")
        }
    }
        .onEach { uri ->
            _uriFlow.value = uri
        }

}

@Composable
fun shareImageWithResult(
    context: Context,
    imageUri: Uri,
    onResult: (Boolean) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            onResult(false)
        } else {
            onResult(true)
        }
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, imageUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    // Mở màn hình chia sẻ
    launcher.launch(Intent.createChooser(shareIntent, "Chia sẻ qua"))
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

@OptIn(ExperimentalCoroutinesApi::class)
fun saveImageToCacheFlow(
    context: Context,
    bitmap: Bitmap
): Flow<Uri?> = flow {
    val cachePath = File(context.cacheDir, "images")
    cachePath.mkdirs()
    val file = File(cachePath, "shared_image.png")

    try {
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "com.example.testcapture.fileprovider222",
            file
        )
        emit(uri)
    } catch (e: Exception) {
        e.printStackTrace()
        emit(null)
    }
}


//@Composable
//fun rememberShareLauncherFlow(
//    context: Context
//): Flow<Boolean> {
//    return callbackFlow {
//        val launcher = rememberLauncherForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            trySend(result.resultCode == Activity.RESULT_OK)
//        }
//
//        awaitClose {
//            // Cleanup if necessary
//        }
//    }
//}





