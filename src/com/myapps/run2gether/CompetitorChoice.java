package com.myapps.fitnessApp;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CompetitorChoice extends Activity {
	public static double lat1,long1,lat2,long2;
	public static int route_id,user_id,competitor_id;
	public static boolean createNewRoute;
	public static int feedback=1;
	public static String route_name,competitor_username;
	
	DatabaseHandler db;
	
	List<Integer> listUserIds;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_competitor_choice);
		db = new DatabaseHandler(getApplicationContext());
		
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.setScreenName("CompetitorChoice");
		t.send(new HitBuilders.ScreenViewBuilder().build());
		
		final ListView listview = (ListView) findViewById(R.id.UserlistView1);
		
		Intent intent = getIntent();
		lat1=intent.getExtras().getDouble("LAT1");
		long1=intent.getExtras().getDouble("LONG1");
		lat2=intent.getExtras().getDouble("LAT2");
		long2=intent.getExtras().getDouble("LONG2");
		user_id=intent.getExtras().getInt("USER_ID");
		route_id=intent.getExtras().getInt("ROUTE_ID");
		createNewRoute=intent.getExtras().getBoolean("CREATENEWROUTE");
		route_name=intent.getStringExtra("ROUTE_NAME");
		
		listUserIds=db.readUsers(route_id);
		
		final ArrayList<String> listUsers = new ArrayList<String>();
		for (int i=0; i < listUserIds.size(); ++i){
			listUsers.add(db.getUsername(listUserIds.get(i)));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listUsers);
	    	listview.setAdapter(adapter);
	    	listview.setOnItemClickListener(new OnItemClickListener() {
		    	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		    		competitor_id=db.getUserId(String.valueOf(((TextView) view).getText()));
		    		passIntent();
		    		}		    	
		    });
	    	
	    }
		

	
	public void passIntent(){
		
		competitor_username=db.getUsername(competitor_id);
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder().setCategory("Competitor Choice").setAction("Competitor").setLabel(competitor_username).build());
		
		Intent intent2 = new Intent(this, FeedbackChoice.class);	   
		intent2.putExtra("LAT1", lat1);
		intent2.putExtra("LONG1", long1);
		intent2.putExtra("LAT2", lat2);
		intent2.putExtra("LONG2", long2);
		intent2.putExtra("USER_ID", user_id);
		intent2.putExtra("COMPETITOR_ID", competitor_id);
		intent2.putExtra("ROUTE_ID", route_id);
		intent2.putExtra("CREATENEWROUTE",createNewRoute);
		intent2.putExtra("FEEDBACK", feedback);
		intent2.putExtra("ROUTE_NAME", route_name);
		startActivity(intent2); 
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);	
}
	
	@Override
	protected void onStop(){
		super.onStop();
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
}
