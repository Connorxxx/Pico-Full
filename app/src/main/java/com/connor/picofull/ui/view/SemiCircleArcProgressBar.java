package com.connor.picofull.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.connor.picofull.R;

import java.util.Timer;
import java.util.TimerTask;

public class SemiCircleArcProgressBar extends View
{
    private int padding = 25;

    private int progressPlaceHolderColor;
    private int progressBarColor;
    private int progressPlaceHolderWidth;
    private int progressBarWidth;
    private int percent;

    private int top = 0;
    private int left = 0;
    private int right = 0;
    private int bottom = 0;

    //Constructors
    public SemiCircleArcProgressBar(Context context)
    {
        super(context);
    }

    public SemiCircleArcProgressBar(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        setAttrs(context, attrs);
    }

    public SemiCircleArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setAttrs(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SemiCircleArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttrs(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

        float progressAmount = percent * (float) 1.8;

        canvas.drawArc(getProgressBarRectF(), 180, 180, false, getPaint(progressPlaceHolderColor, progressPlaceHolderWidth));      //arg2: For the starting point, the starting point is 0 degrees from the positive direction of the x coordinate system. How many angles are arg3 selected to rotate clockwise?
        canvas.drawArc(getProgressBarRectF(), 180, progressAmount, false, getPaint(progressBarColor, progressBarWidth));      //arg2: For the starting point, the starting point is 0 degrees from the positive direction of the x coordinate system. How many angles are arg3 selected to rotate clockwise?
    }


    //Private Methods
    private void setAttrs(Context context, @Nullable AttributeSet attrs)
    {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SemiCircleArcProgressBar, 0, 0);
        try
        {
            progressPlaceHolderColor = typedArray.getColor(R.styleable.SemiCircleArcProgressBar_progressPlaceHolderColor, Color.GRAY);
            progressBarColor = typedArray.getColor(R.styleable.SemiCircleArcProgressBar_progressBarColor, Color.WHITE);
            progressPlaceHolderWidth = typedArray.getInt(R.styleable.SemiCircleArcProgressBar_progressPlaceHolderWidth, 25);
            progressBarWidth = typedArray.getInt(R.styleable.SemiCircleArcProgressBar_progressBarWidth, 10);
            percent = typedArray.getInt(R.styleable.SemiCircleArcProgressBar_percent, 76);
        } finally
        {
            typedArray.recycle();
        }
    }

    private Paint getPaint(int color, int strokeWidth)
    {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setDither(true);
        return paint;
    }

    private RectF getProgressBarRectF()
    {
        padding = progressBarWidth > progressPlaceHolderWidth ? progressBarWidth + 5 : progressPlaceHolderWidth + 5;
        top = padding;
        left = padding;
        right = getMeasuredWidth();
        bottom = getMeasuredHeight() * 2;
        Log.d("getProgressBarRectF", "getProgressBarRectF: left: " + left + " top: " + top + " right: " + (right - padding) + " bottom: " + (bottom - (padding * 2)));

        float newR = (right - padding);
        float newB = (bottom - (padding * 2));
        return new RectF(left, top, newR, newB);
    }

    /**
     * 要将半圆矩形改为圆形，可以将矩形的高度和宽度设置为相等的值，然后将矩形的四个角都设置为半径等于宽度一半的圆弧。
     * 这里先计算出圆的直径，然后计算出圆心的坐标，最后用圆心和半径构造一个圆形的 RectF。
     * @return
     */
    private RectF getRoundProgressBarRectF() {
        padding = progressBarWidth > progressPlaceHolderWidth ? progressBarWidth + 5 : progressPlaceHolderWidth + 5;
        float diameter = getMeasuredHeight() - padding * 2; // 直径为矩形高度减去两倍 padding
        float centerX = getMeasuredWidth() / 2f;
        float centerY = getMeasuredHeight() / 2f;
        float radius = diameter / 2f;

        return new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    //Setters
    public void setProgressPlaceHolderColor(int color)
    {
        progressPlaceHolderColor = color;
        postInvalidate();
    }

    public void setProgressBarColor(int color)
    {
        progressBarColor = color;
        postInvalidate();
    }

    public void setProgressPlaceHolderWidth(int width)
    {
        progressPlaceHolderWidth = width;
        postInvalidate();
    }

    public void setProgressBarWidth(int width)
    {
        progressBarWidth = width;
        postInvalidate();
    }

    public void setPercent(int percent)
    {
        this.percent = percent;
        postInvalidate();
    }

    //Custom Setter
    public void setPercentWithAnimation(final int percent)
    {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask()
        {
            int step = 0;

            @Override
            public void run()
            {
                if (step <= percent) setPercent(step++);
            }

        }, 0, 12);
    }
}