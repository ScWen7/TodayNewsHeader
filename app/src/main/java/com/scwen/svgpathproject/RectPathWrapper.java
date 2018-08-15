package com.scwen.svgpathproject;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;


/**
 * Created by scwen on 2018/8/7.
 * QQ ：811733738
 * 作用：
 */

public class RectPathWrapper extends PathWrapper {

    Paint mPaint;

    public RectPathWrapper(Path path, float fraction) {
        super(path, fraction);
         mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0x32000000);
    }


    public void onDraw(Canvas canvas, Paint paint) {
        if (fraction <= 0) {
            return;
        }

        Path dst = new Path();
        PathMeasure measure = new PathMeasure(mPath, false);         // 将 Path 与 PathMeasure 关联

        float length = measure.getLength();

        measure.getSegment(0, length * fraction, dst, true);                   // 截取一部分 并使用 moveTo 保持截取得到的 Path 第一个点的位置不变

        canvas.drawPath(dst, paint);

        canvas.drawPath(dst, mPaint);


    }
}
