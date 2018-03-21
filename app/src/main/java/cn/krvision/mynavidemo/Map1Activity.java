package cn.krvision.mynavidemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Polyline;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

public class Map1Activity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    private MapView mMapView;
    private AMap aMap;
    private Polyline polyline;
    private AMapLocationClient mlocationClient;
    private Context mContext;
    private AMapLocationClientOption mLocationOption;
    private Button btn;
    private LocationSource.OnLocationChangedListener mListener;
    private LatLonPoint mLatLonPoint;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private Button btn_search, btn_search_more, btn_search_nearly,btn_save;
    private int pageNum = 1;
    private List<Tip> tipLists;
    private ArrayList<String> arrayList;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mContext = this;






        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, NaviActivity.class).putExtra("mLatLonPoint", mLatLonPoint));
                finish();
            }
        });


        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoiAroundSearch();
//                doInputTipSearchQuery();
            }
        });


        btn_search_more = (Button) findViewById(R.id.btn_search_more);
        btn_search_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageNum++;
                PoiAroundSearch();
            }
        });


        btn_search_nearly = (Button) findViewById(R.id.btn_search_nearly);
        btn_search_nearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, NearlySearchActivity.class));
            }
        });


        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.add(mLatLonPoint.toString()+"    ");
                arrayAdapter.notifyDataSetChanged();
            }
        });


        arrayList = new ArrayList<>();
        listView = findViewById(R.id.lv);
        arrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter );

        setBrightness(this,0.1f);


        iniMapView(savedInstanceState);

    }

    public static void setBrightness(Activity activity, float brightnessValue) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (brightnessValue > 1.0f) {
            lp.screenBrightness = 1.0f;
        } else if (brightnessValue <= 0.0f) {
            lp.screenBrightness = 0.0f;
        } else {
            lp.screenBrightness = brightnessValue;
        }
        activity.getWindow().setAttributes(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void iniMapView(Bundle savedInstanceState) {
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.getUiSettings().setZoomGesturesEnabled(true);
        aMap.getUiSettings().setRotateGesturesEnabled(true);
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
                            mLatLonPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                            mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                            aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
//                            LogUtils.e("onLocationChanged ", mLatLonPoint.toString() + " "+aMapLocation.getAddress()+aMapLocation.getAoiName());

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


    private void doInputTipSearchQuery() {
        InputtipsQuery inputquery = new InputtipsQuery("", "南京");
        inputquery.setCityLimit(true);//限制在当前城市
        inputquery.setLocation(mLatLonPoint);
        Inputtips inputTips = new Inputtips(mContext, inputquery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (tipList != null) {
                        tipLists = tipList;
                        LogUtils.e("MDDDDDD", "tipList.size() = " + tipList.toString());
                        for (Tip tip : tipList) {

                            String PoiID = tip.getPoiID();
                            LogUtils.e("PoiType=", " 2322" + tip.getTypeCode() + "  " + tip.getName() + " " + tip.getAddress());
                            LatLonPoint point = tip.getPoint();

                            double PoiLat = 0;
                            double PoiLng = 0;
                            if (PoiID != null && !TextUtils.isEmpty(PoiID)) {
                                if (point != null) {

                                    LogUtils.e("onGetInputtips=", "  PoiID=");
                                    PoiLat = tip.getPoint().getLatitude();
                                    PoiLng = tip.getPoint().getLongitude();
                                    LogUtils.e("onGetInputtips=", "  PoiID=" + PoiID);
                                    LogUtils.e("onGetInputtips=", "  Point=" + tip.getPoint().toString());


                                } else {
                                }
                            }
                        }

                    } else {
                        LogUtils.e("sdfdsfds", "对不起，没有搜索到相关数据！");
                    }
                } else {
                    LogUtils.e("sdfdsfds", "对不起，请检查你的网络！");
                }
            }
        });
        inputTips.requestInputtipsAsyn();
    }
}
