package com.scwen.svgpathproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * 加载view带动画
 */

public class LoadingView extends View {

    private int mWidth;
    private int mHeight;

    private int centerX;
    private int centerY;

    private float radius;

    private Paint bgCirclePaint;
    private Paint arcPaint;
    private Paint okPaint;

    private int paintWidth;

    /**
     * 绘制圆弧区域
     */
    private RectF rectF = new RectF();

    /**
     * 默认圆背景颜色
     */
    private int bgColor = 0xFFe1e5e8;
    /**
     * 圆弧进度条颜色
     */
    private int progressColor = 0xFF353049;

    /**
     * 动画执行时间
     */
    private int duration = 500;
    /**
     * 动画延时启动时间
     */
    private int startDelay = 0;

    /**
     * 圆弧开始偏移角度
     */
    private float startAngle = 270;
    /**
     * 偏移长度
     */
    private float sweepAngle = -90;

    /**
     * 圆弧转圈
     */
    private ValueAnimator animatorDrawLoading;

//    /**
//     *
//     */
//    private ValueAnimator animatorDrawArcToCircle;
    /**
     * 绘制对勾（√）的动画
     */
    private ValueAnimator animatorDrawOk;
    /**
     * 路径--用来获取对勾的路径
     */
    private Path path = new Path();
    /**
     * 取路径的长度
     */
    private PathMeasure pathMeasure;
    /**
     * 对路径处理实现绘制动画效果
     */
    private PathEffect effect;


    /**
     * 是否开始绘制对勾
     */
    private boolean startDrawOk = false;




    /**
     * 点击事件及动画事件2完成回调
     */
    private LoadingViewListener loadingViewListener;

    public void setLoadingViewListener(LoadingViewListener loadingViewListener) {
        this.loadingViewListener = loadingViewListener;
    }


    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paintWidth = dp2px(2);
        initPaint();
        initAnimatorSet();
    }

    private void initPaint() {
        bgCirclePaint = getPaint(paintWidth, bgColor, Paint.Style.STROKE);
        arcPaint = getPaint(paintWidth, progressColor, Paint.Style.STROKE);
        okPaint = getPaint(paintWidth, progressColor, Paint.Style.STROKE);
    }

    /**
     * 统一处理paint
     *
     * @param strokeWidth 画笔宽度
     * @param color       颜色
     * @param style       风格
     * @return paint
     */
    private Paint getPaint(int strokeWidth, int color, Paint.Style style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;

        centerX = w / 2;
        centerY = h / 2;

        radius = Math.min(w, h) / 2 - paintWidth;

        rectF.left = centerX - radius;
        rectF.top = centerY - radius;
        rectF.right = centerX + radius;
        rectF.bottom = centerY + radius;

        initOk();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, bgCirclePaint);
        if (startDrawOk) {
            canvas.drawPath(path, okPaint);
        }else {
            canvas.drawArc(rectF, startAngle, sweepAngle, false, arcPaint);
        }
    }

    /**
     * 绘制对勾
     */
    private void initOk() {
        //对勾的路径
        path.moveTo(mWidth / 8 * 3, mHeight / 2);
        path.lineTo(mWidth / 2, mHeight / 5 * 3);
        path.lineTo(mWidth / 3 * 2, mHeight / 5 * 2);

        pathMeasure = new PathMeasure(path, true);
    }

    /**
     * 初始化弧度转圈的动画
     */
    private void initLoadingAnimation() {
        animatorDrawLoading = ValueAnimator.ofFloat(270, -90);
        animatorDrawLoading.setDuration(duration);
        animatorDrawLoading.setStartDelay(startDelay);
        animatorDrawLoading.setRepeatCount(ObjectAnimator.INFINITE);
        animatorDrawLoading.setInterpolator(new DecelerateInterpolator());
        animatorDrawLoading.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                startAngle = value;
                invalidate();
            }
        });
    }


    /**
     * 绘制对勾的动画
     */
    private void initOkAnimation() {
        animatorDrawOk = ValueAnimator.ofFloat(1, 0);
        animatorDrawOk.setDuration(duration);
        animatorDrawOk.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startDrawOk = true;
                float value = (Float) animation.getAnimatedValue();

                effect = new DashPathEffect(new float[]{pathMeasure.getLength(), pathMeasure.getLength()}, value * pathMeasure.getLength());
                okPaint.setPathEffect(effect);
                invalidate();
            }
        });

    }

    /**
     * 初始化所有动画
     */
    private void initAnimatorSet() {
        initLoadingAnimation();
        initOkAnimation();

    }

    /**
     * 传入一个进度值，从0到progress动画变化
     */
    public LoadingView startLoadAnim() {
        startDrawOk = false;
        startAngle = 270;
        sweepAngle = -90;
        animatorDrawLoading.start();
        return this;
    }



    public   void startOKAnim(){
        animatorDrawLoading.cancel();
        path = new Path();
        initOk();
        animatorDrawOk.start();
    }


    /**
     * 接口回调
     */
    public interface LoadingViewListener {
        /**
         * 动画完成回调
         */
        void animationFinish();
    }


    public int getDuration() {
        return duration;
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
