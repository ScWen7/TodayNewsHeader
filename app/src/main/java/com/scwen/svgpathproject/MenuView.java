package com.scwen.svgpathproject;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by scwen on 2018/8/6.
 * QQ ：811733738
 * 作用：
 */

public class MenuView extends View{
    public MenuView(Context context) {
        this(context,null);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVisibility(VISIBLE);
    }



}
