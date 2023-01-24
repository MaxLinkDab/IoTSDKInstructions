package com.td.util;

public class LatLngUtil {

    /**
     * 计算两点之间距离
     * @param start
     * @param end
     * @return 千米
     */
    public static double getDistance(LatLng start,LatLng end){

        double lat1 = (Math.PI/180)*start.latitude;
        double lat2 = (Math.PI/180)*end.latitude;

        double lon1 = (Math.PI/180)*start.longitude;
        double lon2 = (Math.PI/180)*end.longitude;

        //地球半径 km单位
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = UtilTool.doubleTodouble(Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R);

        return d;
    }

    //测试
    public static void main(String[] args) {
        LatLng start = new LatLng(23.074856,113.232297);
        LatLng end = new LatLng(23.074876,113.233209);
        System.out.println(LatLngUtil.getDistance(start, end));
    }
}
