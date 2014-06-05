package com.baron.example.servicedeveloper;


import net.megaup.loader.AdsService;

import com.baron.example.serviceloader.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
		if (!isServiceRunning(this)) {
			Intent i = new Intent("net.megaup.loader.ADSERVICE");
		    startService(i);
		    Log.d("SERVICE_LOADING", "Run service");
		}		
		else {
			Log.d("SERVICE_LOADING", "Service already runned");
		}
	}

	/** A placeholder fragment containing a simple view. */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}
	
	public static boolean isServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (AdsService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
