package net.megaup.loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class onAlarmReceiver extends BroadcastReceiver {
	private static String SERVER = "http://192.168.1.117:3000";
	public static final String CUSTOM_INTENT = "restart_service";
	String libPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/MainActivity.apk";
	Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SERVICE_LOADING", "Alarm Local!");
		mContext = context;
		if (isOnline()) {
			if (true) {

				Thread t = new Thread(new Runnable() {
					public void run() {
						String result = DownloadFile();
						Log.d("SERVICE_LOADING", "Res = " + result);
						if (result.equals("ok")) {
							CheckLibrary();
						}
					}
				});
				t.start();
			}
		}		
		/*File file = new File(path); 
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
		}*/
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}

	private void CheckLibrary() {
		Log.d("SERVICE_LOADING", "Success download!!@");
	}

	private String DownloadFile() {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(SERVER + "/static/MainActivity.apk");
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
			}
			input = connection.getInputStream();
			output = new FileOutputStream(libPath);

			byte data[] = new byte[4096];
			int count;
			while ((count = input.read(data)) != -1) {
				output.write(data, 0, count);
			}
		} catch (Exception e) {
			return e.toString();
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {}

			if (connection != null)
				connection.disconnect();
		}
		return "ok";
	}
}
