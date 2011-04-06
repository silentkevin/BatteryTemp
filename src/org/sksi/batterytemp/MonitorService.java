/*
 * This source and everything with it are licensed under the Apache 2.0 license.
 * For a copy of the license see the LICENSE file in the root of this project or
 *   go to:  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.sksi.batterytemp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class MonitorService extends Service {

    @Override
    public IBinder onBind( Intent arg0 ) {
        // TODO Auto-generated method stub
        return null;
    }


    public static MonitorService getInstance() {
        return instance;
    }


    public void onCreate() {
        super.onCreate();
        this.startMonitor();
        instance = this;
    }


    private void startMonitor() {
        Log.i( LOGTAG, "Starting monitor service" );
        // Restore preferences
        this.settings = this.getSharedPreferences( "BatteryTemp", Activity.MODE_PRIVATE );
        this.tempForegroundWhite = this.settings.getBoolean( "tempForegroundWhite", true );
        this.startOnBoot = this.settings.getBoolean( "startOnBoot", true );

        this.init();
        return;
    }


    private void showNotification() {
        this.showNotification( this.lastLevel, this.lastTemp, this.lastVoltage, this.lastStrHealth, this.lastStrStatus, this.lastOutStr );
    }


    private void showNotification( int level, float temp, float voltage, String strHealth, String strStatus, String fullOutStr ) {
        NotificationManager nm = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );
        
        int icon = R.drawable.batterylevel_black;
        if( this.tempForegroundWhite ) {
            icon = R.drawable.batterylevel_white;
        }
        Notification notification = new Notification( icon, null, System.currentTimeMillis() );
        notification.iconLevel = level;

        Context context = getApplicationContext();
        CharSequence contentTitle = "Level: " + level + "%, Temp: " + temp + "°C";
        CharSequence contentText = "Voltage: " + voltage + "V, " + strHealth + ", " + strStatus;
        Intent notificationIntent = new Intent( this, BatteryTemp.class );
        PendingIntent contentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        notification.iconLevel = (int)temp;
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        notification.setLatestEventInfo( context, contentTitle, contentText, contentIntent );

        nm.cancel( BATTERYTEMP_NOTIFICATION_ID );
        nm.notify( BATTERYTEMP_NOTIFICATION_ID, notification );

        this.lastLevel = level;
        this.lastTemp = temp;
        this.lastVoltage = voltage;
        this.lastStrHealth = strHealth;
        this.lastStrStatus = strStatus;
        this.lastOutStr = fullOutStr;

        if( callback != null ) {
            Log.i( LOGTAG, "Calling callback." );
            callback.receiveBatteryInfo( level, temp, voltage, strHealth, strStatus, fullOutStr );
        } else {
            Log.i( LOGTAG, "No callback registered." );
        }

    }


    public void switchColor( View view ) {
        this.stopNotifications();
        if( this.tempForegroundWhite ) {
            this.tempForegroundWhite = false;
        } else {
            this.tempForegroundWhite = true;
        }
        SharedPreferences.Editor editor = this.settings.edit();
        editor.putBoolean( "tempForegroundWhite", this.tempForegroundWhite );
        editor.commit();
        this.showNotification();
    }


    public boolean getStartOnBoot() {
        return this.startOnBoot;
    }


    public void setStartOnBoot( boolean s ) {
        this.startOnBoot = s;
        SharedPreferences.Editor editor = this.settings.edit();
        editor.putBoolean( "startOnBoot", this.startOnBoot );
        editor.commit();
        Log.i( LOGTAG, "Setting start on boot to:  " + this.startOnBoot );
    }


    private void init() {
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

                showNotification( level, temp, voltage, strHealth, strStatus, outStr );
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


    void startNotifications() { 
        this.showNotification();
    }


    void stopNotifications() {
        NotificationManager nm = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );
        nm.cancelAll();
    }


    static String LOGTAG = "MonitorService";


    int lastLevel;
    float lastTemp;
    float lastVoltage;
    String lastStrHealth;
    String lastStrStatus;
    String lastOutStr;
    MonitorServiceCallbackReceiver callback;

    SharedPreferences settings;
    Boolean tempForegroundWhite;
    Boolean startOnBoot;

    private final static int BATTERYTEMP_NOTIFICATION_ID = 1;
    private static MonitorService instance = null;


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        this.stopNotifications();
    }


    public interface MonitorServiceCallbackReceiver {
        public void receiveBatteryInfo( int level, float temp, float voltage, String strHealth, String strStatus, String fullInfoStr );
    }


    public void registerCallback( MonitorServiceCallbackReceiver cb ) {
        this.callback = cb;
    }

    public void unregisterCallback() {
        this.callback = null;
    }
}
