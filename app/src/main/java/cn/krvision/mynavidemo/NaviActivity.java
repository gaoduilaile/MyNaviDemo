package cn.krvision.mynavidemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Polyline;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.LatLonPoint;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.List;

public class NaviActivity extends AppCompatActivity implements AMapNaviListener, AMapNaviViewListener {
    private Context mContext;
    private AMapNaviView aMapNaviView;
    private AMapNavi mAMapNavi;
    private AMapNaviPath mAMapNaviPath;
    private List<NaviLatLng> coordList;
    private List<AMapNaviStep> stepsList;
    private double mNaviAmapLatitude;
    private double mNaviAmapLongitude;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private AMap aMap;
    private Polyline polyline;
    private Button btn;
    private ArrayList<NaviLatLng> naviLatLngArrayList = new ArrayList<>(), naviLatLngArrayList2;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private ArrayList<NaviLatLng> myCurrentCoordsLinkList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mStartPoint = getIntent().getParcelableExtra("mLatLonPoint");
        mEndPoint = new LatLonPoint(30.242301161024304, 120.03618326822917);

        LogUtils.e("onCreate  ", mStartPoint.toString() + " " + mEndPoint.toString());

        initPermission();
        initNavi(savedInstanceState);
    }

    private void initNavi(Bundle savedInstanceState) {
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        aMapNaviView = (AMapNaviView) findViewById(R.id.navi_mapView);
        aMapNaviView.onCreate(savedInstanceState);
        aMapNaviView.setAMapNaviViewListener(this);
        AMapNaviViewOptions options = aMapNaviView.getViewOptions();
        options.setTrafficLayerEnabled(false);
        options.setCompassEnabled(false);
        options.setLayoutVisible(false);
        options.setAutoChangeZoom(true);
        options.setPointToCenter(0.5, 0.5);
        options.setLaneInfoShow(true);
        aMapNaviView.setViewOptions(options);
        aMapNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);

        if (aMap == null) {
            aMap = aMapNaviView.getMap();
        }

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, MapCarvenActivity.class).putParcelableArrayListExtra("naviLatLngArrayList", naviLatLngArrayList));
//                startActivity(new Intent(mContext,MapCarvenActivity.class).putParcelableArrayListExtra("naviLatLngArrayList",myCurrentCoordsLinkList));
            }
        });
    }


    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || mContext.checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    || mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                boolean isAllGranted = true;
                // 判断是否所有的权限都已经授予了
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        LogUtils.e("onInitNaviSuccess ", " " + mStartPoint.toString() + " " + mEndPoint.toString());
        mAMapNavi.calculateWalkRoute(new NaviLatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()),
                new NaviLatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude()));
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        mAMapNavi.startNavi(NaviType.GPS);
        mAMapNaviPath = mAMapNavi.getNaviPath();//获取当前规划的路线方案 获取当前计算出的路线，步行和驾车共用这一个对
        coordList = mAMapNaviPath.getCoordList();

        stepsList = mAMapNaviPath.getSteps();


        LogUtils.e("onCalculateRouteSuccess 111 ", "coordList=" + coordList.size() + "  stepsList=" + stepsList.size());


        List<AMapTrafficStatus> trafficStatuses = mAMapNaviPath.getTrafficStatuses();
        LogUtils.e("onCalculateRouteSuccess 222  ", trafficStatuses.size() + " ");
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

        mNaviAmapLatitude = naviInfo.m_Latitude;
        mNaviAmapLongitude = naviInfo.m_Longitude;

        LogUtils.e("onNaviInfoUpdate ", mNaviAmapLatitude + " " + mNaviAmapLongitude);


        int current_step = naviInfo.getCurStep();//获取当前大路段索引
        int current_link = naviInfo.getCurLink();//获取自车所在小路段

        LogUtils.e("onNaviInfoUpdate ", " 当前大路段下标 current_step=" + current_step + " 当前小路段下标 current_link=" + current_link);


        AMapNaviStep aMapNaviCurrentStep = mAMapNaviPath.getSteps().get(current_step);//获取当前step大路段道路
        List<NaviLatLng> coordsCurrentStepList = aMapNaviCurrentStep.getCoords();//获取当前step大路段道路 所有的点

        int startIndex = aMapNaviCurrentStep.getStartIndex();
        int endIndex = aMapNaviCurrentStep.getEndIndex();
        LogUtils.e("onNaviInfoUpdate ", "当前大路段开始结束点 startIndex=" + startIndex + " endIndex=" + endIndex);
        LogUtils.e("onNaviInfoUpdate ", "当前大路段开始结束点 startIndex=" + 0 + " endIndex=" + (coordsCurrentStepList.size() - 1));
        LogUtils.e("onNaviInfoUpdate ", "当前大路段开始结束点 startIndex=" + coordsCurrentStepList.get(0).toString() +
                " endIndex=" + coordsCurrentStepList.get(coordsCurrentStepList.size() - 1).toString());


        AMapNaviLink aMapNaviLink = aMapNaviCurrentStep.getLinks().get(current_link);//获取当前step大路段道路中的Linkd道路
        //获取当前Linkd道路坐标点集
        myCurrentCoordsLinkList = (ArrayList<NaviLatLng>) aMapNaviLink.getCoords();

        LogUtils.e("onNaviInfoUpdate ", "当前小路段开始结束点 startIndex=" + 0 + " endIndex=" + (myCurrentCoordsLinkList.size() - 1));
        LogUtils.e("onNaviInfoUpdate ", "当前小路段开始结束点 startIndex=" + myCurrentCoordsLinkList.get(0).toString() +
                " endIndex=" + myCurrentCoordsLinkList.get(myCurrentCoordsLinkList.size() - 1).toString());


        NaviLatLng naviLatLngStepStart = coordsCurrentStepList.get(0);
        NaviLatLng naviLatLngStepEnd = coordsCurrentStepList.get(coordsCurrentStepList.size() - 1);




//        naviLatLngArrayList = new ArrayList<>();
//        naviLatLngArrayList.add(naviLatLngStepStart);
//        naviLatLngArrayList.add(naviLatLngStepEnd);
//        naviLatLngArrayList.add(naviLatLngLinkStart);
//        naviLatLngArrayList.add(naviLatLngLinkEnd);

        List<AMapNaviLink> linksList = aMapNaviCurrentStep.getLinks();

        for (AMapNaviLink mapNaviLink : linksList) {
            List<NaviLatLng> coordsList = mapNaviLink.getCoords();

            for (NaviLatLng naviLatLng : coordsList) {
                naviLatLngArrayList.add(naviLatLng);
            }
//            NaviLatLng latLngLinkStart = coordsList.get(0);
//            NaviLatLng latLngLinkEnd = coordsList.get(coordsList.size() - 1);
//            naviLatLngArrayList.add(latLngLinkStart);
//            naviLatLngArrayList.add(latLngLinkEnd);

        }


        boolean trafficLights1 = aMapNaviLink.getTrafficLights();//获取当前Linkd道路是否有红绿灯
        int trafficLightNumber1 = aMapNaviCurrentStep.getTrafficLightNumber();
        LogUtils.e("onNaviInfoUpdate ", "当前大路段红绿灯数量 trafficLightNumber1=" + trafficLightNumber1 + "  当前小路段是否有红绿灯 trafficLights1=" + trafficLights1);


//        //将坐标点添加到地图上
        if (mAMapNaviPath.getSteps().size() > current_step + 1) {

            AMapNaviStep aMapNaviNextStep = mAMapNaviPath.getSteps().get(current_step + 1);//获取当前step大路段道路
            AMapNaviLink aMapNaviLinkNext = aMapNaviNextStep.getLinks().get(0);//获取当前step大路段道路中的Linkd道路
            List<NaviLatLng> myCurrentCoordsLinkListNext = aMapNaviLinkNext.getCoords();//获取当前Linkd道路坐标点集
            int trafficLightNumber = aMapNaviNextStep.getTrafficLightNumber();
            boolean trafficLights = aMapNaviLinkNext.getTrafficLights();

            LogUtils.e("onNaviInfoUpdate ", "下一个大路段红绿灯数量 trafficLightNumber=" +
                    trafficLightNumber + "  下一个小路段是否有红绿灯 trafficLights=" + trafficLights);
        }


    }


    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {
        LogUtils.e("onGetNavigationText ", " " + s);
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }


    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }


    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {

    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }


}
