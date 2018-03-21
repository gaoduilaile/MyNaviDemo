package cn.krvision.mynavidemo;

/**
 * Created by gaoqiong on 2018/3/21
 */

public class JniKit3 {
    static{
        System.load("gps");
    }
    public static native double add(double lat, double lon);
}
