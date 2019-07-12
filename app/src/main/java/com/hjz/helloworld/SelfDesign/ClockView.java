package com.hjz.helloworld.SelfDesign;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.hjz.helloworld.R;

import java.util.Calendar;
import java.util.Locale;

import static java.lang.Math.min;


public class ClockView extends View {

    private float secondNeedleWidth;
    private float minuteNeedleWidth;
    private float hourNeedleWidth;
    private float padding;
    private int RoundWidth;
    private float ticKLength;
    private float mPointRadius;

    private int mainTickColor;
    private int tickColor;
    private int secondNeedleColor;
    private int minuteNeedleColor;
    private int hourNeedleColor;
    private float mRadius;
    private Paint mpaint;
    private boolean isClockShape;


    public ClockView(Context context) {
        super(context);
        init(context, null);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private void init(Context context, AttributeSet attrs){

        isClockShape = true;
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.ClockView);
        //属性的设置与获取
        secondNeedleWidth = typedArray.getDimension(R.styleable.ClockView_secondNeedleWidth,dp2px(context,2));
        minuteNeedleWidth = typedArray.getDimension(R.styleable.ClockView_minuteNeedleWidth,dp2px(context,3));
        hourNeedleWidth = typedArray.getDimension(R.styleable.ClockView_hourNeedleWidth,dp2px(context,5));
        secondNeedleColor = typedArray.getColor(R.styleable.ClockView_secondNeedleColor,Color.WHITE);
        minuteNeedleColor = typedArray.getColor(R.styleable.ClockView_minuteNeedleColor,Color.WHITE);
        hourNeedleColor = typedArray.getColor(R.styleable.ClockView_hourNeedleColor,Color.WHITE);
        padding = typedArray.getDimension(R.styleable.ClockView_circlePadding,dp2px(context,10));
        tickColor = typedArray.getColor(R.styleable.ClockView_tickColor,Color.LTGRAY);
        mainTickColor = typedArray.getColor(R.styleable.ClockView_mainTickColor, Color.WHITE);
        mPointRadius = typedArray.getDimension(R.styleable.ClockView_mPointRadius,dp2px(context,10));
        //注意资源的回收
        typedArray.recycle();

        //画笔的设置
        mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);

        //初始化控件大小
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels;
        RoundWidth = min(width,height);
    }
    //默认设置成一个正方形的组件
    @Override
    protected void onMeasure(int wideMeasureSpec, int heightMeasureSpec){
        super.onMeasure(wideMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(measureSize(wideMeasureSpec),measureSize(heightMeasureSpec));
    }
    //不管是什么模式下我们都采用最小的width
    private int measureSize(int measureSpec){
        int size = MeasureSpec.getSize(measureSpec);
        RoundWidth = min(size,RoundWidth);
        return RoundWidth;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(isClockShape){
            drawTick(canvas);
            drawNeedle(canvas);

        }else{
            drawNumber(canvas);
        }
        postInvalidateDelayed(1000);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w, h) - padding) / 2;
    }

    private void drawTick(Canvas canvas){
        int linesize;
        for(int i=0;i<60;++i){
            if(i%5==0){
                //绘制刻度
                mpaint.setStrokeWidth(dp2px(getContext(),1.5f));
                mpaint.setColor(mainTickColor);
                linesize = 40;
                //绘制文字
                String text = ((i / 5) == 0 ? 12 : (i / 5)) + "";
                mpaint.setTextSize(sp2px(getContext(),16));
                Rect rect = new Rect();
                mpaint.getTextBounds(text,0,text.length(),rect);
                mpaint.setColor(Color.WHITE);
                //canvas.rotate(-6*i,RoundWidth/2,RoundWidth/2);
                canvas.drawText(text,RoundWidth/2-rect.width()/2,rect.height()+dp2px(getContext(),5)+padding+linesize,mpaint);
                //canvas.rotate(6*i,RoundWidth/2,RoundWidth/2);
            }else{
                mpaint.setStrokeWidth(dp2px(getContext(),1.0f));
                mpaint.setColor(tickColor);
                linesize = 40;
            }
            canvas.drawLine(RoundWidth/2,padding,RoundWidth/2,padding+linesize,mpaint);
            canvas.rotate(6,RoundWidth/2,RoundWidth/2);
        }
    }

    private void drawNeedle(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);// 时
        int minute = calendar.get(Calendar.MINUTE);// 分
        int second = calendar.get(Calendar.SECOND);// 秒
        // 转过的角度
        float angleHour = (hour + (float) minute / 60) * 360 / 12;
        float angleMinute = (minute + (float) second / 60) * 360 / 60;
        int angleSecond = second * 360 / 60;

        // 绘制时针
        canvas.save();
        canvas.rotate(angleHour,RoundWidth/2,RoundWidth/2); // 旋转到时针的角度
        RectF rectHour = new RectF(RoundWidth/2 -hourNeedleWidth / 2, RoundWidth/2 -mRadius * 3 / 5, RoundWidth/2 +hourNeedleWidth / 2, RoundWidth/2);
        mpaint.setColor(hourNeedleColor);
        mpaint.setStyle(Paint.Style.STROKE);
        mpaint.setStrokeWidth(hourNeedleWidth);
        canvas.drawRoundRect(rectHour, mPointRadius, mPointRadius, mpaint);
        canvas.restore();
        // 绘制分针
        canvas.save();
        canvas.rotate(angleMinute,RoundWidth/2,RoundWidth/2); // 旋转到分针的角度
        RectF rectMinute = new RectF(RoundWidth/2-minuteNeedleWidth / 2, RoundWidth/2-mRadius * 3.4f / 5, RoundWidth/2+minuteNeedleWidth/ 2, RoundWidth/2);
        mpaint.setColor(minuteNeedleColor);
        mpaint.setStrokeWidth(minuteNeedleWidth);
        canvas.drawRoundRect(rectMinute, mPointRadius, mPointRadius, mpaint);
        canvas.restore();
        // 绘制秒针
        canvas.save();
        canvas.rotate(angleSecond,RoundWidth/2,RoundWidth/2); // 旋转到分针的角度
        RectF rectSecond = new RectF(RoundWidth/2-secondNeedleWidth / 2, RoundWidth/2-mRadius * 3.9f/5, RoundWidth/2+secondNeedleWidth / 2, RoundWidth/2);
        mpaint.setStrokeWidth(secondNeedleWidth);
        mpaint.setColor(secondNeedleColor);
        canvas.drawRoundRect(rectSecond, mPointRadius, mPointRadius, mpaint);
        canvas.restore();

        // 绘制原点
        mpaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(RoundWidth/2, RoundWidth/2, secondNeedleWidth * 4, mpaint);
    }

    private void drawNumber(Canvas canvas){
        Rect rect = new Rect();
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);// 时
        int minute = calendar.get(Calendar.MINUTE);// 分
        int second = calendar.get(Calendar.SECOND);// 秒
        String text = hour + " : " +minute + " : " + second;
        mpaint.setTextSize(sp2px(getContext(),30));
        mpaint.getTextBounds(text,0,text.length(),rect);
        mpaint.setColor(Color.WHITE);

        canvas.drawText(text,RoundWidth/2-rect.width()/2,rect.height()/2+RoundWidth/2,mpaint);
    }

    public void reverse(){
        isClockShape = !isClockShape;
    }

}
