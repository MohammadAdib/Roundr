package mohammad.adib.roundr;

/**
 * Copyright 2013 Mohammad Adib
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Settings extends Activity {

	public boolean running = false;

	/**
	 * Settings Dialog Theme can be changed via Manifest
	 * 
	 * @author Mohammad Adib <m.a.adib96@gmail.com>
	 * 
	 *         Contributors: Mark Wei
	 * 
	 * 
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		running = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		// Read saved preferences
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean boot = prefs.getBoolean("boot", true);
		boolean notification = prefs.getBoolean("notification", true);
		boolean isIconOn = prefs.getBoolean("icon", false);
		int radius = prefs.getInt("radius", 10);
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
		CheckBox iconCB = (CheckBox) findViewById(R.id.iconCB);
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
						new AlertDialog.Builder(Settings.this).setTitle("Required by Android").setMessage("The notification prevents Android from killing RoundR in low memory situations.\n\nOn Android 4.1+ devices, it can be disabled via the RoundR App Info.").setPositiveButton("Go to RoundR App Info", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								showInstalledAppDetails("mohammad.adib.roundr");
							}
						}).show();
					} else { // below 4.1
						new AlertDialog.Builder(Settings.this).setTitle("Required by Android").setMessage("The notification prevents Android from killing RoundR in low memory situations.\n\nOnly on Android 4.1+ devices, it can be disabled via the RoundR App Info. Unfortunately, your device is not running updated firmware. Check for updates with your carrier.").setNeutralButton("Make the icon invisible", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								prefs.edit().putBoolean("notification", false).commit();
								StandOutWindow.sendData(Settings.this, Corner.class, 0, Corner.NOTIFICATION_CODE, new Bundle(), Corner.class, 0);
							}
						}).show();
					}
				}
				StandOutWindow.sendData(Settings.this, Corner.class, 0, Corner.NOTIFICATION_CODE, new Bundle(), Corner.class, 0);
			}

		});
		
		iconCB.setChecked(isIconOn);
		iconCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				prefs.edit().putBoolean("icon",isChecked).commit();
			}
		});
		
		radiusSB.setProgress(radius - 2);
		radiusTV.setText("Corner Radius: " + pxFromDp(radius));
		radiusSB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				prefs.edit().putInt("radius", seekBar.getProgress() + 2).commit();
				radiusTV.setText("Corner Radius: " + pxFromDp(seekBar.getProgress() + 2));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				refresh();
			}

		});
		ToggleButton tb = (ToggleButton) findViewById(R.id.quitTB);
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					StandOutWindow.show(Settings.this, Corner.class, 0);
					StandOutWindow.show(Settings.this, Corner.class, 1);
					StandOutWindow.show(Settings.this, Corner.class, 2);
					StandOutWindow.show(Settings.this, Corner.class, 3);
				} else {
					StandOutWindow.closeAll(Settings.this, Corner.class);
				}
			}

		});
		// Fun animation
		findViewById(R.id.rl).startAnimation(AnimationUtils.loadAnimation(this, R.anim.show));
	}

	@SuppressLint("InlinedApi")
	public void showInstalledAppDetails(String packageName) {
		Intent intent = new Intent();
		intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", packageName, null);
		intent.setData(uri);
		startActivity(intent);
	}

	private int pxFromDp(double dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
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
		StandOutWindow.sendData(Settings.this, Corner.class, Corner.wildcard, Corner.UPDATE_CODE, new Bundle(), Corner.class, StandOutWindow.DISREGARD_ID);
	}

}
