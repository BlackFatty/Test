package cn.com.example.guide;

import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.TraceLocation;

/**
 * 数据格式： 经度， 纬度， 速度（此数值除以10后的单位为‘m/s’），
 * 高度， 航向(此数值除以10后的单位为‘度’)， 俯仰角（单位为‘度’），
 * 横滚角（单位为‘度’）， 颠簸， 侧翻等级， 危险驾驶
 */
public class GPSData {
    //经度
    double lon;
    //纬度
    double lat;
    //速度
    float speed;
    //高度
    float high;
    //航向
    float bearing;
    //俯仰
    float pitch;
    //横滚
    float rool;
    //颠簸
    int bump;
    //侧翻
    float cartwheel;
    //危险驾驶
    byte traffic;
    //时间
    long loctime;

    LatLng latLng;

    public GPSData() {
        super();
    }

    public GPSData(double lon, double lat, float speed, float high, float bearing, float pitch, float rool, int bump, float cartwheel, byte traffic, long loctime, LatLng latLng) {
        this.lon = lon;
        this.lat = lat;
        this.speed = speed;
        this.high = high;
        this.bearing = bearing;
        this.pitch = pitch;
        this.rool = rool;
        this.bump = bump;
        this.cartwheel = cartwheel;
        this.traffic = traffic;
        this.loctime = loctime;
        this.latLng = latLng;
    }


    public double getLon() {
        return this.lon/ Math.pow(10, 6);
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return this.lat/ Math.pow(10, 6);
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public float getSpeed() {
        return speed/10;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getBearing() {
        return bearing;//2017.5.8 修改
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRool() {
        return rool;
    }

    public void setRool(float rool) {
        this.rool = rool;
    }

    public int getBump() {
        return bump;
    }

    public void setBump(int bump) {
        this.bump = bump;
    }

    public float getCartwheel() {
        return cartwheel;
    }

    public void setCartwheel(float cartwheel) {
        this.cartwheel = cartwheel;
    }

    public byte getTraffic() {
        return traffic;
    }

    public void setTraffic(byte traffic) {
        this.traffic = traffic;
    }

    public long getLoctime() {
        return loctime;
    }

    public void setLoctime(long loctime) {
        this.loctime = loctime;
    }

    public LatLng getLatLng() {
        return new LatLng(getLat(), getLon());
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public TraceLocation GPSDataToTraceLocation() {
        TraceLocation location = new TraceLocation();
        location.setLongitude(this.lon / Math.pow(10, 6));
        location.setLatitude(this.lat / Math.pow(10, 6));
        location.setSpeed(this.speed / 10);
        location.setBearing(this.bearing / 10);
        location.setTime(this.loctime);
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof GPSData)) {
            return false;
        } else {
            GPSData var2 = (GPSData) obj;
            return this.lon==var2.lon&&this.lat==var2.lat;
        }
    }
}
