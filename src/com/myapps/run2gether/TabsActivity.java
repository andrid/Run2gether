package com.myapps.fitnessApp;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class TabsActivity extends TabActivity {
	public static double lat1,long1,lat2,long2;
	public static int route_id,user_id,competitor_id;
	public static boolean createNewRoute;
	public static int feedback;
	public static String route_name;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);
		
		Intent intent = getIntent();
		lat1=intent.getDoubleExtra("LAT1", 0);
		long1=intent.getDoubleExtra("LONG1", 0);
		lat2=intent.getDoubleExtra("LAT2", 0);
		long2=intent.getDoubleExtra("LONG2", 0);
		user_id=intent.getExtras().getInt("USER_ID");
		competitor_id=intent.getExtras().getInt("COMPETITOR_ID");
		route_id=intent.getExtras().getInt("ROUTE_ID");
		createNewRoute=intent.getExtras().getBoolean("CREATENEWROUTE");
		route_name=intent.getStringExtra("ROUTE_NAME");
		feedback=intent.getExtras().getInt("FEEDBACK");
		
		
			
TabHost tabHost = getTabHost();
		
		//First Tab (Status)
		TabSpec statusSpec = tabHost.newTabSpec("Status");
		// setting Title and Icon for the Tab
		statusSpec.setIndicator("Status");
		Intent statusIntent = new Intent(this, StatusActivity.class );
		statusIntent.putExtra("LAT1", lat1);
		statusIntent.putExtra("LONG1", long1);
		statusIntent.putExtra("LAT2", lat2);
		statusIntent.putExtra("LONG2", long2);
		statusIntent.putExtra("USER_ID", user_id);
		statusIntent.putExtra("COMPETITOR_ID", competitor_id);
		statusIntent.putExtra("ROUTE_ID", route_id);
		statusIntent.putExtra("CREATENEWROUTE", createNewRoute);
		statusIntent.putExtra("FEEDBACK", feedback);
		statusSpec.setContent(statusIntent);
		
		//Second Tab (Map)
		TabSpec mapSpec = tabHost.newTabSpec("Map");
		// setting Title and Icon for the Tab
		mapSpec.setIndicator("Map");
		Intent mapIntent = new Intent(this, MapActivity.class );
		mapIntent.putExtra("LAT1", lat1);
		mapIntent.putExtra("LONG1", long1);
		mapIntent.putExtra("LAT2", lat2);
		mapIntent.putExtra("LONG2", long2);
		mapIntent.putExtra("USER_ID", user_id);
		mapIntent.putExtra("COMPETITOR_ID", competitor_id);
		mapIntent.putExtra("ROUTE_ID", route_id);
		mapIntent.putExtra("CREATENEWROUTE", createNewRoute);
		mapIntent.putExtra("ROUTE_NAME", route_name);
		mapIntent.putExtra("FEEDBACK", feedback);
		mapSpec.setContent(mapIntent);
		
		//Third Tab (History)
		TabSpec historySpec = tabHost.newTabSpec("History");
		// setting Title and Icon for the Tab
		historySpec.setIndicator("History");
		Intent historyIntent = new Intent(this, HistoryActivity.class );
		historyIntent.putExtra("USER_ID", user_id);
		historySpec.setContent(historyIntent);
		
		// Adding all TabSpec to TabHost
		tabHost.addTab(statusSpec);
		tabHost.addTab(mapSpec);
		tabHost.addTab(historySpec);	
	}

}
