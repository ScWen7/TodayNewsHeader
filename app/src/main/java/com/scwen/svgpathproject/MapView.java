package com.scwen.svgpathproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by scwen on 2018/8/6.
 * QQ ：811733738
 * 作用：
 */

public class MapView extends View {


    private Paint mPaint;

    private Context mContext;

    private List<ProviceItem> mProviceItems;

    private RectF totalRectF;


    private int mWidth, mHeight;

    private float scale = -1f;

    private int[] colorArray = new int[]{0xFF239BD7, 0xFF30A9E5, 0xFF80CBF1, 0xFFFFFFFF};

    private ProviceItem select;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mloadThread.start();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        获取到当前控件宽高值
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = width;
        mHeight = height;

        handleScale(mWidth);
//

        setMeasuredDimension(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
    }


    /**
     * 计算拉伸比例
     *
     * @param width
     */
    public void handleScale(int width) {
        if (scale == -1f) {
            if (totalRectF != null) {
                double mapWidth = totalRectF.width();
                scale = (float) (width / mapWidth);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (mProviceItems == null) {
            return;
        }
        handleScale(mWidth);

        canvas.save();
        canvas.scale(scale, scale);


        for (ProviceItem proviceItem : mProviceItems) {
            if (proviceItem != select) {
                proviceItem.onDraw(canvas, mPaint, false);
            }
        }
        if (select != null) {
            select.onDraw(canvas, mPaint, true);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleTouch(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        if (mProviceItems == null) {
            return;
        }
        ProviceItem preItem = null;
        for (ProviceItem proviceItem : mProviceItems) {
            if (proviceItem.isTouch(x / scale, y / scale)) {
                preItem = proviceItem;
            }
        }
        if (preItem != null) {
            select = preItem;
            postInvalidate();
        }

    }

//    @Override
//    protected void onDetachedFromWindow() {
//        try {
//            if (mloadThread.isAlive()) {
//                mloadThread.interrupt();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        super.onDetachedFromWindow();
//    }

    private Thread mloadThread = new Thread() {

        @Override
        public void run() {
            InputStream inputStream = mContext.getResources().openRawResource(R.raw.china);

            List<ProviceItem> proviceItems = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                Element element = document.getDocumentElement();

                NodeList items = element.getElementsByTagName("path");

                float left = -1f;
                float top = -1f;
                float right = -1f;
                float bottom = -1f;

                for (int i = 0; i < items.getLength(); i++) {

                    Element element1 = (Element) items.item(i);

                    String pathData = element1.getAttribute("android:pathData");

                    String title = element1.getAttribute("android:provice");


                    Path path = PathParser.createPathFromPathData(pathData);

                    RectF rectF = new RectF();

                    path.computeBounds(rectF, true);

                    left = left == -1 ? rectF.left : Math.min(left, rectF.left);
                    top = left == -1 ? rectF.top : Math.min(top, rectF.top);
                    right = left == -1 ? rectF.left : Math.max(right, rectF.right);
                    bottom = left == -1 ? rectF.left : Math.max(bottom, rectF.bottom);

                    ProviceItem proviceItem = new ProviceItem();

                    proviceItem.setPath(path);

                    proviceItem.setShowStr(title);

                    proviceItems.add(proviceItem);

                }
                totalRectF = new RectF(left, top, right, bottom);

                mProviceItems = proviceItems;

                postInvalidate();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
