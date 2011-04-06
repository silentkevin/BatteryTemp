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

import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class BatteryTemp extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );


        this.headerView = (TextView) this.findViewById(R.id.header);
        this.batteryInfoView = (TextView) this.findViewById(R.id.batteryLevel);
        this.footerView = (TextView) this.findViewById(R.id.footer);
        this.startOnBootCheckbox = (CheckBox)this.findViewById( R.id.startOnBootCheckbox );
        this.startOnBootCheckbox.setEnabled( false );
        this.startOnBootCheckbox.setOnCheckedChangeListener( new OnCheckedChangeListener() {
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                MonitorService.getInstance().setStartOnBoot( isChecked );
            }
        });

        this.headerView.setText( "Hello dearest Karen, who didn't believe I had put an app of my own on my phone." );

        this.batteryInfoView.setText( "Still initting..." );

        this.footerView.setText( "Love, Kevin" );

        Handler tmp = new Handler();
        if( MonitorService.getInstance() == null ) {
            ComponentName comp = new ComponentName( this.getPackageName(), MonitorService.class.getName() );
            ComponentName service = this.startService( new Intent().setComponent( comp ) );
            tmp.postDelayed( DeferredRegisterCallback, 5000 );
            Log.i( LOGTAG, "Monitor service was not running but I started it" );
        } else {
            tmp.postDelayed( DeferredRegisterCallback, 250 );
            Log.i( LOGTAG, "Monitor service was already running, not starting." );
        }
    }


    private Runnable DeferredRegisterCallback = new Runnable() {
        public void run() {
            MonitorService.getInstance().registerCallback( new CallbackReceiver() );
            MonitorService.getInstance().startNotifications();
            startOnBootCheckbox.setEnabled( true );
            if( MonitorService.getInstance().getStartOnBoot() ) {
                startOnBootCheckbox.setChecked( true );
            } else {
                startOnBootCheckbox.setChecked( false );
            }
        }
     };


    private class CallbackReceiver implements MonitorService.MonitorServiceCallbackReceiver {
        public void receiveBatteryInfo( int level, float temp, float voltage,
                String strHealth, String strStatus, String fullInfoStr ) {
            batteryInfoView.setText( fullInfoStr );
        }
    }


    public void exit( View view ) {
        MonitorService.getInstance().unregisterCallback();
        MonitorService.getInstance().stopNotifications();
        MonitorService.getInstance().stopSelf();
        this.finish();
    }


    public void close( View view ) {
        MonitorService.getInstance().unregisterCallback();
        this.finish();
    }


    public void switchColor( View view ) {
        if( MonitorService.getInstance() != null ) {
            MonitorService.getInstance().switchColor( view );
        }
    }


    TextView headerView;
    TextView batteryInfoView;
    TextView footerView;
    CheckBox startOnBootCheckbox;

    private static final String LOGTAG = "BatteryTemp";

}

