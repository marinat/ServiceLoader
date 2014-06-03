package net.megaup.loader;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AdsService extends Service {
	IncomingReceiver receiver;
	@Override
	public void onCreate()
	{
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("SERVICE_LOADING", "Simple AD Service");
		Intent i = new Intent(this, onAlarmReceiver.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);	
	    try {
			pendingIntent.send();
		} catch (CanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    IntentFilter intentFilter = new IntentFilter(onAlarmReceiver.CUSTOM_INTENT);
	    receiver = new IncomingReceiver();
	    registerReceiver(receiver, intentFilter);
		return START_STICKY;
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
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
