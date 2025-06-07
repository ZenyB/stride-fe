package com.trio.stride.ui.components.notification

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.trio.stride.R
import com.trio.stride.ui.theme.StrideTheme

@Composable
fun HtmlTextView(
    htmlText: String,
    fontResId: Int = R.font.roboto_regular,
    color: Color = StrideTheme.colorScheme.onSurface,
    textSz: Float = 12f
) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            typeface = ResourcesCompat.getFont(context, fontResId)
            text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_LEGACY)
            textSize = textSz
            setTextColor(color.toArgb())
        }
    })
}
