package com.td.util;

/**
 *  经纬度工具
 */
public class LatLonUtil {
    private static double PI = 3.14159265;
    private static double EARTH_RADIUS = 6378137;
    private static double RAD = Math.PI / 180.0;

    public static void main(String[] args) {
        double[] doubles = GetAround(113.883080, 22.553290, 1000);
        System.out.println(doubles);
    }

    /// <summary>
    /// 根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
    /// </summary>
    /// <param name="lat">纬度</param>
    /// <param name="lon">经度</param>
    /// <param name="raidus">半径(米)</param>
    /// <returns></returns>
    public static double[] GetAround(double lat, double lon, int raidus)
    {

        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[] { minLat, minLng, maxLat, maxLng };
    }

    /// <summary>
    /// 根据提供的两个经纬度计算距离(米)
    /// </summary>
    /// <param name="lng1">经度1</param>
    /// <param name="lat1">纬度1</param>
    /// <param name="lng2">经度2</param>
    /// <param name="lat2">纬度2</param>
    /// <returns></returns>
    public static double GetDistance(double lng1, double lat1, double lng2, double lat2)
    {
        double radLat1 = lat1 * RAD;
        double radLat2 = lat2 * RAD;
        double a = radLat1 - radLat2;
        double b = (lng1 - lng2) * RAD;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
}
