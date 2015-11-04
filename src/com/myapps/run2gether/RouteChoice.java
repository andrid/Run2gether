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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class RouteChoice extends Activity {
	public static double lat1;
	public static double long1;
	public static double lat2;
	public static double long2;
	public static int route_id;
	public static int user_id;
	public static boolean createNewRoute=false;
	public static String route_name;
	
	DatabaseHandler db;
	
	List<Route> listRoutes;
	Route r = null;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_choice);
		db = new DatabaseHandler(getApplicationContext());
		
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.setScreenName("RouteChoice");
		t.send(new HitBuilders.ScreenViewBuilder().build());
		
		final ListView listview = (ListView) findViewById(R.id.RoutelistView1);
		
		Intent intent = getIntent();
		String name = intent.getStringExtra("USERNAME");
		user_id = intent.getExtras().getInt("USER_ID");
		
		
		TextView textView = (TextView) findViewById(R.id.name);
		textView.setText(name);	
		
		listRoutes = db.readRoutes();
		
		
		final ArrayList<String> list = new ArrayList<String>();
	 	for (int i=0; i < listRoutes.size(); ++i){
	 		 list.add(listRoutes.get(i).getDescription());
	 	}
	 	
	 	 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
		    listview.setAdapter(adapter);
		    listview.setOnItemClickListener(new OnItemClickListener() {
		    	public void onItemClick(AdapterView<?> parent, View view, int position, long id){
		    		r = db.getRouteByName(String.valueOf(((TextView) view).getText()));
		    		lat1=r.getStart_latitude();
		    		long1=r.getStart_longitude();
		    		lat2=r.getFinish_latitude();
		    		long2=r.getFinish_longitude();
		    		route_id=r.getRoute_id();
		    		route_name=r.getDescription();
		    		passIntent();
		    		}
		    	
		    });
		 
		   
	}

	
	
	//if user presses the create new route button
	public void createNewRoute(View v){
		createNewRoute=true;
		EditText editText = (EditText) findViewById(R.id.editTextRouteName);
		route_name = editText.getText().toString();
		route_id=db.getNewRouteId();
		if (route_name.isEmpty()){
			Toast.makeText(getApplicationContext(), "Please insert a route name", Toast.LENGTH_SHORT).show();
			}
		else if (db.routeNameExists(route_name)){
			Toast.makeText(getApplicationContext(), "Sorry,route name already exists!", Toast.LENGTH_SHORT).show();
			}
		else {
			passIntent2();
			}
	}
	
	public void passIntent(){
		
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder().setCategory("Route Choice").setAction("Route").setLabel(route_name).build());
		
		Intent intent2 = new Intent(this, CompetitorChoice.class);	   
		intent2.putExtra("LAT1", lat1);
		intent2.putExtra("LONG1", long1);
		intent2.putExtra("LAT2", lat2);
		intent2.putExtra("LONG2", long2);
		intent2.putExtra("USER_ID", user_id);
		intent2.putExtra("ROUTE_ID", route_id);
		intent2.putExtra("CREATENEWROUTE",createNewRoute);
		intent2.putExtra("ROUTE_NAME", route_name);
		startActivity(intent2); 
	}
	
	public void passIntent2(){
		
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder().setCategory("Route Choice").setAction("Route").setLabel(route_name).build());
		
		Intent intent3 = new Intent(this, TabsActivity.class);	   
		intent3.putExtra("LAT1", lat1);
		intent3.putExtra("LONG1", long1);
		intent3.putExtra("LAT2", lat2);
		intent3.putExtra("LONG2", long2);
		intent3.putExtra("USER_ID", user_id);
		intent3.putExtra("ROUTE_ID", route_id);
		intent3.putExtra("CREATENEWROUTE",createNewRoute);
		intent3.putExtra("ROUTE_NAME", route_name);
		startActivity(intent3); 
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
	

