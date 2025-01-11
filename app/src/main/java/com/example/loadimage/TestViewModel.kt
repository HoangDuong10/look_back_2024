package com.example.loadimage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class TestViewModel: ViewModel() {

    private val _uriFlow = MutableStateFlow<Uri?>(null)
    val uriFlow: StateFlow<Uri?> = _uriFlow
    val scope = viewModelScope
    fun saveImageToSpecialApp(
        bitmap: Bitmap,
        context: Context,
    ): Flow<Uri> {
        return flow {
            val uri = saveImageToCache(context, bitmap)
            if (uri != null) {
                emit(uri)
            } else {
                // Nếu không thể lưu ảnh, phát tín hiệu lỗi hoặc trả về null
                throw Exception("Không thể lưu ảnh vào bộ nhớ cache.")
            }
        }


    }

//    @Composable
//    fun shareImageWithResult(
//        context: Context,
//        imageUri: Uri,
//        onResult: (Boolean) -> Unit
//    ) {
//        val launcher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == Activity.RESULT_CANCELED) {
//                onResult(false)
//            } else {
//                onResult(true)
//            }
//        }
//
//        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//            type = "image/png"
//            putExtra(Intent.EXTRA_STREAM, imageUri)
//            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//        }
//
//        // Mở màn hình chia sẻ
//        launcher.launch(Intent.createChooser(shareIntent, "Chia sẻ qua"))
//    }
}