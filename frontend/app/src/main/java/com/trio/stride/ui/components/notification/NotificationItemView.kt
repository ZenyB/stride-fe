package com.trio.stride.ui.components.notification

import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import com.trio.stride.domain.model.NotificationItem
import com.trio.stride.domain.model.UserInfo
import com.trio.stride.ui.components.Avatar
import com.trio.stride.ui.theme.StrideTheme
import com.trio.stride.ui.utils.getStartOfWeekInMillis
import com.trio.stride.ui.utils.minusNWeeks
import com.trio.stride.ui.utils.toTimeAgo

@Composable
fun NotificationItemView(
    user: UserInfo,
    notification: NotificationItem,
    modifier: Modifier = Modifier,
    onItemClick: ((String) -> Unit)? = null,
) {
    var isBodyOverflowing by remember { mutableStateOf(false) }
    var isBodyExpand by remember { mutableStateOf(false) }
    val containerColor =
        if (notification.seen) StrideTheme.colors.transparent else StrideTheme.colorScheme.primary.copy(
            alpha = 0.2f
        )
    val plainText = parseHtmlToAnnotatedString(notification.body.trimIndent())

    Box(
        modifier = modifier
            .background(color = containerColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple()
            ) {
                isBodyExpand = !isBodyExpand
                onItemClick?.let { it(notification.id) }
            }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Avatar(
                ava = user.ava,
                name = user.name,
                width = 48.dp
            )
            Spacer(Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    notification.title,
                    maxLines = 1,
                    style = StrideTheme.typography.titleMedium,
                    color = StrideTheme.colorScheme.onBackground,
                )

                Text(
                    plainText,
                    maxLines = if (isBodyExpand) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    style = StrideTheme.typography.labelLarge,
                    color = StrideTheme.colorScheme.onBackground,
                    onTextLayout = { textLayoutResult ->
                        val didOverflow = textLayoutResult.hasVisualOverflow
                        if (didOverflow) {
                            isBodyOverflowing = true
                        }
                    },
                    modifier = Modifier.animateContentSize()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        notification.time.toTimeAgo(),
                        maxLines = 1,
                        style = StrideTheme.typography.labelMedium,
                        color = StrideTheme.colors.gray
                    )
                    if (isBodyOverflowing) {
                        Text(
                            if (isBodyExpand) "Show less" else "See more",
                            maxLines = 1,
                            style = StrideTheme.typography.labelMedium,
                            color = StrideTheme.colors.placeHolderText
                        )
                    }
                }
            }
        }
    }
}

private fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    return buildAnnotatedString {
        val text = spanned.toString()
        append(text)

        spanned.getSpans(0, text.length, CharacterStyle::class.java).forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)

            when (span) {
                is StyleSpan -> {
                    when (span.style) {
                        Typeface.BOLD -> addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            start,
                            end
                        )

                        Typeface.ITALIC -> addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            start,
                            end
                        )
                    }
                }

                is UnderlineSpan -> {
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun preview() {
    NotificationItemView(
        user = UserInfo(
            name = "Rei",
        ),
        notification = NotificationItem(
            title = "Kudos to you, Rei",
            body = "Nice work logging an activity. Check out all your stats, Nice work logging an activity. Check out all your stats.",
            time = getStartOfWeekInMillis().minusNWeeks(3)
        )
    )
}