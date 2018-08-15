package com.scwen.svgpathproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;


/**
 * Created by scwen on 2018/8/6.
 * QQ ：811733738
 * 作用：
 */

public class ProviceItem {
    private Path mPath;

    private int color;


    private String showStr;


    public String getShowStr() {
        return showStr == null ? "" : showStr;
    }

    public void setShowStr(String showStr) {
        this.showStr = showStr;
    }



    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }


    /**
     * 绘制 provice
     * @param canvas
     */
    public  void onDraw(Canvas canvas, Paint paint,boolean isSelect){
        if(isSelect){
//            //选中时，绘制描边效果
//            paint.clearShadowLayer();
//            paint.setStrokeWidth(1);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(drawColor);
//            canvas.drawPath(path, paint);
//
//            paint.setStyle(Paint.Style.STROKE);
//            int strokeColor = 0xFFD0E8F4;
//            paint.setColor(strokeColor);
//            canvas.drawPath(path, paint);
//
            paint.clearShadowLayer();
            paint.setStrokeWidth(2);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(15,5,5,0xff5d5d5d);
            canvas.drawPath(mPath,paint);
//后面是填充
            paint.clearShadowLayer();
            paint.setColor(0xFF239BD7);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(mPath, paint);

        }else {
//        设置边界
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setShadowLayer(8,0,0,0xffffff);
            canvas.drawPath(mPath,paint);
//后面是填充
            paint.clearShadowLayer();
            paint.setColor(0xFF239BD7);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(2);
            canvas.drawPath(mPath, paint);
        }

    }


    /**
     * 是否是点击 范围
     * @param x
     * @param y
     * @return
     */
    public  boolean isTouch(float x,float y){
        RectF rectF = new RectF();
        mPath.computeBounds(rectF, true);
//        rectF   矩形  包含了Path
        Region region = new Region();
        region.setPath(mPath, new Region((int)rectF.left, (int)rectF.top,(int)rectF.right, (int)rectF.bottom));
        return region.contains((int)x,(int)y);
    }
}
