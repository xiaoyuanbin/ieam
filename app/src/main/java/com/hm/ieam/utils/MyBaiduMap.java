package com.hm.ieam.utils;

import android.content.Context;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;
import java.util.Locale;

public class MyBaiduMap {

    Context context;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    int accuracyCircleFillColor = 0xAAFFFF88;//自定义精度圈填充颜色
    int accuracyCircleStrokeColor = 0xAA00FF00;//自定义精度圈边框颜色
    MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private boolean isFirstLoc = true; // 是否首次定位
    public MyBaiduMap(MapView mMapView,Context context) {
        this.context=context;
        this.mMapView=mMapView;
        initMap();
    }


    private void initMap() {
        mBaiduMap = mMapView.getMap();
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);

        //默认显示普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(context);     //声明LocationClient类
        //配置定位SDK参数
        initLocation();
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        //开启定位
        mLocationClient.start();

        //图片点击事件，回到定位点
        mLocationClient.requestLocation();

    }

    public void reStart(){
        mLocationClient.restart();
    }
    public void initLocation() {

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);


        option.setCoorType("bd09ll");


        option.setScanSpan(1000);

        option.setOpenGps(true);


        option.setLocationNotify(true);


        option.setIgnoreKillProcess(false);


        option.SetIgnoreCacheException(false);


        option.setWifiCacheTimeOut(5*60*1000);

        option.setEnableSimulateGps(false);

        mLocationClient.setLocOption(option);

    }
    public void searchPosition(String address) {

    }


    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            // 当不需要定位图层时关闭定位图层
            //mBaiduMap.setMyLocationEnabled(false);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

//                if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                    // GPS定位结果
//                    Toast.makeText(context, "GPS定位成功，当前位置为：经度"+location.getLatitude()+"，纬度"+location.getLongitude(), Toast.LENGTH_SHORT).show();
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                    // 网络定位结果
//                    Toast.makeText(context, "网络定位成功，当前位置为：经度"+location.getLatitude()+"，纬度"+location.getLongitude(), Toast.LENGTH_SHORT).show();
//
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
//                    // 离线定位结果
//                    Toast.makeText(context,"离线定位成功，当前位置为：经度"+location.getLatitude()+"，纬度"+location.getLongitude(), Toast.LENGTH_SHORT).show();
//
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    Toast.makeText(context, "服务器错误，请检查", Toast.LENGTH_SHORT).show();
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    Toast.makeText(context, "网络错误，请检查", Toast.LENGTH_SHORT).show();
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    Toast.makeText(context, "手机模式错误，请检查是否飞行", Toast.LENGTH_SHORT).show();
//                }
            }


        }
    }

}
