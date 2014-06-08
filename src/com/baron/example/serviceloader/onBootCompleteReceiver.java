package com.baron.example.serviceloader;

import net.megaup.loader.AdsService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class onBootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) 
            startService(context);
	}
	
	public void startService(Context context) {
		Intent adServiceIntent = new Intent(context, AdsService.class);
		context.startService(adServiceIntent);
	}
}
