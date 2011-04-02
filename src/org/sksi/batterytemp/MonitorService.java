package org.sksi.batterytemp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MonitorService extends Service {

    @Override
    public IBinder onBind( Intent arg0 ) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onCreate() {
        super.onCreate();
        this.startMonitor();
    }

    private void startMonitor() {
        Log.i( LOGTAG, "Starting monitor service" );
        return;
    }

    static String LOGTAG = "MonitorService";
}
