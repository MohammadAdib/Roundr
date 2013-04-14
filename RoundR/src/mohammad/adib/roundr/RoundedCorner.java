package mohammad.adib.roundr;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class RoundedCorner extends StandOutWindow {

	public static final String ACTION_SETTINGS = "SETTINGS";
	public static final int REFRESH_CODE = 1;
	public static final int NOTIFICATION_CODE = 2;
	public static final int SHOW_CODE = 3;
	public static final int CLOSE_CODE = 4;
	public static boolean running = false;

	@Override
	public String getAppName() {
		return "Swipe Detector";
	}

	@Override
	public int getAppIcon() {
		return R.drawable.r;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ImageView v = (ImageView) inflater.inflate(R.layout.corner, frame, true).findViewById(R.id.iv);
		v.setImageDrawable(getResources().getDrawable(R.drawable.topleft));
		switch (id) {
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

	private int pxFromDp(float dp) {
		return (int) (dp * getResources().getDisplayMetrics().density);
	}

	/**
	 * Corners: 0 = top left; 1 = top right; 2 = bottom left; 3 = bottom right;
	 */
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		int size = pxFromDp(prefs.getInt("radius", 8));
		switch (id) {
		case 0:
			return new StandOutLayoutParams(id, size, size, 0, 0, 1, 1);
		case 1:
			return new StandOutLayoutParams(id, size, size, StandOutLayoutParams.RIGHT, 0, 1, 1);
		case 2:
			return new StandOutLayoutParams(id, size, size, 0, StandOutLayoutParams.RIGHT, 1, 1);
		case 3:
			return new StandOutLayoutParams(id, size, size, StandOutLayoutParams.BOTTOM, StandOutLayoutParams.RIGHT, 1, 1);
		}
		return new StandOutLayoutParams(id, size, size, 0, 0, 1, 1);
	}

	@Override
	public int getFlags(int id) {
		return super.getFlags(id) | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE;
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "Tap to configure";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return new Intent(this, RoundedCorner.class).putExtra("id", id).setAction(ACTION_SETTINGS);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			int id = intent.getIntExtra("id", DEFAULT_ID);

			// this will interfere with getPersistentNotification()
			if (id == ONGOING_NOTIFICATION_ID) {
				throw new RuntimeException("ID cannot equals StandOutWindow.ONGOING_NOTIFICATION_ID");
			}

			if (ACTION_SHOW.equals(action) || ACTION_RESTORE.equals(action)) {
				show(id);
			} else if (ACTION_SETTINGS.equals(action)) {
				Intent intentS = new Intent(this, SettingsActivity.class);
				intentS.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intentS);
			} else if (ACTION_HIDE.equals(action)) {
				hide(id);
			} else if (ACTION_CLOSE.equals(action)) {
				close(id);
			} else if (ACTION_CLOSE_ALL.equals(action)) {
				closeAll();
			} else if (ACTION_SEND_DATA.equals(action)) {
				if (isExistingId(id) || id == DISREGARD_ID) {
					Bundle data = intent.getBundleExtra("wei.mark.standout.data");
					int requestCode = intent.getIntExtra("requestCode", 0);
					@SuppressWarnings("unchecked")
					Class<? extends StandOutWindow> fromCls = (Class<? extends StandOutWindow>) intent.getSerializableExtra("wei.mark.standout.fromCls");
					int fromId = intent.getIntExtra("fromId", DEFAULT_ID);
					onReceiveData(id, requestCode, data, fromCls, fromId);
				}
			}
		}
		return START_NOT_STICKY;
	}

	@Override
	public boolean onClose(final int id, final Window window) {
		running = false;
		return false;
	}

	@Override
	public String getPersistentNotificationTitle(int id) {
		return "Rounded Corners";
	}

	@Override
	public boolean onShow(final int id, final Window window) {
		running = true;
		if (id == 0) {
			new Thread(new Runnable() {

				private int lastO;

				@Override
				public void run() {
					while (true) {
						int o = getResources().getConfiguration().orientation;
						try {
							if (lastO != o) {
								sendData(id, RoundedCorner.class, id, REFRESH_CODE, new Bundle());
							}
							Thread.sleep(500);
						} catch (Exception e) {
							e.printStackTrace();
						}
						lastO = o;
					}
				}

			}).start();
		}
		return false;
	}

	@Override
	public void onReceiveData(int id, int requestCode, Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Window window = getWindow(id);
		if (requestCode == REFRESH_CODE) {
			updateViewLayout(0, getParams(0, window));
			updateViewLayout(1, getParams(1, window));
			updateViewLayout(2, getParams(2, window));
			updateViewLayout(3, getParams(3, window));
		} else if (requestCode == NOTIFICATION_CODE) {
			if (!prefs.getBoolean("notification", true)) {
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = getPersistentNotification(id);
				notification.icon = R.drawable.nothing;
				mNotificationManager.notify(getClass().hashCode() + ONGOING_NOTIFICATION_ID, notification);
			} else {
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = getPersistentNotification(id);
				mNotificationManager.notify(getClass().hashCode() + ONGOING_NOTIFICATION_ID, notification);
			}
		}
	}
}
