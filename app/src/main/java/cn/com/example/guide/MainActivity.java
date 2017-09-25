package cn.com.example.guide;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MainActivity extends AppCompatActivity implements TraceListener {

    boolean isCorrrection=false;/**默认全局纠偏为false*/
    AMap aMap;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView= (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        aMap=mapView.getMap();
        Task task = new Task();
        task.execute(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "gdmap/三驰到304.txt" );
        drawMap();

    }

    private void drawMap() {
        if (isCorrrection) {
            CorrrectionTask task = new CorrrectionTask();
            task.execute(gpsdata);
        } else {

        }
    }
    List<TraceLocation> traceList;

    @Override
    public void onRequestFailed(int i, String s) {

    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {

    }

    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int waitingtime) {
    }

    class CorrrectionTask extends AsyncTask<List<GPSData>, Void, List<TraceLocation>> {

        @Override
        protected List<TraceLocation> doInBackground(List<GPSData>... params) {
            traceList = new ArrayList<>();
            for (GPSData gpsData : params[0]) {
                TraceLocation location = new TraceLocation();
                LatLng ll = gpsData.getLatLng();
                CoordinateConverter converter = new CoordinateConverter(MainActivity.this);
                converter.from(CoordinateConverter.CoordType.GPS);
                converter.coord(ll);
                // 执行转换操作
                LatLng desLatLng = converter.convert();
                location.setLongitude(desLatLng.longitude);
                location.setLatitude(desLatLng.latitude);
                location.setSpeed(gpsData.getSpeed() / 10);
                location.setBearing(gpsData.getBearing());
                location.setTime(gpsData.getLoctime());
                traceList.add(location);
            }
            return traceList;
        }

        @Override
        protected void onPostExecute(List<TraceLocation> traceLocations) {
            super.onPostExecute(traceLocations);
            if (traceLocations != null && traceLocations.size() > 0) {
                traceGrasp(traceLocations);
            }
        }
    }
    LBSTraceClient client;
    int mSequenceLineID=1000;
    List<LatLng>latlngs;
    ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<>();
    private void traceGrasp(List<TraceLocation> traceLocations) {
        TraceOverlay mTraceOverlay = new TraceOverlay(aMap);
        mOverlayList.put(mSequenceLineID, mTraceOverlay);
        latlngs = traceLocationToMap(traceLocations);
        mTraceOverlay.setProperCamera(latlngs);
        client = new LBSTraceClient(MainActivity.this);
        //纠偏实现
        client.queryProcessedTrace(mSequenceLineID, traceLocations,
                LBSTraceClient.TYPE_AMAP, this);
    }

    //提取待纠偏数据中的经纬度
    private List<LatLng> traceLocationToMap(List<TraceLocation> traceLocations) {
        List<LatLng> mapList = new ArrayList<>();
        for (TraceLocation location : traceLocations) {
            LatLng latlng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mapList.add(latlng);
        }
        return mapList;
    }


    List<GPSData>gpsdata;

    class Task extends AsyncTask<String, Void, List<TraceLocation>>{

        @Override
        protected List<TraceLocation> doInBackground(String... params) {
            gpsdata=getTraceData(params[0],MainActivity.this);
            return null;
        }
    }
    static long time = 1487583761000L;
    public static List<GPSData> getTraceData(String path, Context context) {
        List<GPSData> result = new ArrayList<>();
        List<String> lath = new ArrayList<>();
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            try {
                InputStream inputstream = new FileInputStream(file);
                if (inputstream != null) {
                    InputStreamReader inputstreamreader = new InputStreamReader(inputstream, "UTF-8");
                    BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                    String line;
                    while ((line = bufferedreader.readLine()) != null) {
                        lath.add(line);
                    }
                    inputstream.close();
                    inputstreamreader.close();
                    bufferedreader.close();
                }
            } catch (IOException e) {
            }
            if (lath != null && lath.size() > 0) {
                for (int i = 0; i < lath.size(); i++) {
                    String[] split = lath.get(i).split(" ");
                    if (split.length < 10) {
                        return null;
                    } else {
                        // 经度
                        double lon = Double.parseDouble(split[0]);
                        // 纬度
                        double lat = Double.parseDouble(split[1]);
                        // 速度
                        float speed = Float.parseFloat(split[3]);
                        // 高度
                        float high = Float.parseFloat(split[4]);
                        // 航向
                        float bearing = Float.parseFloat(split[2]) / 10;
                        //俯仰
                        float pitch = Float.parseFloat(split[5]);
                        //横滚
                        float rool = Float.parseFloat(split[6]);
                        //颠簸
                        int bump = Integer.parseInt(split[7]);
                        //侧翻
                        float cartwheel = Float.parseFloat(split[8]);
                        //危险驾驶
                        byte traffic = Byte.parseByte(split[9]);
                        time += 1000;
                        result.add(new GPSData(lon, lat, speed, high, bearing, pitch, rool, bump, cartwheel, traffic, time, null));
                    }
                }
            }
            return result;
        }
        return null;
    }


}
