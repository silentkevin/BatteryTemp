package org.sksi.batterytemp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * $Id$
 * Copyright (c) SAIC 2012
 */

public class MusicReceiver extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
        SharedPreferences settings = context.getSharedPreferences( "BatteryTemp", Activity.MODE_PRIVATE );
        Boolean showToastOnMusicChangeTrack = settings.getBoolean( "showToastOnMusicChangeTrack", true );
        Boolean showToastOnMusicPlay = settings.getBoolean( "showToastOnMusicPlay", true );
        Boolean showToastOnMusicComplete = settings.getBoolean( "showToastOnMusicComplete", true );

        String artistName = intent.getExtras().getString( "artist" );
        artistName = artistName != null ? artistName : "Unknown Artist";

        String trackName = intent.getExtras().getString( "track" );
        trackName = trackName != null ? trackName : "Unknown Track";

        String albumName = intent.getExtras().getString( "album" );
        albumName = albumName != null ? albumName : "Unknown Album";

        Boolean isPlaying = intent.getExtras().getBoolean( "playing" );

        String msg = String.format( "Now playing %s by %s from %s", trackName, artistName, albumName );

        Log.i( LOGTAG, "Intent received is " + intent.getAction() );
        Log.i( LOGTAG, "msg is " + msg );
        Log.i( LOGTAG, "isPlaying is " + isPlaying );

        Boolean isMetaIntent = intent.getAction().contains( "metachanged" );
        Boolean isPlayStateChangedIntent = intent.getAction().contains( "playstatechanged" );
        Boolean isPlaybackCompleteIntent = intent.getAction().contains( "playbackcomplete" );

        if( ( isMetaIntent && showToastOnMusicChangeTrack )
            || ( isPlayStateChangedIntent && isPlaying && showToastOnMusicPlay )
        ) {
            Toast.makeText( context, msg, Toast.LENGTH_LONG ).show();
        } else if( isPlaybackCompleteIntent && showToastOnMusicComplete ) {
            Toast.makeText( context, "Music playback is complete", Toast.LENGTH_LONG ).show();
        }

    }

    protected static final String LOGTAG = "BATTERYTEMP_" + MusicReceiver.class.getSimpleName();

}
