package org.sksi.batterytemp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BatteryTemp extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.headerView = (TextView) this.findViewById(R.id.header);
        this.batteryInfoView = (TextView) this.findViewById(R.id.batteryLevel);
        this.footerView = (TextView) this.findViewById(R.id.footer);

        this.headerView.setText( "Hello dearest Karen, who didn't believe I had put an app of my own on my phone." );

        this.batteryInfoView.setText( "Still initting..." );

        this.footerView.setText( "Love, Kevin" );
        this.batteryLevel();
    }
	
    
    private void showNotification( int level, float temp, float voltage, String strHealth, String strStatus ) {
		NotificationManager nm = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

	    Notification notification = new Notification( R.drawable.batterylevel, "Battery Level:  " + level, System.currentTimeMillis() );
	    notification.iconLevel = level;

	    Context context = getApplicationContext();
	    CharSequence contentTitle = "Level: " + level + "%, Temp: " + temp + "°C";
	    CharSequence contentText = "Voltage: " + voltage + "V, " + strHealth + ", " + strStatus;
	    Intent notificationIntent = new Intent( this, BatteryTemp.class );
	    PendingIntent contentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

	    notification.iconLevel = (int)temp;
	    notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

	    notification.setLatestEventInfo( context, contentTitle, contentText, contentIntent );

	    nm.cancel( BATTERYLEVEL_NOTIFICATION_ID );
	    nm.notify( BATTERYLEVEL_NOTIFICATION_ID, notification );
		
	}
	
	
    public void exit( View view ) {
		NotificationManager nm = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );
		nm.cancelAll();
    	this.finish();
    }
    
    private void batteryLevel() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
            	String outStr = "";
            	outStr += "\n";
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                outStr += "Battery Level Remaining: " + level + "%\n";

                float temp = (float)intent.getIntExtra( BatteryManager.EXTRA_TEMPERATURE, -1 ) / 10;
                outStr += "Battery Temperature:  " + temp + " C\n";

                float voltage = (float)intent.getIntExtra( BatteryManager.EXTRA_VOLTAGE, -1 ) / 1000;
                outStr += "Battery Voltage:  " + voltage + "\n";

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                String strStatus;
                if (status == BatteryManager.BATTERY_STATUS_CHARGING){
                	strStatus = "Charging";
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING){
                	strStatus = "Dis-charging";
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
                	strStatus = "Not charging";
                } else if (status == BatteryManager.BATTERY_STATUS_FULL){
                	strStatus = "Full";
                } else {
                	strStatus = "Unknown";
                }
                outStr += "Battery Status:  " + strStatus + "\n";

                int health = intent.getIntExtra( BatteryManager.EXTRA_HEALTH, -1 );
                String strHealth;
                if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
                	strHealth = "Good";
                } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
                	strHealth = "Over Heat";
                } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
                	strHealth = "Dead";
                } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
                	strHealth = "Over Voltage";
                } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
                	strHealth = "Unspecified Failure";
                } else {
                	strHealth = "Unknown";
                }
                outStr += "Battery Health:  " + strHealth + "\n";

                batteryInfoView.setText( outStr );

                showNotification( level, temp, voltage, strHealth, strStatus );
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
        this.batteryInfoView.setText( "Done initting..." );
    }

    TextView headerView;
    TextView batteryInfoView;
    TextView footerView;
    
    private final static int BATTERYLEVEL_NOTIFICATION_ID = 1;
}