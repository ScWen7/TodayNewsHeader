package com.scwen.svgpathproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

public class MainActivity extends AppCompatActivity {

    private NewRefreshView refresh_view;

    private float fraction = 0.1f;

    private SmartRefreshLayout refresh;

    private LoadingView  loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        refresh = findViewById(R.id.refresh);
//        loading = findViewById(R.id.loading);
//        refresh_view = findViewById(R.id.refresh_view);
//        Button btn = findViewById(R.id.btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fraction += 0.1f;
//                if (fraction > 1) {
//                    refresh_view.setDragState();
//                } else {
//                    refresh_view.setFraction(fraction);
//                }
//            }
//        });

//        refresh.setRefreshHeader(new MyCustomerHeader(this));
//
//        refresh.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshLayout) {
//                refresh.finishRefresh(4000);
//            }
//        });

//        loading.startLoadAnim();


    }
}
