#pragma once

struct KalmanInfo
{
    double filterValue;
    double kalmanGain;
    double kA;
    double kH;
    double kP;
    double kQ;
    double kR;
};

struct RouteInfo
{
    double startLat;
    double startLon;
    double endLat;
    double endLon;
    int directionAngle;
    int routeDistance;
};

class CGPSDataProcess
{
public:
    CGPSDataProcess();
    ~CGPSDataProcess();

    void getRouteInfo(double lat1, double lon1, double lat2, double lon2, int routeDis);
    void getNodeCoor(double *lat, double *lon, double &tempLat, double &tempLon);
    bool kalmanFilter(double *lat, double *lon);
    bool transLatLon(double *lat, double *lon);
    double getRouteRate();
    double haverSin(double theta);
    int getDistance(double routelat, double routelon, double klat, double klon);
    RouteInfo routeInfo;
private:
    KalmanInfo latInfo;
    KalmanInfo lonInfo;

    bool inited;
};