package net.megaup.loader;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class AdsService extends Service {
	static AlarmManager alarmManager;
	IncomingReceiver receiver;

	@Override
	public void onCreate() {
		Log.d("SERVICE_LOADING", "Simple AD Service");
		Intent intent = new Intent(this, onAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int timeInterval = 30;
		// int timeInterval = 14400;
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeInterval * 1000, timeInterval * 1000, pendingIntent);
		IntentFilter intentFilter = new IntentFilter(onAlarmReceiver.CUSTOM_INTENT);
		receiver = new IncomingReceiver();
		registerReceiver(receiver, intentFilter);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public class IncomingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(onAlarmReceiver.CUSTOM_INTENT)) {
				stopSelf();
				Intent i = new Intent("net.megaup.loader.ADSERVICE");
				startService(i);
			}
		}
	}

	@Override
	public void onDestroy() {
		Log.d("SERVICE_LOADING", "Local Destroy");
		if (receiver != null)
			unregisterReceiver(receiver);
		if (alarmManager != null) {
			Intent intent = new Intent(this, onAlarmReceiver.class);
			alarmManager.cancel(PendingIntent.getBroadcast(this, 0, intent, 0));
		}
		Intent i = new Intent("net.megaup.loader.ADSERVICE");
	    startService(i);
	}

}
