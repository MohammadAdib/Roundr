package mohammad.adib.roundr;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;

public class MainActivity extends Activity {

	/**
	 * Main Activity that launches the 4 floating windows (corners)
	 * 
	 * @author Mohammad Adib <m.a.adib96@gmail.com>
	 * 
	 *         Contributors: Mark Wei
	 * 
	 */

	ProgressDialog progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!BootReceiver.boot_up) {
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
		}
		StandOutWindow.show(this, RoundedCorner.class, 0);
		StandOutWindow.show(this, RoundedCorner.class, 1);
		StandOutWindow.show(this, RoundedCorner.class, 2);
		StandOutWindow.show(this, RoundedCorner.class, 3);
		finish();
	}

}
