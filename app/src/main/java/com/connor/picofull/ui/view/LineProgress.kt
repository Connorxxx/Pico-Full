package com.connor.picofull.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.withTranslation
import com.connor.picofull.R

class LineProgress@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.deep_orange_primary)
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        isDither = true
    }

    private val bgPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        isDither = true
    }

    var progress: Float = 0.7f
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas?) {

        canvas?.apply {
            withTranslation(width / 2f, 0f) {
                // draw background line
                drawLine(0f, 0f, width.toFloat(), 0f, bgPaint)

                // draw progress line
                drawLine(0f, 0f, progress * width, 0f, paint)
            }

        }
    }
}