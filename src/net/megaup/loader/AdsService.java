package net.megaup.loader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AdsService extends Service {
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
		return super.onStartCommand(intent, flags, startId);
	}

}
