package com.myapps.fitnessApp;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import android.app.Application;

public class fitnessApp extends Application{

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	public synchronized Tracker getTracker(TrackerName trackerId) {
		
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
	}
	
    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
//        E_COMMERCE_TRACKER,
    }
}
