package org.sksi.batterytemp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
        // just make sure we are getting the right intent (better safe than sorry)
        if( "android.intent.action.BOOT_COMPLETED".equals( intent.getAction() ) ) {
            SharedPreferences settings = context.getSharedPreferences( "BatteryTemp", Activity.MODE_PRIVATE );
            boolean startOnBoot = settings.getBoolean( "startOnBoot", true );

            if( startOnBoot ) {
                ComponentName comp = new ComponentName( context.getPackageName(), MonitorService.class.getName() );
                ComponentName service = context.startService( new Intent().setComponent( comp ) );
                if( null == service ) {
                    // something really wrong here
                    Log.e( LOGTAG, "Could not start service " + comp.toString() );
                }
                Log.i( LOGTAG, "Starting Monitor Service on bootup." );
            } else {
                Log.i( LOGTAG, "Not starting monitor service on bootup because we are not supposed to." );
            }
        } else {
            Log.e( LOGTAG, "Received unexpected intent " + intent.toString() );   
        }
    }

    static String LOGTAG = "BootReceiver";

}
