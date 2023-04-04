package com.connor.picofull.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.withTranslation
import com.connor.picofull.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class Test @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0
    private var scaleCount = 0
    private val rectF = RectF()
    private var radius = 0f

    private lateinit var s: ScaleData

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        isDither = true
    }

    private val cPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.deep_orange_primary)
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        isDither = true
       // paint.setShadowLayer(18f, 15f, 15f, Color.GRAY)
    }

    private val fcPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
        strokeWidth = 8f
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


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) * 0.48f
        rectF.set(-radius, -radius, radius, radius)
        rectF.offset(w / 2f, h / 2f)

        val startX = 0f
        val startY = height / 2f
        val endX = width.toFloat()
        val shader = LinearGradient(
            startX, startY, endX, startY,
            intArrayOf(
                ContextCompat.getColor(context, R.color.deep_orange_primary_dark),
                ContextCompat.getColor(context, R.color.amber_primary)
            ),
            null, Shader.TileMode.CLAMP
        )
        scalePaint.shader = shader
        paint.shader = shader

        val centerX = width / 2f
        val centerY = height / 2f
        val tickLength = 40f // length of each tick
        val tickStartRadius = radius - 20f // radius of tick start
        val tickEndRadius = radius - tickLength // radius of tick end
        val tickAngle = 210f / 40f // angle between each tick
        val tickStartAngle = 165f + tickAngle / 2f // starting angle of ticks

        s = ScaleData(
            centerX,
            centerY,
            tickLength,
            tickStartRadius,
            tickEndRadius,
            tickAngle,
            tickStartAngle
        )
    }

    override fun onDraw(canvas: Canvas?) {
        val p = progress * 2.1f

        val endAngle = 165f + p
        val endX = width / 2f + radius * cos(endAngle * PI / 180f).toFloat()
        val endY = height / 2f + radius * sin(endAngle * PI / 180f).toFloat()

        canvas?.apply {

            drawArc(rectF, 165f, 210f, false, bgPaint)
            drawArc(rectF, 165f, p, false, paint)
            drawCircle(endX, endY, 12f, fcPaint)
            drawCircle(endX, endY, 12f, cPaint)
            drawScale(39, bgScalePaint)
            if (scaleCount >= 2) drawScale(scaleCount, scalePaint)
        }
    }

    private fun Canvas.drawScale(count: Int, paint: Paint) {
        for (i in 0..count) {
            val angle = s.tickStartAngle + i * s.tickAngle
            val startX =
                s.centerX + s.tickStartRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val startY =
                s.centerY + s.tickStartRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
            val endX = s.centerX + s.tickEndRadius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val endY = s.centerY + s.tickEndRadius * sin(Math.toRadians(angle.toDouble())).toFloat()
            this.drawLine(startX, startY, endX, endY, paint)
        }
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setScale(value: Int) {
        scaleCount = if (value >= 40) 39
        else if (value <= 1) 1
        else value
        invalidate()
    }
}

data class ScaleData(
    val centerX: Float = 0f,
    val centerY: Float = 0f,
    val tickLength: Float = 0f,
    val tickStartRadius: Float = 0f,
    val tickEndRadius: Float = 0f,
    val tickAngle: Float = 0f,
    val tickStartAngle: Float = 0f
)