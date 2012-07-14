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
        if( intent == null ) {
            Log.e( LOGTAG, "Received a null intent." );
            return;
        }
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

    protected static final String LOGTAG = "BATTERYTEMP_" + BootReceiver.class.getSimpleName();

}
