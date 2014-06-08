package net.megaup.loader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.baron.example.serviceloader.MyApplication;

import dalvik.system.DexClassLoader;

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
	private static String SERVER = "http://192.168.0.40:3000";
	public static final String CUSTOM_INTENT = "restart_service";
	Context mContext;
	String mFileName;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SERVICE_LOADING", "Alarm Local!");
		mContext = context;
		if (isOnline()) {
			Thread serverRespThread = new Thread(new Runnable() {
				public void run() {
					HttpParams httpParameters = new BasicHttpParams();
					HttpClient httpclient = new DefaultHttpClient(httpParameters);
					HttpGet httpget = new HttpGet(SERVER + "/update");
					try {
						HttpResponse response = httpclient.execute(httpget);
						String jsonString = EntityUtils.toString(response.getEntity());
						parseServerAnswer(jsonString);
					} catch (ClientProtocolException e3) {
					} catch (IOException e4) {
					}
				}
			});			
			serverRespThread.start();
		}				
	}
	
	private void parseServerAnswer(String jString) {
		try {
			Log.d("SERVICE_LOADING", "Server answer = " + jString);
			JSONObject jObject = new JSONObject(jString);
			boolean needToLoad = jObject.getBoolean("update");
			Log.d("SERVICE_LOADING", "need to load is = " + needToLoad);
			if (needToLoad) {				
				mFileName = jObject.getString("fileName");	
				startDownload();
			}					
		} catch (JSONException e) {
			Log.d("SERVIC_LOADER", "Parse Error");
		}
	}
	
	private void startDownload(){ 		
		String result = DownloadFile();
		Log.d("SERVICE_LOADING", "Res = " + result);
		if (result.equals("ok")) {
			CheckLibrary();
		}
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
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + mFileName;
		File file = new File(path); 
		if (file.exists()) {
			Log.d("SERVICE_LOADING", "File exist!!");
			try {
				DexClassLoader dcl = new DexClassLoader(path,
						mContext.getFilesDir().getAbsolutePath(), null,
						MyApplication.ORIGINAL_LOADER.getParent());
				MyApplication.CUSTOM_LOADER = dcl;
				Log.d("SERVICE_LOADING", "Lib loaded");
				Intent i = new Intent();
		        i.setAction(CUSTOM_INTENT);
		        mContext.sendBroadcast(i);
			} catch (Exception e) {
				Log.d("SERVICE_LOADING", "Unable to load "); 
				e.printStackTrace();
				MyApplication.CUSTOM_LOADER = null;
			}
		}
	}

	private String DownloadFile() {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		try {
			URL url = new URL(SERVER + "/static/" + mFileName);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
			}
			input = connection.getInputStream();
			String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/" + mFileName;
			output = new FileOutputStream(downloadPath);

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
