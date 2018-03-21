//#pragma once
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include "GPSDataProcess.h"
#include <math.h>
//#include <iostream>
//#include <Windows.h>
//#include <iomanip>

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


const double K_PI = 3.1415926;
const double EARTH_RADIUS = 6371.0;

CGPSDataProcess::CGPSDataProcess()
{
    latInfo.filterValue = 0;
    latInfo.kalmanGain = 0;
    latInfo.kA = 1;
    latInfo.kH = 1;
    latInfo.kP = 1;
    latInfo.kQ = 2;
    latInfo.kR = 300;

    lonInfo.filterValue = 0;
    lonInfo.kalmanGain = 0;
    lonInfo.kA = 1;
    lonInfo.kH = 1;
    lonInfo.kP = 1;
    lonInfo.kQ = 2;
    lonInfo.kR = 100;

    inited = false;
}

CGPSDataProcess::~CGPSDataProcess()
{
    latInfo.kalmanGain = 0;
    latInfo.kP = 1;

    lonInfo.kalmanGain = 0;
    lonInfo.kP = 1;

    inited = false;
}



jboolean Java_cn_krvision_mynavidemo_JniKit2_transLatLon( JNIEnv *env,jobject /* this */,double *lat, double *lon)
{
    if (!kalmanFilter(lat, lon))
    {
        return false;
    }
    double tLat, tLon;
    getNodeCoor(lat, lon, tLat, tLon);
    int tempDis = getDistance(routeInfo.startLat, routeInfo.startLon, tLat, tLon);
    int endDis = getDistance(routeInfo.endLat, routeInfo.endLon, tLat, tLon);

    if (endDis > routeInfo.routeDistance)
    {
        return true;
    }

    if (tempDis > routeInfo.routeDistance)
    {
        tLat = routeInfo.endLat;
        tLon = routeInfo.endLon;
        return false;
    }
    *lat = tLat;
    *lon = tLon;
    return true;
}

jboolean Java_cn_krvision_mynavidemo_JniKit2_kalmanFilter(double *lat, double *lon)
{
    if (!inited)
    {
        latInfo.filterValue = *lat;
        lonInfo.filterValue = *lon;
        inited = true;
        return false;
    }


    double tempLat = latInfo.kA * latInfo.filterValue;
    double tempLon = lonInfo.kA * lonInfo.filterValue;

    latInfo.kP = latInfo.kA * latInfo.kP * latInfo.kA + latInfo.kQ;
    lonInfo.kP = lonInfo.kA * lonInfo.kP * lonInfo.kA + lonInfo.kQ;


    latInfo.kalmanGain = (latInfo.kH * latInfo.kP) / (latInfo.kH * latInfo.kP * latInfo.kH + latInfo.kR);
    lonInfo.kalmanGain = (lonInfo.kH * lonInfo.kP) / (lonInfo.kH * lonInfo.kP * lonInfo.kH + lonInfo.kR);

    tempLat = tempLat + (*lat - latInfo.kH * tempLat) * latInfo.kalmanGain;
    tempLon = tempLon + (*lon - lonInfo.kH * tempLon) * lonInfo.kalmanGain;
    *lat = tempLat;
    *lon = tempLon;

    latInfo.kP = (1 - latInfo.kalmanGain * latInfo.kH) * latInfo.kP;
    lonInfo.kP = (1 - lonInfo.kalmanGain * lonInfo.kH) * lonInfo.kP;
    return true;
}

void*  jboolean Java_cn_krvision_mynavidemo_JniKit2_getRouteInfo(JNIEnv *env,jobject /* this */,double lat1, double lon1, double lat2, double lon2, int routeDis)
{
    routeInfo.startLat = lat1;
    routeInfo.startLon = lon1;
    routeInfo.endLat = lat2;
    routeInfo.endLon = lon2;
    routeInfo.routeDistance = routeDis;
}

void* Java_cn_krvision_mynavidemo_JniKit2_getNodeCoor(JNIEnv *env,jobject /* this */,double *lat, double *lon, double &tempLat, double &tempLon)
{
    double rk = getRouteRate();

    if (rk < 0)
    {
        tempLat = *lat;
        tempLon = routeInfo.endLon;
    }
    if (rk == 0)
    {
        tempLat = routeInfo.endLat;
        tempLon = *lon;
    }

    double tempK = 1 / rk;
    double tempB = *lat - *lon * tempK;

    double rb = routeInfo.endLat - routeInfo.endLon * rk;

    tempLon = (tempB - rb) / (rk - tempK);
    tempLat = tempLon * tempK + tempB;
}

jdouble Java_cn_krvision_mynavidemo_JniKit2_getRouteRate(JNIEnv *env,jobject /* this */,)
{
    double diffLat = routeInfo.endLat - routeInfo.startLat;
    double diffLon = routeInfo.endLon - routeInfo.startLon;

    if (diffLon == 0)
    {
        return -1;
    }

    double routeRate = diffLat / diffLon;
    return routeRate;
}

double haverSin(double theta)
{
    double v = sin(theta / 2);
    return v*v;
}

jint Java_cn_krvision_mynavidemo_JniKit2_getDistance(JNIEnv *env,jobject /* this */,double routelat, double routelon, double klat, double klon)
{
    double lat1 = routelat*K_PI / 180;
    double lon1 = routelon*K_PI / 180;
    double lat2 = klat*K_PI / 180;
    double lon2 = klon*K_PI / 180;

    double diffLat = abs(lat1 - lat2);
    double diffLon = abs(lon1 - lon2);

    double tempH = haverSin(diffLat) + cos(lat1)*cos(lat2)*haverSin(diffLon);
    float distance = 2 * EARTH_RADIUS*asin(sqrt(tempH));
    return int(distance * 1000);
}

