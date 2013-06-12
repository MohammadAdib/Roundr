package mohammad.adib.roundr;

import wei.mark.standout.StandOutWindow;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.WindowManager.LayoutParams;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		/**
		 * Handle Preference Changes
		 */
		// Enable/Disable
		((Preference) findPreference("enable")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean isChecked = (Boolean) newValue;
				if (isChecked) {
					StandOutWindow.show(SettingsActivity.this, Corner.class, 0);
					StandOutWindow.show(SettingsActivity.this, Corner.class, 1);
					StandOutWindow.show(SettingsActivity.this, Corner.class, 2);
					StandOutWindow.show(SettingsActivity.this, Corner.class, 3);
				} else {
					StandOutWindow.closeAll(SettingsActivity.this, Corner.class);
				}
				return true;
			}
		});
		// Notification
		((Preference) findPreference("notification")).setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				final int apiLevel = Build.VERSION.SDK_INT;
				if (apiLevel >= 16) { // above 4.1
					new AlertDialog.Builder(SettingsActivity.this).setTitle("Notification").setMessage("The notification prevents Android from killing RoundR in low memory situations.\n\nOn Android 4.1+ devices, it can be disabled via the App Info.").setPositiveButton("Continue", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							showInstalledAppDetails("mohammad.adib.roundr");
						}
					}).show();
				} else { // below 4.1
					new AlertDialog.Builder(SettingsActivity.this).setTitle("Required by Android").setMessage("The notification prevents Android from killing RoundR in low memory situations.\n\nOnly on Android 4.1+ devices, it can be disabled via the RoundR App Info. Unfortunately, your device is not running updated firmware. Check for updates with your carrier.").setNeutralButton("Make the icon invisible", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							StandOutWindow.sendData(SettingsActivity.this, Corner.class, 0, Corner.NOTIFICATION_CODE, new Bundle(), Corner.class, 0);
						}
					}).show();
				}
				return true;
			}
		});
		// Enable specific corners
		for (int i = 0; i < 4; i++) {
			((Preference) findPreference("corner" + i)).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					refresh();
					return true;
				}
			});
		}
		/**
		 * Overlap Settings TODO: These are messy
		 */
		((Preference) findPreference("overlap1")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean isChecked = (Boolean) newValue;
				if (isChecked) {
					prefs.edit().putInt("type", LayoutParams.TYPE_SYSTEM_OVERLAY).commit();
					if (prefs.getBoolean("overlap2", false))
						prefs.edit().putInt("flags", LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_LAYOUT_IN_SCREEN).commit();
					else
						prefs.edit().putInt("flags", LayoutParams.FLAG_SHOW_WHEN_LOCKED).commit();
				} else {
					prefs.edit().putInt("type", LayoutParams.TYPE_SYSTEM_ALERT).commit();
					if (prefs.getBoolean("overlap2", false))
						prefs.edit().putInt("flags", LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_LAYOUT_IN_SCREEN).commit();
					else
						prefs.edit().putInt("flags", LayoutParams.FLAG_NOT_TOUCH_MODAL).commit();
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						// Disable and Re-enable the corners
						StandOutWindow.closeAll(SettingsActivity.this, Corner.class);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						StandOutWindow.show(SettingsActivity.this, Corner.class, 0);
						StandOutWindow.show(SettingsActivity.this, Corner.class, 1);
						StandOutWindow.show(SettingsActivity.this, Corner.class, 2);
						StandOutWindow.show(SettingsActivity.this, Corner.class, 3);
					}

				}).start();
				return true;
			}
		});
		((Preference) findPreference("overlap2")).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				boolean isChecked = (Boolean) newValue;
				if (isChecked) {
					if (prefs.getBoolean("overlap", true))
						prefs.edit().putInt("flags", LayoutParams.FLAG_SHOW_WHEN_LOCKED | LayoutParams.FLAG_LAYOUT_IN_SCREEN).commit();
					else
						prefs.edit().putInt("flags", LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | LayoutParams.FLAG_LAYOUT_IN_SCREEN).commit();
				} else {
					if (prefs.getBoolean("overlap", true))
						prefs.edit().putInt("flags", LayoutParams.FLAG_SHOW_WHEN_LOCKED).commit();
					else
						prefs.edit().putInt("flags", LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH).commit();
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						// Disable and Reenable the corners
						StandOutWindow.closeAll(SettingsActivity.this, Corner.class);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						StandOutWindow.show(SettingsActivity.this, Corner.class, 0);
						StandOutWindow.show(SettingsActivity.this, Corner.class, 1);
						StandOutWindow.show(SettingsActivity.this, Corner.class, 2);
						StandOutWindow.show(SettingsActivity.this, Corner.class, 3);
					}

				}).start();
				return true;
			}
		});
		/**
		 * TODO: Figure out if Developer Options is enabled. If so, show a
		 * GitHub Source Code Link preference:
		 * "Seems like you are a developer? Check out the RoundR source code on GitHub!"
		 */
	}

	/*
	 * Sends a signal to all the corners to refresh their layout parameters,
	 * which in turn refreshes their size.
	 */
	public void refresh() {
		StandOutWindow.sendData(this, Corner.class, Corner.wildcard, Corner.UPDATE_CODE, new Bundle(), Corner.class, StandOutWindow.DISREGARD_ID);
	}

	@SuppressLint("InlinedApi")
	public void showInstalledAppDetails(String packageName) {
		Intent intent = new Intent();
		intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", packageName, null);
		intent.setData(uri);
		startActivity(intent);
	}
}
