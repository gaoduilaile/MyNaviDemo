package cn.krvision.mynavidemo;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class GetCustomPoiPointControl {

    //获取最近标志物（筛选过后的POI）
    public String getString(List<PoiItem> poi_items, LatLng latlng) {

        String return_msg = "";

        String tempPOIname = new String();
        String poi4names = "";     //地理坐标系下周围4个poi的名字
        int poi4dist = 1000;            //4个poi的距离，初始化为1000米

        for (int i = 0; i < poi_items.size(); i++) {

            tempPOIname = poi_items.get(i).getTitle();

            //tempPOIdir判断：
            double latitude = poi_items.get(i).getLatLonPoint().getLatitude();//获取POI的纬度
            double longtitude = poi_items.get(i).getLatLonPoint().getLongitude();//获取POI的经度

            //根据i点经纬度获取Latlng对象
            LatLng round_latlng = new LatLng(latitude, longtitude);

            //计算第i个POI点与用户当前位置的距离
            double d_distance = AMapUtils.calculateLineDistance(latlng, round_latlng);
            int distance = (int) d_distance;

            if (distance < poi4dist) {
                poi4dist = distance;
                poi4names = tempPOIname;
            }
        }

        if (poi4dist <= 500) {
            return_msg = "当前位置" + cutReturnString(poi4names) + "附近";
        } else {
            return_msg = "当前位置200米内没有标志建筑";
        }

        return return_msg;
    }

    /*得到用户手机朝向*/
    public static String getCurrentDirection(double angle_z) {
        String tempPOIdir = "";
        if ((angle_z >= 67.5) && (angle_z <= 112.5)) {
            tempPOIdir = "东";
        } else if ((angle_z > 112.5) && (angle_z < 157.5)) {
            tempPOIdir = "东南";
        } else if ((157.5 <= angle_z) || (angle_z < -157.5)) {
            tempPOIdir = "南";
        } else if ((-157.5 <= angle_z) && (angle_z < -112.5)) {
            tempPOIdir = "西南";
        } else if ((-112.5 <= angle_z) && (angle_z <= -67.5)) {
            tempPOIdir = "西";
        } else if ((-67.5 < angle_z) && (angle_z < -22.5)) {
            tempPOIdir = "西北";
        } else if ((angle_z >= -22.5) && (angle_z <= 22.5)) {
            tempPOIdir = "北";
        } else if ((22.5 < angle_z) && (angle_z < 67.5)) {
            tempPOIdir = "东北";
        }
        return tempPOIdir;
    }

    //获取单个点的方位
    public static String getSinglePointDirection(LatLng latlng1, LatLng latlng2, double angle_face) {

        double pointToPointAngle = getPoiAngle00(latlng1, latlng2);

        double angle = getCornerDirection(angle_face, pointToPointAngle);

        if (angle > 0) {
            return "右";
        } else {
            return "左";
        }
    }

    //获取最近200m各个方向的标志物（筛选过后的POI）
    public static String getStringDirection(List<PoiItem> poi_items, LatLng latlng, double angle_z, int button_flag) {
        String return_string = "";
        String[] userPoi4names = new String[4]; //用户坐标系下周围4个poi的名字
        int[] userPoi4dist = {200, 200, 200, 200};  //用户坐标系下周围4个poi的距离

        for (int i = 0; i < poi_items.size(); i++) {
            //tempPOIdir判断：
            double latitude = poi_items.get(i).getLatLonPoint().getLatitude();//获取POI的纬度
            double longtitude = poi_items.get(i).getLatLonPoint().getLongitude();//获取POI的经度
            //根据i点经纬度获取Latlng对象
            LatLng round_latlng = new LatLng(latitude, longtitude);
            //计算第i个POI点与用户当前位置的距离
            double d_distance = AMapUtils.calculateLineDistance(latlng, round_latlng);
            int distance = (int) d_distance;

            if (distance <= 200) {
                double pointToPointAngle = getPoiAngle00(latlng, new LatLng(latitude, longtitude));
                double angle = getCornerDirection(angle_z, pointToPointAngle);

                if (angle > -90 && angle < 0) {
                    if (userPoi4dist[0] > distance) {
                        userPoi4names[0] = poi_items.get(i).getTitle();
                        userPoi4dist[0] = distance;
                    }
                } else if (angle > 0 && angle < 90) {
                    if (userPoi4dist[1] > distance) {
                        userPoi4names[1] = poi_items.get(i).getTitle();
                        userPoi4dist[1] = distance;
                    }
                } else if (angle > -180 && angle < -90) {
                    if (userPoi4dist[2] > distance) {
                        userPoi4names[2] = poi_items.get(i).getTitle();
                        userPoi4dist[2] = distance;
                    }
                } else if (angle > 90 && angle < 180) {
                    if (userPoi4dist[3] > distance) {
                        userPoi4names[3] = poi_items.get(i).getTitle();
                        userPoi4dist[3] = distance;
                    }
                }
            }
        }

        String[] directions = {"左前方", "右前方", "左后方", "右后方"};

        for (int j = 0; j < userPoi4names.length; j++) {
            if (j == button_flag - 1) {
                if (userPoi4names[j] != null) {

                    return_string = cutReturnString(userPoi4names[j]) + "在你" + directions[j] + userPoi4dist[j] + "米";

                } else {
                    return_string = directions[j] + "没有标志建筑";
                }
            }
        }

        return return_string;
    }

    //定位点校正，将漂移点校正到行走路径上
    public LatLng refineLatlng(LatLng link_start, LatLng link_end, LatLng latlng) {
        LatLng re_latlng = null;
        //斜率对应方向
        if (link_end.longitude != link_start.longitude) {

            double link_direction = (link_end.latitude - link_start.latitude) / (link_end.longitude - link_start.longitude);

            if ((link_direction > -1) && (link_direction < 1)) {

                double link_lat = link_direction * (latlng.longitude - link_start.longitude) + link_start.latitude;

                re_latlng = new LatLng(link_lat, latlng.longitude);

            } else {

                link_direction = 1.0 / link_direction;

                double link_lng = link_direction * (latlng.latitude - link_start.latitude) + link_start.longitude;

                re_latlng = new LatLng(latlng.latitude, link_lng);
            }

        } else {

            re_latlng = new LatLng(latlng.latitude, link_start.longitude);
        }
        return re_latlng;
    }

    //获取路径方向角（与某一条纬线夹角）
    public static double getPoiAngle00(LatLng my_latlng, LatLng poi_latlng) {
        double m_angle_direction = 0;
        //斜率对应方向
        if (poi_latlng.longitude != my_latlng.longitude) {

            double link_direction = (poi_latlng.latitude - my_latlng.latitude) / (poi_latlng.longitude - my_latlng.longitude);
            double road_directtion = 180 / 3.1415926 * Math.atan(link_direction);
//            double toDegrees = Math.toDegrees(Math.atan(link_direction));
            if (road_directtion > 0) {
                if (poi_latlng.longitude - my_latlng.longitude > 0) {
                    m_angle_direction = road_directtion;
                } else {
                    m_angle_direction = road_directtion - 180;
                }
            } else {
                if (poi_latlng.longitude - my_latlng.longitude > 0) {
                    m_angle_direction = road_directtion;
                } else {
                    m_angle_direction = road_directtion + 180;
                }
            }
        } else {
            if (poi_latlng.latitude > my_latlng.latitude) {
                m_angle_direction = 90;
            } else if (poi_latlng.latitude < my_latlng.latitude) {
                m_angle_direction = -90;
            } else {
                m_angle_direction = 0;
            }
        }

        m_angle_direction = 90 - m_angle_direction;

        if (m_angle_direction > 180) {

            m_angle_direction = m_angle_direction - 360;
        }

        return m_angle_direction;
    }

    //当前朝向和道路的夹角
    public static double getCornerDirection(double current_angle, double next_angle) {

        double dir_angle = next_angle - current_angle;
        if (dir_angle > 180) {
            dir_angle = dir_angle - 360;
        } else if (dir_angle < -180) {
            dir_angle = dir_angle + 360;
        }
        return dir_angle;
    }

    //获取终点与当前位置绝对位置关系
    public String getEndPoint(double end_angle) {

        String return_str = "";

        if ((end_angle > -22.5) && (end_angle < 22.5)) {
            return_str = "正前方";
        } else if ((end_angle > 22.5) && (end_angle < 67.5)) {
            return_str = "右前方";
        } else if ((end_angle > 67.5) && (end_angle < 112.5)) {
            return_str = "右侧";
        } else if ((end_angle > 112.5) && (end_angle < 157.5)) {
            return_str = "右后方";
        } else if ((end_angle > -67.5) && (end_angle < -22.5)) {
            return_str = "左前";
        } else if ((end_angle > -112.5) && (end_angle < -67.5)) {
            return_str = "左侧";
        } else if ((end_angle > -157.5) && (end_angle < -112.5)) {
            return_str = "左后方";
        } else {
            return_str = "正后方";
        }

        return return_str;
    }

    //获取拐点提示信息
    public String getCorner(double corner_angle) {

        String return_str = "";

        if (corner_angle < 10 && corner_angle > -10) {
            return_str = "直行";
        } else if ((corner_angle > 10) && (corner_angle < 67.5)) {
            return_str = "右向小弯道";
        } else if ((corner_angle > 67.5) && (corner_angle < 112.5)) {
            return_str = "右向弯道";
        } else if ((corner_angle > 112.5) && (corner_angle < 157.5)) {
            return_str = "右向大弯道";
        } else if ((corner_angle > -67.5) && (corner_angle < -10)) {
            return_str = "左向小弯道";
        } else if ((corner_angle > -112.5) && (corner_angle < -67.5)) {
            return_str = "左向弯道";
        } else if ((corner_angle > -157.5) && (corner_angle < -112.5)) {
            return_str = "左向大弯道";
        } else {
            return_str = "到达弯道，掉头";
        }
        return return_str;
    }

    //点到直线的距离
    public double PointToLineDistance(LatLng m_round_latlng, LatLng m_link_start, LatLng m_link_end) {
        double re_distance = 0;
        //斜率对应方向
        if (m_link_end.longitude != m_link_start.longitude) {
            double link_direction = (m_link_end.latitude - m_link_start.latitude) / (m_link_end.longitude - m_link_start.longitude);
            double m_fx = link_direction * m_round_latlng.longitude - m_round_latlng.latitude + m_link_start.latitude - link_direction * m_link_start.longitude;
            double m_kx = Math.sqrt(link_direction * link_direction + 1);
            re_distance = m_fx / m_kx * 111319.5;
        } else {
            re_distance = Math.sqrt((m_round_latlng.longitude - m_link_start.longitude) * (m_round_latlng.longitude - m_link_start.longitude)) * 111319.5;
        }
        if (re_distance < 0) {
            re_distance = -re_distance;
        }
        return re_distance;
    }

    public static String cutReturnString(String return_string) {
        String[] cut_return_string = return_string.split("（");
        String[] cut_return_string00 = cut_return_string[0].split("\\(");
        return_string = cut_return_string00[0];
        return return_string;
    }

    public String getHeadString(float mAngle, double m_first_link_angle) {

        String head_string = "向前";

        int offest_angle = (int) (mAngle - m_first_link_angle);
        if ((offest_angle > 0) && (offest_angle <= 180)) {
            if (offest_angle < 23) {
                head_string = "向前";
            } else if (offest_angle > 150) {
                head_string = "向后";
            } else {
                head_string = "向左";
            }
        } else if ((offest_angle < 0) && (offest_angle >= -180)) {
            offest_angle = abs(offest_angle);
            if (offest_angle < 23) {
                head_string = "向前";
            } else if (offest_angle > 150) {
                head_string = "向后";
            } else {
                head_string = "向右";
            }
        } else if (offest_angle > 180) {
            offest_angle = 360 - offest_angle;
            if (offest_angle < 23) {
                head_string = "向前";
            } else if (offest_angle > 150) {
                head_string = "向后";
            } else {
                head_string = "向右";
            }
        } else if (offest_angle < -180) {
            offest_angle = 360 - abs(offest_angle);
            if (offest_angle < 23) {
                head_string = "向前";
            } else if (offest_angle > 150) {
                head_string = "向后";
            } else {
                head_string = "向左";
            }
        }
        return head_string;
    }


    /**
     * 2017/11/29  GaoQiong 将手机朝向转换为 1180 180  0为正北
     */
    public static float getPhoneCurrentDegree(float x) {
        float currentDegree = 0f;
        x %= 360.0F;
        if (x > 180.0F)
            x -= 360.0F;
        else if (x < -180.0F)
            x += 360.0F;
        currentDegree = Float.isNaN(x) ? 0 : x;
        return currentDegree;

    }


    /**
     * 2017/11/2  GaoQiong  导航 匹配本地收藏的兴趣点  如果距离小于10就取出
     */
    public static ExcelBean searchPoi(double mAmapLatitude, double mAmapLongitude, ArrayList<ExcelBean> poiInfoList, int index) {
        String poiString = "";
        String poiDirection = "";
        int distance = 30;
        int mDistance = 200;
        ExcelBean mPoinfo = null;


        if (index == 0) {

            //第一次从所有的点中取最近的
            for (ExcelBean info : poiInfoList) {
                double poiLat = info.getLatitude();
                double poiLng = info.getLongitude();
                distance = (int) AMapUtils.calculateLineDistance(new LatLng(poiLat, poiLng), new LatLng(mAmapLatitude, mAmapLongitude));

                if (distance < mDistance) {
                    mDistance = distance;
                    mPoinfo = info;
                }
            }

        } else {

            //以后每次都去判断是否到达下一个点

            ExcelBean excelBean = poiInfoList.get(index + 1);

            double poiLat = excelBean.getLatitude();
            double poiLng = excelBean.getLongitude();
            distance = (int) AMapUtils.calculateLineDistance(new LatLng(poiLat, poiLng), new LatLng(mAmapLatitude, mAmapLongitude));

            if (distance < mDistance) {
                mDistance = distance;
                mPoinfo = excelBean;
            }

        }


        if (mDistance < 10) {
            return mPoinfo;
        } else {
            return null;
        }


    }
}
