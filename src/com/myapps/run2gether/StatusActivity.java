package com.myapps.fitnessApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class StatusActivity extends Activity {
	
	public static double lat1,long1,lat2,long2;
	public static int user_id,competitor_id,route_id,feedback;
	public static boolean createNewRoute;
	
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_status);
		db = new DatabaseHandler(getApplicationContext());

		Intent intent = getIntent();
		lat1=intent.getDoubleExtra("LAT1", 0);
		long1=intent.getDoubleExtra("LONG1", 0);
		lat2=intent.getDoubleExtra("LAT2", 0);
		long2=intent.getDoubleExtra("LONG2", 0);
		user_id=intent.getExtras().getInt("USER_ID");
		competitor_id=intent.getExtras().getInt("COMPETITOR_ID");
		route_id=intent.getExtras().getInt("ROUTE_ID");
		createNewRoute=intent.getExtras().getBoolean("CREATENEWROUTE");
		feedback=intent.getExtras().getInt("FEEDBACK");
		
		TextView textView1 = (TextView) findViewById(R.id.usernameTextView);
		textView1.setText(db.getUsername(user_id));
		
		TextView textView2 = (TextView) findViewById(R.id.user_idTextView);
		textView2.setText(Integer.toString(user_id));
		
		TextView textView3 = (TextView) findViewById(R.id.route_idTextView);
		if (createNewRoute == true) {
			textView3.setText("New Route!");
		} else {
			textView3.setText(db.getRouteName((route_id)));
		}
		
		TextView textView4 = (TextView) findViewById(R.id.feedbackTextView);
		textView4.setText(Integer.toString(feedback));
		
		TextView textView5 = (TextView) findViewById(R.id.competitorTextView);
		if (createNewRoute == true) {
			textView5.setText("New Route!");
		} else {	
			textView5.setText(db.getUsername(competitor_id));
		}
		
		String start=Double.toString(lat1)+','+Double.toString(long1);
		TextView textView6 = (TextView) findViewById(R.id.route_start);
		if (createNewRoute == true) {
			textView6.setText("New Route!");
		} else {
			textView6.setText(start);
		}
		
		String finish=Double.toString(lat2)+','+Double.toString(long2);
		TextView textView7 = (TextView) findViewById(R.id.route_finish);
		if (createNewRoute == true) {
			textView7.setText("New Route!");
		} else {
			textView7.setText(finish);
		}
		

	}

	
}
