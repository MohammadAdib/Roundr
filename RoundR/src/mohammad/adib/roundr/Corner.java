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
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;

public class Corner extends StandOutWindow {

	/**
	 * The individual floating window that sits at one of the corners of the
	 * screen. Window ID corresponds to which corner this goes to.
	 * 
	 * @author Mohammad Adib <m.a.adib96@gmail.com>
	 * 
	 *         Contributors: Mark Wei, Jan Metten
	 * 
	 */

	public static final String ACTION_SETTINGS = "SETTINGS";
	public static final String BCAST_CONFIGCHANGED = "android.intent.action.CONFIGURATION_CHANGED";
	public static final int UPDATE_CODE = 2;
	public static final int NOTIFICATION_CODE = 3;
	public static final int wildcard = 0;
	private SharedPreferences prefs;
	public static boolean running = false;

	@Override
	public String getAppName() {
		return "RoundR";
	}

	@Override
	public int getAppIcon() {
		return R.drawable.r_icon;
	}

	@Override
	public void createAndAttachView(int corner, FrameLayout frame) {
		// Set the image based on window corner
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ImageView v = (ImageView) inflater.inflate(R.layout.corner, frame, true).findViewById(R.id.iv);
		// Top left by default
		v.setImageDrawable(getResources().getDrawable(R.drawable.topleft));
		switch (corner) {
		case 1:
			v.setImageDrawable(getResources().getDrawable(R.drawable.topright));
			break;
		case 2:
			v.setImageDrawable(getResources().getDrawable(R.drawable.bottomleft));
			break;
		case 3:
			v.setImageDrawable(getResources().getDrawable(R.drawable.bottomright));
			break;
		}
	}

	private int pxFromDp(double dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}

	/**
	 * Corners: 0 = top left; 1 = top right; 2 = bottom left; 3 = bottom right;
	 */
	@Override
	public StandOutLayoutParams getParams(int corner, Window window) {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// Check if this corner is enabled
		if (prefs.getBoolean("corner" + corner, true)) {
			int radius = pxFromDp(prefs.getInt("radius", 10));
			// Thanks to Jan Metten for rewriting this based on gravity
			switch (corner) {
			case 0:
				return new StandOutLayoutParams(corner, radius, radius, Gravity.TOP | Gravity.LEFT);
			case 1:
				return new StandOutLayoutParams(corner, radius, radius, Gravity.TOP | Gravity.RIGHT);
			case 2:
				return new StandOutLayoutParams(corner, radius, radius, Gravity.BOTTOM | Gravity.LEFT);
			case 3:
				return new StandOutLayoutParams(corner, radius, radius, Gravity.BOTTOM | Gravity.RIGHT);
			}
		}
		// Outside of screen
		return new StandOutLayoutParams(corner, 1, 1, -1, -1, 1, 1);

	}

	@Override
	public int getFlags(int corner) {
		return super.getFlags(corner) | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
	}

	@Override
	public String getPersistentNotificationMessage(int corner) {
		return "Tap to configure";
	}

	@Override
	public Intent getPersistentNotificationIntent(int corner) {
		return new Intent(this, Corner.class).putExtra("id", corner).setAction(ACTION_SETTINGS);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			int corner = intent.getIntExtra("id", DEFAULT_ID);
			if (corner == ONGOING_NOTIFICATION_ID) {
				throw new RuntimeException("ID cannot equals StandOutWindow.ONGOING_NOTIFICATION_ID");
			}

			if (ACTION_SHOW.equals(action) || ACTION_RESTORE.equals(action)) {
				show(corner);
			} else if (ACTION_SETTINGS.equals(action)) {
				try {
					Intent intentS = new Intent(this, Settings.class);
					intentS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentS);
				} catch (Exception e) {
					// Addressing this issue: http://i.imgur.com/Op9kfy8.png
				}
			} else if (ACTION_HIDE.equals(action)) {
				hide(corner);
			} else if (ACTION_CLOSE.equals(action)) {
				close(corner);
			} else if (ACTION_CLOSE_ALL.equals(action)) {
				closeAll();
			} else if (ACTION_SEND_DATA.equals(action)) {
				if (isExistingId(corner) || corner == DISREGARD_ID) {
					Bundle data = intent.getBundleExtra("wei.mark.standout.data");
					int requestCode = intent.getIntExtra("requestCode", 0);
					@SuppressWarnings("unchecked")
					Class<? extends StandOutWindow> fromCls = (Class<? extends StandOutWindow>) intent.getSerializableExtra("wei.mark.standout.fromCls");
					int fromId = intent.getIntExtra("fromId", DEFAULT_ID);
					onReceiveData(corner, requestCode, data, fromCls, fromId);
				}
			}
		}
		return START_NOT_STICKY;
	}

	@Override
	public boolean onClose(final int corner, final Window window) {
		running = false;
		return false;
	}

	@Override
	public String getPersistentNotificationTitle(int corner) {
		return "Rounded Corners";
	}

	@SuppressLint({ "InlinedApi", "NewApi" })
	@SuppressWarnings("deprecation")
	@Override
	public Notification getPersistentNotification(int id) {
		int icon = getAppIcon();
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getPersistentNotificationTitle(id);
		String contentText = getPersistentNotificationMessage(id);

		Intent notificationIntent = getPersistentNotificationIntent(id);

		PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 4.1+ Low priority notification
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 16) {
			if(prefs.getBoolean("icon", true)){
				Notification.Builder mBuilder =
				        new Notification.Builder(this)
				        .setSmallIcon(getAppIcon())
				        .setContentTitle(contentTitle)
				        .setContentText(contentText)
				        .setPriority(Notification.PRIORITY_MIN)
				        .setContentIntent(contentIntent);
			return mBuilder.build();
			}
			else{
				Notification.Builder mBuilder = 
						new Notification.Builder(this)
						.setContent(new RemoteViews(getPackageName(), R.layout.notification))
						.setSmallIcon(getAppIcon())
						.setContentTitle(contentTitle)
						.setContentText(contentText)
						.setPriority(Notification.PRIORITY_MIN)
						.setContentIntent(contentIntent);
			return mBuilder.build();
			}
		}

		String tickerText = String.format("%s: %s", contentTitle, contentText);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);

		return notification;
	}

	@Override
	public boolean onShow(final int corner, final Window window) {
		running = true;
		return false;
	}

	@Override
	public void onReceiveData(int corner, int requestCode, Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
		Window window = getWindow(corner);
		if (requestCode == UPDATE_CODE) {
			// Update the corners when device is rotated
			updateViewLayout(3, getParams(3, window));
			updateViewLayout(2, getParams(2, window));
			updateViewLayout(1, getParams(1, window));
			updateViewLayout(0, getParams(0, window));
		} else if (requestCode == NOTIFICATION_CODE) {
			if (!prefs.getBoolean("notification", true)) {
				// Hide Notification Icon (for <= Gingerbread devices only)
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = getPersistentNotification(corner);
				notification.icon = R.drawable.nothing;
				mNotificationManager.notify(getClass().hashCode() + ONGOING_NOTIFICATION_ID, notification);
			} else {
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = getPersistentNotification(corner);
				mNotificationManager.notify(getClass().hashCode() + ONGOING_NOTIFICATION_ID, notification);
			}
		}
	}
}
