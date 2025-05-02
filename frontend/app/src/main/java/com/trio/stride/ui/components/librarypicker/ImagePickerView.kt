package com.trio.stride.ui.components.librarypicker

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun ImagePickerView(
    modifier: Modifier = Modifier,
    onImageSelected: (Uri) -> Unit
) {
    val primaryColor = StrideTheme.colorScheme.primary
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                context.contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                onImageSelected(it)
            }
        }
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                brush = SolidColor(StrideTheme.colorScheme.primary),
                shape = RoundedCornerShape(6.dp),
            )
            .drawBehind {
                val strokeF = 4f
                val dashLength = 10.dp.toPx()
                val gapLength = 6.dp.toPx()
                val paint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    color = primaryColor.toArgb()
                    strokeWidth = strokeF
                    pathEffect =
                        android.graphics.DashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
                }
                drawContext.canvas.nativeCanvas.drawRoundRect(
                    0f,
                    0f,
                    size.width,
                    size.height,
                    6.dp.toPx(),
                    6.dp.toPx(),
                    paint
                )
            }
            .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
            .padding(24.dp),
        Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.image_icon),
                contentDescription = "Add Photo",
                tint = StrideTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Add Photos/Videos",
                color = StrideTheme.colorScheme.primary,
                style = StrideTheme.typography.bodyMedium
            )
        }
    }
}