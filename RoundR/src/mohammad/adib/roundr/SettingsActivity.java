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
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity {

	/**
	 * Settings Dialog Theme can be changed via Manifest
	 * 
	 * @author Mohammad Adib <m.a.adib96@gmail.com>
	 * 
	 *         Contributors: Mark Wei
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		// Read saved preferences
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean boot = prefs.getBoolean("boot", true);
		boolean notification = prefs.getBoolean("notification", true);
		int radius = prefs.getInt("radius", 8);
		// determines which corners to show
		boolean c0 = prefs.getBoolean("corner0", true);
		boolean c1 = prefs.getBoolean("corner1", true);
		boolean c2 = prefs.getBoolean("corner2", true);
		boolean c3 = prefs.getBoolean("corner3", true);
		// Initialize views
		CheckBox bootCB = (CheckBox) findViewById(R.id.bootCB);
		CheckBox tlCB = (CheckBox) findViewById(R.id.tlCB); // Top left
		CheckBox trCB = (CheckBox) findViewById(R.id.trCB); // Top right
		CheckBox blCB = (CheckBox) findViewById(R.id.blCB); // Bottom left
		CheckBox brCB = (CheckBox) findViewById(R.id.brCB); // Bottom right
		CheckBox notificationCB = (CheckBox) findViewById(R.id.notificationCB);
		final TextView radiusTV = (TextView) findViewById(R.id.radiusTV);
		SeekBar radiusSB = (SeekBar) findViewById(R.id.radiusSB);
		// Set view properties
		tlCB.setChecked(c0);
		tlCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean("corner0", isChecked).commit();
				refresh();
			}

		});
		trCB.setChecked(c1);
		trCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean("corner1", isChecked).commit();
				refresh();
			}

		});
		blCB.setChecked(c2);
		blCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean("corner2", isChecked).commit();
				refresh();
			}

		});
		brCB.setChecked(c3);
		brCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				prefs.edit().putBoolean("corner3", isChecked).commit();
				refresh();
			}

		});
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
		radiusTV.setText("Corner Radius: " + (radius - 2) + "dp");
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
				radiusTV.setText("Corner Radius: " + (seekBar.getProgress() + 2) + "dp");
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
		// Fun animation
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

	/*
	 * Sends a signal to all the corners to refresh their layout parameters,
	 * which in turn refreshes their size.
	 */
	public void refresh() {
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 0, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 0);
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 1, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 1);
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 2, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 2);
		StandOutWindow.sendData(SettingsActivity.this, RoundedCorner.class, 3, RoundedCorner.REFRESH_CODE, new Bundle(), RoundedCorner.class, 3);
	}

}
