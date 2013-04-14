package mohammad.adib.roundr;

import wei.mark.standout.StandOutWindow;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean boot = prefs.getBoolean("boot", true);
		boolean notification = prefs.getBoolean("notification", true);
		int radius = prefs.getInt("radius", 8);
		CheckBox bootCB = (CheckBox) findViewById(R.id.bootCB);
		CheckBox notificationCB = (CheckBox) findViewById(R.id.notificationCB);
		SeekBar radiusSB = (SeekBar) findViewById(R.id.radiusSB);
		bootCB.setChecked(boot);
		bootCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean("boot", isChecked).commit();
			}

		});
		notificationCB.setChecked(notification);
		notificationCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean("notification", true).commit();
				if (!isChecked) {
					final int apiLevel = Build.VERSION.SDK_INT;
					if (apiLevel >= 16) { // above 4.1
						new AlertDialog.Builder(SettingsActivity.this).setTitle("Required by Android").setMessage("The notification prevents Android from killing RoundR in low memory situations.\n\nOn Android 4.1+ devices, it can be disabled via the RoundR App Info.").setNegativeButton("Make the icon invisible", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								prefs.edit().putBoolean("notification", false).commit();
								StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 0, RoundedCorner.NOTIFICATION_CODE, new Bundle(), RoundedCorner.class, 0);
							}
						}).setPositiveButton("App Info", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								showInstalledAppDetails("mohammad.adib.roundr");
							}
						}).show();
					} else { // below 4.1
						new AlertDialog.Builder(SettingsActivity.this).setTitle("Required by Android").setMessage("The notification prevents Android from killing RoundR in low memory situations.\n\nOnly on Android 4.1+ devices, it can be disabled via the RoundR App Info. Unfortunately, your device is not running updated firmware. Check for updates with your carrier.").setNeutralButton("Make the icon invisible", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								prefs.edit().putBoolean("notification", false).commit();
								StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 0, RoundedCorner.NOTIFICATION_CODE, new Bundle(), RoundedCorner.class, 0);
							}
						}).show();
					}
				}
				StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 0, RoundedCorner.NOTIFICATION_CODE, new Bundle(), RoundedCorner.class, 0);
			}

		});
		radiusSB.setProgress(radius - 2);
		radiusSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				prefs.edit().putInt("radius", seekBar.getProgress() + 2).commit();
				refresh();
			}

		});
		ToggleButton tb = (ToggleButton) findViewById(R.id.quitTB);
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					StandOutWindow.show(SettingsActivity.this, RoundedCorner.class, 0);
					StandOutWindow.show(SettingsActivity.this, RoundedCorner.class, 1);
					StandOutWindow.show(SettingsActivity.this, RoundedCorner.class, 2);
					StandOutWindow.show(SettingsActivity.this, RoundedCorner.class, 3);
				} else {
					StandOutWindow.closeAll(SettingsActivity.this, RoundedCorner.class);
				}
			}

		});
		findViewById(R.id.rl).startAnimation(AnimationUtils.loadAnimation(this, R.anim.show));
	}

	@SuppressLint("InlinedApi")
	public void showInstalledAppDetails(String packageName) {
		Intent intent = new Intent();
		intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", packageName, null);
		intent.setData(uri);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	public void refresh() {
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 0, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 0);
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 1, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 1);
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 2, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 2);
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 3, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 3);
	}

}
