package cn.krvision.mynavidemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MultiPointItem;
import com.amap.api.maps.model.MultiPointOverlay;
import com.amap.api.maps.model.MultiPointOverlayOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.model.NaviLatLng;

import java.util.ArrayList;
import java.util.List;

public class MapCarvenActivity extends AppCompatActivity {

    private MapView mMapView;
    private AMap aMap;
    private Context mContext;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;
    private Button btn_mark;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_carven);
        mContext = this;
        iniMapView(savedInstanceState);
    }

    private void iniMapView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);//去掉高德地图右下角隐藏的缩放按钮
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setRotateGesturesEnabled(true);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        initMarker();
    }


    private void initMarker() {
        if (getIntent() != null) {
//            ArrayList<NaviLatLng> naviLatLngArrayLists = getIntent().getParcelableArrayListExtra("naviLatLngArrayList");
//            if (naviLatLngArrayLists != null) {
//                aMap.clear();
//                LatLng latLng0 = new LatLng(naviLatLngArrayLists.get(0).getLatitude(), naviLatLngArrayLists.get(0).getLongitude());
//                LatLng latLng1 = new LatLng(naviLatLngArrayLists.get(1).getLatitude(), naviLatLngArrayLists.get(1).getLongitude());
//                LatLng latLng2 = new LatLng(naviLatLngArrayLists.get(2).getLatitude(), naviLatLngArrayLists.get(2).getLongitude());
//                LatLng latLng3 = new LatLng(naviLatLngArrayLists.get(3).getLatitude(), naviLatLngArrayLists.get(3).getLongitude());
//
//                final Marker marker0 = aMap.addMarker(new MarkerOptions().position(latLng0).title("大路段开启").snippet("DefaultMarker"));
//                final Marker marker1 = aMap.addMarker(new MarkerOptions().position(latLng1).title("大路段结束").snippet("DefaultMarker"));
//                final Marker marker2 = aMap.addMarker(new MarkerOptions().position(latLng2).title("小路段结束").snippet("DefaultMarker"));
//                final Marker marker3 = aMap.addMarker(new MarkerOptions().position(latLng3).title("小路段结束").snippet("DefaultMarker"));
//
//                ArrayList<LatLng> LatLngList = new ArrayList<>();
////                LatLngList.add(latLng0);
////                LatLngList.add(latLng1);
//                LatLngList.add(latLng2);
//                LatLngList.add(latLng3);
//                addTrail2Map(LatLngList, 0);
//
//            }



            ArrayList<NaviLatLng> myCurrentCoordsLinkList = getIntent().getParcelableArrayListExtra("naviLatLngArrayList");

            LogUtils.e("onLocationChanged ", myCurrentCoordsLinkList.size()+" ");

            ArrayList<LatLng> LatLngList = new ArrayList<>();
            for (NaviLatLng naviLatLng : myCurrentCoordsLinkList) {
                LatLng latLng0 = new LatLng(naviLatLng.getLatitude(), naviLatLng.getLongitude());
                LatLngList.add(latLng0);
            }

            LogUtils.e("onLocationChanged ", "LatLngList "+LatLngList.size()+" ");
//            addTrail2Map(LatLngList, 0);

            markedMeethod(LatLngList);

        }
    }


    /**
     * 2017/11/10  GaoQiong 显示数据
     */
    private void addTrail2Map(ArrayList<LatLng> latLngList, int indexColor) {

        List<Integer> listColor = new ArrayList<>();
        listColor.add(Color.parseColor("#FF1493"));
        listColor.add(Color.parseColor("#71C671"));
        listColor.add(Color.parseColor("#030303"));
        listColor.add(Color.parseColor("#0000FF"));

        polyline = aMap.addPolyline(new PolylineOptions()
                        .addAll(latLngList)
//                        .add(latLng)
                        .width(20)
//                        .colorValues(listColor)
                        .color(listColor.get(indexColor))
                        .useGradient(true)
                        .geodesic(true)

        );

        LatLngBounds.Builder b = LatLngBounds.builder();

        for (LatLng latLng : latLngList) {
            b.include(latLng);
        }

        LogUtils.e("onLocationChanged ", "LatLngBounds "+b.toString());
        LatLngBounds bounds = b.build();
        aMap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(bounds, 30));
    }


    private void markedMeethod(ArrayList<LatLng> latLngList){
        MultiPointOverlayOptions overlayOptions = new MultiPointOverlayOptions();
//        overlayOptions.icon(bitmapDescriptor);//设置图标
        overlayOptions.anchor(0.5f,0.5f); //设置锚点

        MultiPointOverlay multiPointOverlay = aMap.addMultiPointOverlay(overlayOptions);

        List<MultiPointItem> list = new ArrayList<MultiPointItem>();

        for (LatLng latLng : latLngList) {
            MultiPointItem multiPointItem = new MultiPointItem(latLng);
            list.add(multiPointItem);
        }

        multiPointOverlay.setItems(list);//将规范化的点集交给海量点管理对象设置，待加载完毕即可看到海量点信息
    }

}
