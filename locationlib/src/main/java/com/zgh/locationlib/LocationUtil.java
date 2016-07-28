package com.zgh.locationlib;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

/**
 * 用于定位的工具类
 * Created by zhuguohui on 2016/7/22.
 */
public class LocationUtil {

    // 定位相关
    static LocationClient mLocClient;
    public static MyLocationListener myListener = new MyLocationListener();
    private static BDLocation sLocation = null;
    private static int MSG_CHECK_TIMEOUT = 1;
    private static boolean haveInited=false;
    public static boolean test=false;
    private static Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(sLocation==null){
                        if(mLocationListener!=null){
                            mLocationListener.onGetLocationTimeOut();
                        }
                    }
                    if(mLocClient.isStarted()){
                        mLocClient.stop();
                    }
                    break;
            }
        }
    };


    public static void init(Context context) {
        //百度地图初始化
        SDKInitializer.initialize(context.getApplicationContext());

        // 定位初始化
        mLocClient = new LocationClient(context.getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);;
        haveInited=true;
    }


    /**
     * 定位SDK监听函数
     */
    private static class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null) {
                return;
            }
            if(!test) {
                sLocation = location;
                if (mLocationListener != null) {
                    mLocationListener.onReceiveLocation(sLocation);
                }
                if (sNeedAutoClose) {
                    if (mLocClient.isStarted()) {
                        mLocClient.stop();
                    }
                }
            }


        }

    }

    public static interface LocationListener {

        void onGetLocationStart();

        void onReceiveLocation(BDLocation location);

        void onGetLocationTimeOut();
    }

    public static class LocationListenrAdatper implements LocationListener{

        @Override
        public void onGetLocationStart() {

        }

        @Override
        public void onReceiveLocation(BDLocation location) {

        }

        @Override
        public void onGetLocationTimeOut() {

        }
    }

    private static LocationListener mLocationListener = null;
    private static boolean sNeedAutoClose=true;
    /**
     * 获取定位
     *
     * @param listener    回调
     * @param timeOut     超时时间:单位毫秒，-1表示不限时间。
     * @param forceUpdate 强制刷新
     */
    public static void getLocation(LocationListener listener, long timeOut, boolean forceUpdate,boolean autoClose) {
        if(!haveInited){
            throw new RuntimeException("请先使用init()方法进行初始化");
        }

        if(forceUpdate||sLocation==null){
            if (mLocationListener != null) {
                mLocationListener.onGetLocationStart();
            }
        }
        //不要求强制刷新的时候，使用已有的定位
        if (!forceUpdate && sLocation != null) {
            if (listener != null) {
                listener.onReceiveLocation(sLocation);
            }
        }

        //开始定位
        sNeedAutoClose=autoClose;
        sLocation=null;
        mLocationListener = listener;
        mLocClient.start();

        if (timeOut != -1) {
            mHandler.sendEmptyMessageDelayed(MSG_CHECK_TIMEOUT, timeOut);
        }

    }

    /**
     * 获取一次定位
     * @param listener
     */
    public static void  getLocation(LocationListener listener,boolean forceUpdate){
        getLocation(listener,-1,forceUpdate,true);
    }


    public static void stopLoacation(){
        if(mLocClient!=null&&mLocClient.isStarted()){
            mLocClient.stop();
        }
    }
}
