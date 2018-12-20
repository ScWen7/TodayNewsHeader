package com.scwen.svgpathproject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by scwen on 2018/8/8.
 * QQ ：811733738
 * 作用：
 */

public class SvgHeader extends LinearLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉推荐";
    public static String REFRESH_HEADER_REFRESHING = "推荐中...";
    public static String REFRESH_HEADER_RELEASE = "松开推荐";
    private TextView releaseText;
    private AnimatedSvgView mAnimatedSvgView;


    public SvgHeader(Context context) {
        this(context, null);
    }

    public SvgHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SvgHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.setGravity(Gravity.CENTER_HORIZONTAL);
        this.setOrientation(LinearLayout.VERTICAL);

//        mNewRefreshView = new NewRefreshView(context);


        mAnimatedSvgView = new AnimatedSvgView(context);

        SVG svg = SVG.GOOGLE;
        mAnimatedSvgView.setGlyphStrings(svg.glyphs);
        mAnimatedSvgView.setFillColors(svg.colors);
        mAnimatedSvgView.setViewportSize(svg.width, svg.height);
        mAnimatedSvgView.setTraceResidueColor(0x32000000);
        mAnimatedSvgView.setTraceColors(svg.colors);
        mAnimatedSvgView.rebuildGlyphData();
        mAnimatedSvgView.setState(AnimatedSvgView.STATE_TRACE_STARTED);


        LayoutParams lpNewRefresh = new LayoutParams(dip2px(context, 80), dip2px(context, 80));
        lpNewRefresh.setMargins(30, dip2px(context, 30), 30, 0);

        this.addView(mAnimatedSvgView, lpNewRefresh);


        LayoutParams lpReleaseText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpReleaseText.setMargins(0, 30, 0, 30);

        releaseText = new TextView(context);
        releaseText.setText(REFRESH_HEADER_PULLDOWN);
        releaseText.setTextColor(0xff666666);
        addView(releaseText, lpReleaseText);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            mNewRefreshView.setDragState();

//            mHandler.sendEmptyMessageDelayed(0, 100);
        }
    };

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onPulling(float percent, int offset, int height, int extendHeight) {
        Log.e("TAG", "fraction:" + percent);
//        mNewRefreshView.setFraction((percent - 0.8f) * 6f);
        mAnimatedSvgView.setPhase((percent - 0.8f) * 6f);
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        Log.e("TAG", "onReleasing：");
        onPulling(percent, offset, height, extendHeight);
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
//        mHandler.removeCallbacksAndMessages(null);
//        mHandler.sendEmptyMessage(0);
        mAnimatedSvgView.startBlink();
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        Log.e("TAG", "onStartAnimator：");
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mHandler.removeCallbacksAndMessages(null);
        mAnimatedSvgView.reset();
        return 0;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                break;
            case PullDownToRefresh:
                releaseText.setText(REFRESH_HEADER_PULLDOWN);
                break;
            case PullUpToLoad:
                break;
            case ReleaseToRefresh:
                releaseText.setText(REFRESH_HEADER_RELEASE);
                break;
            case Refreshing:
                releaseText.setText(REFRESH_HEADER_REFRESHING);
                break;
            case Loading:
                break;
        }
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
