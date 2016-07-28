package com.zgh.locationdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.zgh.locationlib.LocationUtil;

public class MainActivity extends Activity implements LocationUtil.LocationListener {
    MapView mMapView = null;
    LinearLayout ll_load, ll_retry;
    private BaiduMap mBaiduMap = null;
    private boolean isFirstLoc = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        ll_load = (LinearLayout) findViewById(R.id.ll_load);
        ll_retry = (LinearLayout) findViewById(R.id.ll_retry);
        ll_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        //打开测试，用于测试状态切换是否正常
        LocationUtil.test=true;
        getLocation();
    }

    int retryTimes=0;
    private void getLocation() {
        //重试两次，成功
        retryTimes++;
        if(retryTimes==2){
            LocationUtil.test=false;
        }
        LocationUtil.getLocation(this, 3000, false, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationUtil.stopLoacation();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }


    @Override
    public void onGetLocationStart() {
        hideAll();
        ll_load.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        hideAll();
        mMapView.setVisibility(View.VISIBLE);
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(15.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }

    }

    @Override
    public void onGetLocationTimeOut() {
        hideAll();
        ll_retry.setVisibility(View.VISIBLE);

    }

    private void hideAll() {
        ll_retry.setVisibility(View.GONE);
        ll_load.setVisibility(View.GONE);
        mMapView.setVisibility(View.GONE);

    }
}
