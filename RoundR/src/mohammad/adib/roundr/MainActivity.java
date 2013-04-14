package mohammad.adib.roundr;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;

public class MainActivity extends Activity {

	ProgressDialog progress;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		Intent i = new Intent(this, SettingsActivity.class);
		startActivity(i);
		StandOutWindow.show(this, RoundedCorner.class, 0);
		StandOutWindow.show(this, RoundedCorner.class, 1);
		StandOutWindow.show(this, RoundedCorner.class, 2);
		StandOutWindow.show(this, RoundedCorner.class, 3);
		finish();
	}

}
