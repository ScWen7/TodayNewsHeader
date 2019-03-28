package com.scwen.svgpathproject;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * Created by scwen on 2019/3/28.
 * QQ ：811733738
 * 作用：
 */
public class DotProgressView extends View {

    private Paint mPaint; // 画笔

    private Context mContext;

    private int startColor;  // 渐变 初始颜色

    private int endColor;  //渐变 末尾颜色

    private float fraction = 0.5f; //当前 进度

    private ArgbEvaluator mEvaluator = new ArgbEvaluator();


    private int strokeLineCount = 40;


    private float strokeWidth = DensityUtil.dp2px(3);

    //进度线条高度
    private float lineHeight = DensityUtil.dp2px(40);

    /**
     * 进度线之间的 间隔距离
     * 这个变量与  测量模式 有关：
     * 当 测量模式为 EXACTLY 时，这个变量 根据总宽度计算出来
     * 当测量模式是  AT_MOST 时，这个变量，根据设定值
     */
    private float lineSpace = strokeWidth;

    private float mPaddingTop = DensityUtil.dp2px(10);


    private float trigonLineWidth = DensityUtil.dp2px(10);


    private float trigonSpaceLine = DensityUtil.dp2px(10);


    private Path mPath;

    public DotProgressView(Context context) {
        this(context, null);
    }

    public DotProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DotProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        mContext = context;
        initPaint(strokeWidth, startColor, Paint.Style.STROKE);
        mPath = new Path();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DotProgressView);
        startColor = array.getColor(R.styleable.DotProgressView_start_color, getResources().getColor(R.color.startColor));
        endColor = array.getColor(R.styleable.DotProgressView_end_color, getResources().getColor(R.color.endColor));
        strokeLineCount = array.getInt(R.styleable.DotProgressView_line_count, 40);
        strokeWidth = array.getDimension(R.styleable.DotProgressView_stroke_width, DensityUtil.dp2px(3));
        lineHeight = array.getDimension(R.styleable.DotProgressView_line_height, DensityUtil.dp2px(40));
        lineSpace = array.getDimension(R.styleable.DotProgressView_line_space, strokeWidth);
        trigonLineWidth = array.getDimension(R.styleable.DotProgressView_trigon_lineWidth, DensityUtil.dp2px(10));
        trigonSpaceLine = array.getDimension(R.styleable.DotProgressView_trigon_spaceLine, DensityUtil.dp2px(10));

        mPaddingTop = getPaddingTop();

        array.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {

            if (width <= strokeWidth * strokeLineCount) {
                throw new IllegalArgumentException("can not set current width");
            }
            //计算lineSpace
            lineSpace = (width - getPaddingRight() - getPaddingLeft()) * 1.0f / (strokeLineCount + 1);
        } else {
            width = (int) (strokeLineCount * strokeWidth + (strokeLineCount + 1) * lineSpace);
        }


        int contentHeight = (int) (mPaddingTop + getPaddingBottom() + lineHeight + trigonSpaceLine + trigonLineWidth);
        if (height == MeasureSpec.EXACTLY) {
            if (height < contentHeight) {
                height = contentHeight;
            }
        } else {
            height = contentHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < strokeLineCount; i++) {

            float fraction = i * 1.0f / strokeLineCount;

            mPaint.setColor((int) mEvaluator.evaluate(fraction, startColor, endColor));

            float startX = (i + 1) * lineSpace + i * strokeWidth;

            float stopY = mPaddingTop + lineHeight;

            canvas.drawLine(startX, mPaddingTop, startX, stopY, mPaint);
        }

        //绘制三角形
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        //计算绘制在哪一个线条下面  使用了 floor 函数 可以修改
        int progress = getProgress();

        //计算当前 颜色
        mPaint.setColor((int) mEvaluator.evaluate(fraction, startColor, endColor));

        // 设置 三角形path 位置
        initTrigonPath(progress);

        canvas.drawPath(mPath, mPaint);

    }

    public void reset() {
        mPath.reset();
    }

    /**
     * 根据当前 位置 设置 path 属性
     *
     * @param progress
     */
    private void initTrigonPath(int progress) {
        float trigonStartX = (progress) * lineSpace + (progress - 1) * strokeWidth;

        float trigonStartY = mPaddingTop + lineHeight + trigonSpaceLine;

        double cos = Math.sin(Math.PI / 3);

        float trigonStopY = trigonStartY + (float) cos * trigonLineWidth;

        mPath.moveTo(trigonStartX, trigonStartY);

        mPath.lineTo(trigonStartX - trigonLineWidth / 2f, trigonStopY);

        mPath.lineTo(trigonStartX + trigonLineWidth / 2f, trigonStopY);

        mPath.close();


    }

    private int getProgress() {
        int floor = (int) Math.floor(fraction * strokeLineCount);
        return floor == 0 ? 1 : floor;
    }

    /**
     * 初始化paint
     *
     * @param strokeWidth 画笔宽度
     * @param color       颜色
     * @param style       风格
     * @return paint
     */
    private void initPaint(float strokeWidth, int color, Paint.Style style) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(style);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }
}
