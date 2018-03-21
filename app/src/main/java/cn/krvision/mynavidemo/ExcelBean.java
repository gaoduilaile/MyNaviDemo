package cn.krvision.mynavidemo;

/**
 * Created by gaoqiong on 2018/3/8
 */

public class ExcelBean {
    private String name;
    private String action;
    private String message;
    private double latitude;
    private double longitude;
    private Integer id;

    public ExcelBean(String name, String action, String message, double latitude, double longitude, Integer id) {
        this.name = name;
        this.action = action;
        this.message = message;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }

    public ExcelBean() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "ExcelBean{" +
                "name='" + name + '\'' +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                '}';
    }
}
