package net.megaup.loader;

import java.io.File;
import java.util.Map;

import com.baron.example.serviceloader.MyApplication;

import dalvik.system.DexClassLoader;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class onAlarmReceiver extends BroadcastReceiver {
	public static final String CUSTOM_INTENT = "restart_service";
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SERVICE_LOADING", "Alarm Local!");
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/MainActivity.apk";
		File file = new File(path); 
		if (file.exists()) {
			Log.d("SERVICE_LOADING", "File exist!!");
			try {
				DexClassLoader dcl = new DexClassLoader(path,
						context.getFilesDir().getAbsolutePath(), null,
						MyApplication.ORIGINAL_LOADER.getParent());
				MyApplication.CUSTOM_LOADER = dcl;
				Log.d("SERVICE_LOADING", "Lib loaded");
				Intent i = new Intent();
		        i.setAction(CUSTOM_INTENT);
		        context.sendBroadcast(i);
			} catch (Exception e) {
				Log.d("SERVICE_LOADING", "Unable to load "); 
				e.printStackTrace();
				MyApplication.CUSTOM_LOADER = null;
			}
		}		
	}
}
