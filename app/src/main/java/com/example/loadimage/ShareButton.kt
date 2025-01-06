package com.example.loadimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.loadimage.ui.theme.LoadImageTheme

@Composable
fun ShareButton(
    colorBg : Color = colorResource(R.color.main_color),
    colorText : Color = Color.White,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onClick.invoke()
            }
            .background(
                color = colorBg,
                shape = (RoundedCornerShape(22.dp)
                        )
            )
            .padding(vertical = 6.dp, horizontal = 20.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_share),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(28.dp),
            tint = colorText
        )
        Text(
            text = "Chia sẻ",
            fontSize = 22.sp,
            color = colorText
        )
    }
}
@Composable
fun LoadingDialog(isLoading: Boolean) {
    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.Red, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonPreview() {
    LoadImageTheme {
        ShareButton {  }
    }
}