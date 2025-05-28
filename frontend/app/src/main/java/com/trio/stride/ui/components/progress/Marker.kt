package com.trio.stride.ui.components.progress

import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.trio.stride.domain.model.Progress
import java.text.DecimalFormat

private val YDecimalFormat = DecimalFormat("0.00")
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

fun markerFormatter(
    yFormatter: CartesianValueFormatter,
    items: List<Progress>,
    yList: List<Number>
) =
    DefaultCartesianMarker.ValueFormatter { context, targets ->
        val spannable = SpannableStringBuilder()

        for ((index, target) in targets.withIndex()) {
            val x = target.x
            val yFormatted = yFormatter.format(context, yList[x.toInt()].toDouble(), null)

            val yStyled = SpannableString("$yFormatted")
            yStyled.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                yStyled.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            yStyled.setSpan(
                RelativeSizeSpan(1.2f),
                0,
                yStyled.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.append(yStyled)
            spannable.append("\n")

            val xFormatted = formatWeekRange(
                items[x.toInt()].fromDate,
                items[x.toInt()].toDate,
            )
            spannable.append(xFormatted)

            if (index < targets.lastIndex) {
                spannable.append("\n\n")
            }
        }

        return@ValueFormatter spannable
    }
