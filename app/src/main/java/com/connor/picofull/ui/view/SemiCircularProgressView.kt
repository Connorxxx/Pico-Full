package com.connor.picofull.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.connor.picofull.R

class SemiCircularProgressView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var progress = 0
    private val path = Path()
    private val progressPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 20f
    }
    private val bgPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 20f
    }
    private val rectF = RectF()

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SemiCircularProgressView, 0, 0)  //
            progress = typedArray.getInteger(R.styleable.SemiCircularProgressView_progress, 0)  //SemiCircularProgressView_progress
            progressPaint.color = typedArray.getColor(R.styleable.SemiCircularProgressView_progressColor, Color.BLUE)  //SemiCircularProgressView_progressColor
            bgPaint.color = typedArray.getColor(R.styleable.SemiCircularProgressView_bgColor, Color.GRAY)  //SemiCircularProgressView_bgColor
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height.toFloat()

        rectF.set(centerX - centerY, 0f, centerX + centerY, centerY * 2)

        // Draw the background arc
        path.reset()
        path.addArc(rectF, 180f, 180f)
        canvas.drawPath(path, bgPaint)

        // Draw the progress arc
        path.reset()
        val sweepAngle = (progress / 100f) * 180f
        path.addArc(rectF, 180f, sweepAngle)
        canvas.drawPath(path, progressPaint)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressPaint.color = color
        invalidate()
    }

    fun setBgColor(color: Int) {
        bgPaint.color = color
        invalidate()
    }
}
