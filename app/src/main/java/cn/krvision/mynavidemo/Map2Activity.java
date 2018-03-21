package cn.krvision.mynavidemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Map2Activity extends BaseActivity implements PoiSearch.OnPoiSearchListener, SensorEventListener {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.tv_nowAddress)
    TextView tvNowAddress;
    @BindView(R.id.tv_nextAddress)
    TextView tvNextAddress;
    @BindView(R.id.tv_remainDistance)
    TextView tvRemainDistance;
    @BindView(R.id.tv_stepRetainDistance)
    TextView tvStepRetainDistance;
    @BindView(R.id.tv_offestAngle)
    TextView tvOffestAngle;

    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private Context mContext;
    private AMapLocationClientOption mLocationOption;
    private LocationSource.OnLocationChangedListener mListener;
    private LatLonPoint mLatLonPoint;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private int pageNum = 1;

    private int totalDistance;
    private int alreadyDistance;
    private int remainDistance;
    private int current_road_angle;
    private int calAngle;
    private int offest_angle;
    private String offest_angle_string;
    private int index = 0;

    private float latitude;
    private float longitude;
    private ExcelBean excelBeanCurrent;
    private ExcelBean excelBeanNext;
    private ExcelBean excelBeanPrevious;
    private SensorManager mSensorManager;
    private Sensor sensorOrientation;
    private ArrayList<ExcelBean> excelData;
    private int toNextPoiDistance;
    private ArrayList<String> arrayList;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private Button btn_trail1;
    private Button btn_trail2;
    private Button btn_trail3;
    private int getIndex;
    private String pathString = "";
    private String colorString = "";
    private MapModel mapModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        ButterKnife.bind(this);
        mContext = this;
        mapModel = new MapModel(mContext);

//        int b = JniKit.helloFromC(30.00, 120.11);


//        LogUtils.e("onCreate ", JniKit.helloFromC(2.1,4.9)+" ");
        JniKit jniKit= new JniKit();
        boolean b = jniKit.JniCheckSB();
        LogUtils.e("onCreate ", b+" ");


        mapModel.method1();
//        mapModel.method2();

        mSensorManager = (SensorManager) getSystemService(mContext.SENSOR_SERVICE);
        sensorOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

//        initIntent();

        iniMapView(savedInstanceState);

//        initData();

        btn_trail1 = findViewById(R.id.btn_trail1);
        btn_trail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathString = "GPSDATA.xls";
                colorString = "#00ff00";
//                aMap.clear();
                initData();
            }
        });
        btn_trail2 = findViewById(R.id.btn_trail2);
        btn_trail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathString = "FilterGPSDATA.xls";
                colorString = "#00CC00";
//                aMap.clear();
                initData();
            }
        });

        btn_trail3 = findViewById(R.id.btn_trail3);
        btn_trail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathString = "GPSDATA2.xls";
                colorString = "#339933";
//                aMap.clear();
                initData();
            }
        });

    }

    private void initIntent() {
        Intent intent = getIntent();
        String scheme = intent.getScheme();
        Uri uri = intent.getData();

        if (uri != null && TextUtils.equals(uri.toString(), "krvision://com.navigation")) {

            LogUtils.e(" intent  ", "uri:" + uri.toString());

            startActivity(new Intent(mContext, Map1Activity.class));
        }
    }

    private void initData() {
        /*得到所的点*/
        excelData = SaveFile.getExcelData(mContext, pathString, 0);
        LogUtils.e("initData  ", excelData + "  ");

        /*总距离计算*/
        for (int i = 0; i < excelData.size() - 1; i++) {
            ExcelBean excelBean1 = excelData.get(i);
            ExcelBean excelBean2 = excelData.get(i + 1);
            int roadDistance = (int) AMapUtils.calculateLineDistance(new LatLng(excelBean1.getLatitude(), excelBean1.getLongitude()),
                    new LatLng(excelBean2.getLatitude(), excelBean2.getLongitude()));
            totalDistance += roadDistance;
        }

        addTrail2Map(excelData);
    }

    /**
     * 2017/11/10  GaoQiong 显示数据
     */
    private void addTrail2Map(ArrayList<ExcelBean> excelData) {
        List<Integer> listColor = new ArrayList<>();
        List<LatLng> latLngList = new ArrayList<>();
        for (ExcelBean excelDatum : excelData) {
            LatLng latLng = new LatLng(excelDatum.getLatitude(), excelDatum.getLongitude());
//            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(excelDatum.getName()).snippet("DefaultMarker"));
//            LogUtils.e("initData  ", latLng.toString() +"");

            latLngList.add(latLng);
        }

        listColor.add(Color.parseColor("#66CCCC"));
        listColor.add(Color.parseColor("#66CC99"));
        listColor.add(Color.parseColor("#339933"));
        listColor.add(Color.parseColor("#336633"));
        aMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .width(10)
                .color(Color.parseColor(colorString))
                .useGradient(true)
                .geodesic(true)
        );

        LatLngBounds.Builder b = LatLngBounds.builder();
        for (LatLng latLng : latLngList) {
            b.include(latLng);
        }
        LatLngBounds bounds = b.build();
        aMap.animateCamera(CameraUpdateFactory
                .newLatLngBounds(bounds, 18));


    }


    private void iniMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        } else {
            aMap.clear();
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.getUiSettings().setZoomControlsEnabled(false);//去掉高德地图右下角隐藏的缩放按钮
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//禁止倾斜手势

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.26967, 120.069717), 20));//初始化中心位置 和缩放比例
        UiSettings uiSettings = aMap.getUiSettings();
//        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setScaleControlsEnabled(true);
    }

    /**
     * 2017/11/8  GaoQiong 设置小蓝点
     */
    private void setUpMap() {
        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                startLocationMethod(onLocationChangedListener);
            }

            @Override
            public void deactivate() {

            }
        });// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
    }

    private void startLocationMethod(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(mContext);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {

                    if (mListener != null && aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            latitude = (float) aMapLocation.getLatitude();
                            longitude = (float) aMapLocation.getLongitude();
                            mLatLonPoint = new LatLonPoint(latitude, longitude);
//                            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
//                            aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
//                            LogUtils.e("onLocationChanged ", mLatLonPoint.toString() + " " + aMapLocation.getAddress() + aMapLocation.getAoiName());

                            calculateNavigation();
                            // 32.06051459418403,118.80736707899305
                            // 30.232511393229167,119.98714138454861
                            // 30.24258083767361,120.03440185546874
                        }
                    }
                }
            });

            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);   //设置为高精度定位模式
            if (mLocationOption.isOnceLocationLatest()) {
                //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
                //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
                mLocationOption.setOnceLocationLatest(true);
            }
            mlocationClient.setLocationOption(mLocationOption);    //设置定位参数
            mLocationOption.setInterval(2000);// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，设置定位间隔,单位毫秒,默认为2000ms
            mlocationClient.startLocation();
        }
    }


    /*导航计算*/
    private void calculateNavigation() {

        /*计算最近的点为当前的标记点*/
        excelBeanCurrent = GetCustomPoiPointControl.searchPoi(latitude, longitude, excelData, index);

        if (excelBeanCurrent != null) {
            index = excelBeanCurrent.getId();
        }
        LogUtils.e("initData  ", excelBeanCurrent + "  ");

    /*已经走过的点的距离计算*/
        for (int i = 0; i < index - 1; i++) {
            ExcelBean excelBean1 = excelData.get(i);
            ExcelBean excelBean2 = excelData.get(i + 1);

            int roadDistance = (int) AMapUtils.calculateLineDistance(new LatLng(excelBean1.getLatitude(), excelBean1.getLongitude()),
                    new LatLng(excelBean2.getLatitude(), excelBean2.getLongitude()));
            alreadyDistance += roadDistance;
        }

        //得到上一个点
        if (index > 0 && index < excelData.size() - 1) {
            excelBeanPrevious = excelData.get(index - 1);
        }

        //当前位置到上一个标记点的距离
        if (excelBeanCurrent != null) {
            int toCurrentPoiDistance = (int) AMapUtils.calculateLineDistance(new LatLng(latitude, longitude),
                    new LatLng(excelBeanCurrent.getLatitude(), excelBeanCurrent.getLongitude()));
            alreadyDistance += toCurrentPoiDistance;
        }

        remainDistance = totalDistance - alreadyDistance;

        //得到下一个点  并计算道路朝向
        if (index >= 0 && index < excelData.size() - 1) {
            excelBeanNext = excelData.get(index + 1);
            current_road_angle = (int) GetCustomPoiPointControl.getPoiAngle00(new LatLng(excelBeanCurrent.getLatitude(), excelBeanCurrent.getLongitude()),
                    new LatLng(excelBeanNext.getLatitude(), excelBeanNext.getLongitude()));
        }

        //当前位置到下一个标记点的距离
        if (excelBeanNext != null) {
            toNextPoiDistance = (int) AMapUtils.calculateLineDistance(new LatLng(latitude, longitude),
                    new LatLng(excelBeanNext.getLatitude(), excelBeanNext.getLongitude()));
        }


        tvNowAddress.setText("当前点：" + excelBeanCurrent.getName() + "   动作：" + excelBeanCurrent.getAction());
        tvRemainDistance.setText("剩余距离：" + remainDistance);
        tvNowAddress.setText("下一个点：" + excelBeanNext.getName() + "   动作：" + excelBeanNext.getAction());
        tvStepRetainDistance.setText("距离下一个点：" + toNextPoiDistance + "米");

        TTSSpeak(0, "当前点位置" + excelBeanCurrent.getName() + "，" + excelBeanCurrent.getAction() + ",距离下一个点：" + toNextPoiDistance + "米");

    }


    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, sensorOrientation, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            float x = sensorEvent.values[0];
            int currentDegree = (int) GetCustomPoiPointControl.getPhoneCurrentDegree(x);

            calAngle = currentDegree;

            offest_angle = (int) (calAngle - current_road_angle);
            if ((offest_angle >= 0) && (offest_angle <= 180)) {
                offest_angle_string = "偏右" + offest_angle;
            } else if ((offest_angle < 0) && (offest_angle >= -180)) {
                offest_angle = Math.abs(offest_angle);
                offest_angle_string = "偏左" + offest_angle;
            } else if (offest_angle > 180) {
                offest_angle = 360 - offest_angle;
                offest_angle_string = "偏左" + offest_angle;
            } else if (offest_angle < -180) {
                offest_angle = 360 - Math.abs(offest_angle);
                offest_angle_string = "偏右" + offest_angle;
            }

            tvOffestAngle.setText("道路夹角" + offest_angle_string + "度");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * 搜索周围POI点
     */
    private void PoiAroundSearch() {

        // 汽车服务/汽车销售/汽车维修/摩托车服务/餐饮服务/购物服务/生活服务/体育休闲服务/医疗保健服务/住宿服务/风景名胜/商务住宅/政府机构及社会团体/科教文化服务
        // /交通设施服务/金融保险服务/公司企业/道路附属设施/地名地址信息/公共设施


        String subString = SaveFile.readAssetsTxt(mContext, "poi_search.txt");


//        query = new PoiSearch.Query("", "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|" +
//                "住宿服务|110103|110205|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|道路附属设施|金融保险服务|公司企业|道路附属设施|" +
//                "地名地址信息|公共设施|事件活动|室内设施|通行设施", "");

        query = new PoiSearch.Query("", "110000|110206", "");
//        query = new PoiSearch.Query("", subString, "");


        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setPageNum(pageNum);// 设置查第一页
        query.setDistanceSort(true);
        poiSearch = new PoiSearch(mContext, query);
            /*开启监听*/
        poiSearch.setOnPoiSearchListener(this);
        // 设置搜索区域为以lp点为圆心，其周围200米范围
        poiSearch.setBound(new PoiSearch.SearchBound(mLatLonPoint, 200, true));//
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {

        LogUtils.e("onPoiSearched ", i + " ");
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    // 取得第一页的poiitem数据，页数从数字0开始
                    List<PoiItem> poiItems = poiResult.getPois();

                    LogUtils.e("onPoiSearched  size ", poiItems.size() + " ");
                    if (poiItems != null && poiItems.size() > 0) {


                        for (PoiItem poiItem : poiItems) {

                            LogUtils.e("onPoiSearched ", poiItem.getTitle() + "   " + poiItem.getDistance() + "   poiItems.seze=" + poiItems.size()

                                    + "   getTypeCode=" + poiItem.getTypeCode());

                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
