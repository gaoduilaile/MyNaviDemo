package cn.krvision.mynavidemo;

/**
 * Created by gaoqiong on 2018/3/20
 */

public class JniKit2 {
    public static native boolean transLatLon(double lat, double lon);
    public static native void getRouteInfo(double lat1, double lon1, double lat2, double lon2, int routeDis);
    /*public static native boolean kalmanFilter(double lat, double lon);
    public static native void getNodeCoor(double lat, double lon, double tempLat, double tempLon);
    public static native double getRouteRate();
    public static native double haverSin(double theta);
    public static native int getDistance(double routelat, double routelon, double klat, double klon);
    public static native void getDistance(int x);
    public static native void testd(int x);*/

}
