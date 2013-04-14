package mohammad.adib.roundr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	public static boolean boot_up = false;

	boolean boot = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		boot_up = true;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boot = prefs.getBoolean("boot", true);
		Log.d("boot", boot + "");
		if (boot) {
			Intent i = new Intent(context, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
