package org.sksi.batterytemp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * $Id$
 * Copyright (c) SAIC 2012
 */

public class MusicReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
        Log.i( LOGTAG, "Got an intent:  " + intent.getAction() );
    }

    protected static final String LOGTAG = "BATTERYTEMP_" + MusicReceiver.class.getSimpleName();
}
