package com.myapps.fitnessApp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HistoryActivity extends Activity {
	public static int user_id;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		db = new DatabaseHandler(getApplicationContext());
		
		final ListView listview1 = (ListView) findViewById(R.id.historyListView1);
		final ListView listview2 = (ListView) findViewById(R.id.historyListView2);
		final ListView listview3 = (ListView) findViewById(R.id.historyListView3);
		final ListView listview4 = (ListView) findViewById(R.id.historyListView4);
		
		Intent intent = getIntent();
		user_id = intent.getExtras().getInt("USER_ID");
		
		
		
		//read from History Database Table
		List<History> listHistory = db.getHistoryByUserId(user_id);
		
		
		//Populating lists
		final ArrayList<String> list1 = new ArrayList<String>();
		final ArrayList<String> list2 = new ArrayList<String>();
		final ArrayList<String> list3 = new ArrayList<String>();
		final ArrayList<String> list4 = new ArrayList<String>();
	 	for (int i=0; i < listHistory.size(); ++i){
	 			list1.add(db.getRouteName(listHistory.get(i).getRoute_id()));
	 			list2.add(listHistory.get(i).getDate());
	 			list3.add(String.valueOf(listHistory.get(i).getDistance()));
	 			list4.add(String.valueOf(listHistory.get(i).getAvg_speed()));
	 	}
	 	
	 	//Creating ArrayAdapters from lists
	    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,R.layout.custom_layout, list1);
	    listview1.setAdapter(adapter1);
	    ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,R.layout.custom_layout, list2);
	    listview2.setAdapter(adapter2);
	    ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,R.layout.custom_layout, list3);
	    listview3.setAdapter(adapter3);
	    ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this,R.layout.custom_layout, list4);
	    listview4.setAdapter(adapter4);
	    
	}

}
