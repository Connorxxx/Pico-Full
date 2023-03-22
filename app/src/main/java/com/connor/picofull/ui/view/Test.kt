package com.connor.picofull.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Test @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 30
    private var text = ""
    private var textX = 0f
    private var scaleCount = 0


    private val paint = Paint().apply {
        color = Color.BLACK
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

    private val scalePaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        isDither = true
    }

    private val bgScalePaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        isDither = true
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val rectF = RectF()
    private var radius = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) * 0.4f
        textX = w / 2f + 10
        rectF.set(-radius, -radius, radius, radius)
        rectF.offset(w / 2f, h / 2f)
    }

    override fun onDraw(canvas: Canvas?) {
        val p = progress * 2.1f
        textPaint.textSize = 28f
        textPaint.color = Color.GRAY
        canvas?.drawText(text, textX, 30f, textPaint)
        canvas?.drawArc(rectF, 165f, 210f, false, bgPaint)
        canvas?.drawArc(rectF, 165f, p, false, paint)

        val centerX = width / 2f
        val centerY = height / 2f
        val tickLength = 40f // length of each tick
        val tickStartRadius = radius - 30f // radius of tick start
        val tickEndRadius = radius - tickLength // radius of tick end
        val tickAngle = 210f / 20f // angle between each tick
        val tickStartAngle = 165f + tickAngle / 2f // starting angle of ticks

        for (i in 0..19) {
            val angle = tickStartAngle + i * tickAngle
            val startX = centerX + tickStartRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val startY = centerY + tickStartRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
            val endX = centerX + tickEndRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = centerY + tickEndRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
            canvas?.drawLine(startX, startY, endX, endY, bgScalePaint)
        }

        for (i in 0..scaleCount) {
            val angle = tickStartAngle + i * tickAngle
            val startX = centerX + tickStartRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val startY = centerY + tickStartRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
            val endX = centerX + tickEndRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = centerY + tickEndRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
            canvas?.drawLine(startX, startY, endX, endY, scalePaint)
        }
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setScale(value: Int) {
        scaleCount = if (value >= 20) 19
        else if (value <= 1) 1
        else value
        invalidate()
    }

    fun setText(text: String) {
        this.text = text
        invalidate()
    }
}