package com.scwen.svgpathproject;

import android.content.Context;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by scwen on 2018/8/8.
 * QQ ：811733738
 * 作用：
 */

public class MyCustomerHeader extends RelativeLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉即可刷新...";
    public static String REFRESH_HEADER_REFRESHING = "推荐中...";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";

    private LoadingView mLoadingView;
    private TextView releaseText;


    public MyCustomerHeader(Context context) {
        this(context, null);
    }

    public MyCustomerHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyCustomerHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {

        RelativeLayout relativeLayout = new RelativeLayout(context);


        mLoadingView = new LoadingView(context);

        LayoutParams lpNewRefresh = new LayoutParams(DensityUtil.dp2px(40), DensityUtil.dp2px(40));
        lpNewRefresh.addRule(CENTER_IN_PARENT);
        lpNewRefresh.bottomMargin = DensityUtil.dp2px(20);
        mLoadingView.setVisibility(VISIBLE);
        relativeLayout.addView(mLoadingView, lpNewRefresh);

        LayoutParams lpReleaseText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpReleaseText.addRule(ALIGN_PARENT_BOTTOM);
        lpReleaseText.addRule(CENTER_HORIZONTAL);
        lpReleaseText.bottomMargin = DensityUtil.dp2px(8);
        releaseText = new TextView(context);
        releaseText.setText(REFRESH_HEADER_PULLDOWN);
        releaseText.setTextColor(0xff666666);
        relativeLayout.addView(releaseText, lpReleaseText);


        RelativeLayout.LayoutParams layoutParams = new LayoutParams(MATCH_PARENT,DensityUtil.dp2px(60));

        addView(relativeLayout,layoutParams);

    }




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
//        mLoadingView.setFraction((percent - 0.8f) * 6f);
    }

    @Override
    public void onReleasing(float percent, int offset, int height, int extendHeight) {
        onPulling(percent, offset, height, extendHeight);
    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int extendHeight) {
//        mHandler.removeCallbacksAndMessages(null);
//        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int extendHeight) {
        mLoadingView.startLoadAnim();
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        mHandler.removeCallbacksAndMessages(null);
        mLoadingView.startOKAnim();
        return mLoadingView.getDuration();
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
                releaseText.setVisibility(VISIBLE);
                releaseText.setText(REFRESH_HEADER_PULLDOWN);
                mLoadingView.setVisibility(INVISIBLE);
                break;
            case PullUpToLoad:
                break;
            case ReleaseToRefresh:
                releaseText.setVisibility(VISIBLE);
                releaseText.setText(REFRESH_HEADER_RELEASE);
                mLoadingView.setVisibility(INVISIBLE);
                break;
            case Refreshing:
                releaseText.setVisibility(GONE);
                releaseText.setText(REFRESH_HEADER_REFRESHING);
                mLoadingView.setVisibility(VISIBLE);
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
    }
}
