package com.baron.example.serviceloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baron.example.servicedeveloper.MainActivity;


import dalvik.system.DexClassLoader;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ServiceLoader extends ListActivity {
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addItem("[ Launch SampleActivity ]", null);
		addItem("[ Default.apk ]", null);
		addItem("External MainActivity", Environment.getExternalStorageDirectory() + "/download/MainActivity.apk");
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_1, new String[] { "title" },
				new int[] { android.R.id.text1 });
		setListAdapter(adapter);
		
		ActivityManager manager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.d("processes", service.service.getClassName());
        }
	}
	
	private void addItem(String title, String path) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("title", title);
		map.put("path", path);
		data.add(map);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (position == 0) {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			return;
		}
		if (position == 1) {
			MyApplication.CUSTOM_LOADER = null;
			return;
		}
		Map<String, String> item = data.get(position);
		String title = item.get("title");
		String path = item.get("path");

		try {
			DexClassLoader dcl = new DexClassLoader(path,
					getFilesDir().getAbsolutePath(), null,
					MyApplication.ORIGINAL_LOADER.getParent());
			MyApplication.CUSTOM_LOADER = dcl;

			Toast.makeText(this, title + " loaded, try launch again",
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(this, "Unable to load " + title, Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
			MyApplication.CUSTOM_LOADER = null;
		}
	}
}
