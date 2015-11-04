package com.myapps.fitnessApp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements LocationListener,GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener{
	
	
	// A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
	private GoogleMap googleMap;
    double current_lat,current_long;
    double newRouteStartLat,newRouteStartLong,newRouteFinishLat,newRouteFinishLong;
    int user_id,competitor_id,route_id;
    String route_name,competitor_username;
    double lat1,long1,lat2,long2;
    private List<Points> listPoints, tempList;
    private List<Points> newPoints = new ArrayList<Points>();
    Timer timer;
    TimerTask timerTask;
    int counter = 0;
	Handler handler = new Handler();
	MarkerOptions markerOptions3;
	MarkerOptions markerOptions4;
	Marker marker3;
	Marker marker4;
	TextView warningTextView;
	Button startButton;
	Button stopButton;
	boolean running;
	boolean createNewRoute=false;
	int feedback,user_repetition,co_user_repetition;
	//Write to log file (for troubleshooting) and evaluation file
	CSVFile evaluation;
	DatabaseHandler db;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		db = new DatabaseHandler(getApplicationContext());
		
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.setScreenName("MapActivity");
		t.send(new HitBuilders.ScreenViewBuilder().build());

		running=false;		

		
        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();
        /*
         * Set the update interval
         */
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
	
		
		getDataFromIntent();
		
        
		//read from Points database to find other users routes
		if (createNewRoute==false) {
			co_user_repetition=db.getRepetitions(Integer.toString(route_id),Integer.toString(competitor_id));
			competitor_username=db.getUsername(competitor_id);
			for (int j=1;j<=co_user_repetition;j++){
				tempList=db.getPointsByRepetition(Integer.toString(route_id),Integer.toString(competitor_id),Integer.toString(j));
        		if (listPoints == null || tempList.size() <= listPoints.size()) {
        			listPoints = tempList;
        			}
				}
			}
		
		user_repetition=db.getRepetitions(Integer.toString(route_id), Integer.toString(user_id))+1;
		
		
		//evaluation
		evaluation = new CSVFile(getApplicationContext());
		evaluation.writeToLog("evaluation.txt", getDate()+": User: "+user_id+" ,Route: "+route_id+" ,New Route: "+createNewRoute+" ,Repetition: "+user_repetition+" ,Competitor: "+competitor_id+" ,Feedback: "+feedback);
		
		initilizeMap();
		
		//UI Elements
		warningTextView = (TextView) findViewById(R.id.warning);
		startButton = (Button) findViewById(R.id.buttonStart);
		stopButton = (Button) findViewById(R.id.buttonStop);
		
 
    }
	
		
	
    /*
     * Called when the Activity is no longer visible at all.
     * Stop updates and disconnect.
     */
    @Override
    public void onStop() {

        // If the client is connected
        if (mLocationClient.isConnected()) {
            stopPeriodicUpdates();
        }

        // After disconnect() is called, the client is considered "dead".
        mLocationClient.disconnect();

        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
    
    /*
     * Called when the Activity is going into the background.
     * Parts of the UI may be visible, but the Activity is inactive.
     */
    @Override
    public void onPause() {

        super.onPause();
    }

    /*
     * Called when the Activity is restarted, even before it becomes visible.
     */
    @Override
    public void onStart() {

        super.onStart();
		GoogleAnalytics.getInstance(this).reportActivityStart(this);

        /*
         * Connect the client. Don't re-start any requests here;
         * instead, wait for onResume()
         */
        mLocationClient.connect();
        checkProximity();


    }

    
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
        
 
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            
            //enable My Location Layer
            googleMap.setMyLocationEnabled(true);
            
            googleMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
				@Override
				public boolean onMyLocationButtonClick() {
					Toast.makeText(getApplicationContext(), "Location button presed!", Toast.LENGTH_SHORT).show();  
					return false;
				}
			});
            

            markerOptions3 = new MarkerOptions().position(new LatLng(current_lat, current_long)).title("You are here");
            markerOptions3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));           

            if (createNewRoute==false){
                MarkerOptions markerOptions1 = new MarkerOptions().position(new LatLng(lat1, long1)).title("Start");
                MarkerOptions markerOptions2 = new MarkerOptions().position(new LatLng(lat2, long2)).title("Finish");
                markerOptions4 = new MarkerOptions().position(new LatLng(listPoints.get(0).getLatitude(), listPoints.get(0).getLongitude())).title(competitor_username);
                markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                markerOptions4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                googleMap.addMarker(markerOptions1);
                googleMap.addMarker(markerOptions2);
                //moving camera to current position
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat1, long1)).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else if (createNewRoute==true) 
            {
            //moving camera to  start position(there is no start)
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(current_lat, current_long)).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 

    
    protected void onDestroy(){
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
    
    public void startTimer() {
		//set a new Timer
		timer = new Timer();
		
		//initialize the TimerTask's job
		initializeTimerTask();
		
		//schedule the timer, the TimerTask will run every 10sec
		timer.schedule(timerTask, 10000, 10000); //
	}
    
    public void starttimertask(View v){
    	if (createNewRoute==false){
		warningTextView.setText("Now running...");
		warningTextView.setTextColor(Color.GREEN);
		startButton.setClickable(false);
		stopButton.setClickable(false);
		startButton.setAlpha(.5f);
		stopButton.setAlpha(.5f);
		running = true;
		if (timer == null) {
			startTimer();
		}
    	} else if (createNewRoute==true){
    		warningTextView.setText("Start saved.Now running...");
    		warningTextView.setTextColor(Color.GREEN);
    		startButton.setClickable(false);
    		stopButton.setClickable(true);
    		startButton.setAlpha(.5f);
    		stopButton.setAlpha(1);
    		running = true;
    		newRouteStartLat=current_lat;
    		newRouteStartLong=current_long;
    		if (timer == null) {
    			startTimer();
    		}
    	}
	}
    
    public void stoptimertask(View v) {
		//stop the timer, if it's not already null
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		
		if (createNewRoute==true){
    		warningTextView.setText("Route saved!");
    		warningTextView.setTextColor(Color.GREEN);
			startButton.setClickable(false);
			stopButton.setClickable(false);
			startButton.setAlpha(.5f);
			stopButton.setAlpha(.5f);
			newRouteFinishLat=current_lat;
    		newRouteFinishLong=current_long;
    		//write route_name,start and finish points to route Database Table!
    		db.addRoute(newRouteStartLat, newRouteStartLong, newRouteFinishLat, newRouteFinishLong, route_name);
    		
    		writeToHistory();
    		
			
		}
	}
    
    public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				
				//Using a handler to write route points and give feedback(visual,sound or vibration) 
				handler.post(new Runnable() {
					public void run() {
						//show competitor route only if createNewRoute==false and feedback==4
						if ((createNewRoute==false) && (feedback==4)){
						if (marker4 == null) {
							marker4 = googleMap.addMarker(markerOptions4);							
						}
			   		 	if (counter < listPoints.size()){
			   		 		double latitude = listPoints.get(counter).getLatitude();
			   		 		double longitude = listPoints.get(counter).getLongitude();
			   		 		counter++;
			   		 		marker4.setPosition(new LatLng(latitude, longitude));
			   		 		marker4.showInfoWindow();
			   		 		}
						}
						
						else if ((createNewRoute==false) && (feedback==2)){
							//vibration						
				   		 	if (counter < listPoints.size()){
				   		 		double latitude = listPoints.get(counter).getLatitude();
				   		 		double longitude = listPoints.get(counter).getLongitude();
				   		 		counter++;
				   		 		Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
				   		 		if (isAhead(latitude,longitude)){
				   		 			//3 long vibrations if user is ahead of competitor
				   		 			long pattern1[]={0,800,0,800,0,800};
				   		 			v.vibrate(pattern1, -1);
				   		 		} else{
				   		 			//5 short vibrations if user is behind of competitor
				   		 			long pattern2[]={0,400,0,400,0,400,0,400,0,400};
				   		 			v.vibrate(pattern2, -1);
				   		 			}
				   		 		}
						}
						
						else if ((createNewRoute==false) && (feedback==3)){
							//sound
				   		 	if (counter < listPoints.size()){
				   		 		double latitude = listPoints.get(counter).getLatitude();
				   		 		double longitude = listPoints.get(counter).getLongitude();
				   		 		counter++;
				   		 		if (isAhead(latitude,longitude)){
				   		 			MediaPlayer mp1 = new MediaPlayer();
				   		 			AssetFileDescriptor descriptor1;
									try {
										descriptor1 = getAssets().openFd("ahead.wav");
					   		 			mp1.setDataSource( descriptor1.getFileDescriptor(),descriptor1.getStartOffset(), descriptor1.getLength() );
					   		 			descriptor1.close();
					   		 			mp1.prepare();
					   		 			mp1.start();
									} catch (IOException e) {
										Toast.makeText(getApplicationContext(), "Can't play audio file!", Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}

				   		 		} else{
				   		 			MediaPlayer mp2 = new MediaPlayer();
				   		 			AssetFileDescriptor descriptor2;
									try {
										descriptor2 = getAssets().openFd("behind.wav");
					   		 			mp2.setDataSource( descriptor2.getFileDescriptor(),descriptor2.getStartOffset(), descriptor2.getLength() );
					   		 			descriptor2.close();
					   		 			mp2.prepare();
					   		 			mp2.start();
									} catch (IOException e) {
										Toast.makeText(getApplicationContext(), "Can't play audio file!", Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}

				   		 			}
				   		 		}
						}						
					}
				});
			}
		};
	}
	
    
    
    public void getDataFromIntent(){
    	Intent intent = getIntent();
    	lat1=intent.getDoubleExtra("LAT1", 0);
		long1=intent.getDoubleExtra("LONG1", 0);
		lat2=intent.getDoubleExtra("LAT2", 0);
		long2=intent.getDoubleExtra("LONG2", 0);
    	user_id = intent.getExtras().getInt("USER_ID");
    	competitor_id = intent.getExtras().getInt("COMPETITOR_ID");
    	route_id = intent.getExtras().getInt("ROUTE_ID");		
		createNewRoute=intent.getExtras().getBoolean("CREATENEWROUTE");
		route_name=intent.getStringExtra("ROUTE_NAME");
		feedback=intent.getExtras().getInt("FEEDBACK");		
    }
    
    public void checkProximity(){
		//check if user is close to Start
    	if (((Math.abs(current_lat - lat1) > 0.0001) || (Math.abs(current_long - long1) > 0.0001)) && (running == false) && (createNewRoute == false)){
			warningTextView.setText("Please move closer to Start!"); 
			warningTextView.setTextColor(Color.RED);
			startButton.setClickable(false);
			stopButton.setClickable(false);
			startButton.setAlpha(.5f);
			stopButton.setAlpha(.5f);
		} else if ((Math.abs(current_lat - lat1) < 0.0001) && (Math.abs(current_long - long1) < 0.0001) && (running == false) && (createNewRoute == false)){
			warningTextView.setText("You can start running!");
			warningTextView.setTextColor(Color.GREEN);
			startButton.setClickable(true);
			stopButton.setClickable(true);
			startButton.setAlpha(1);
			stopButton.setAlpha(1);
			//check if user is close to finish
		} else if ((Math.abs(current_lat - lat2) < 0.0001) && (Math.abs(current_long - long2) < 0.0001) && (running == true) && (createNewRoute == false)){
			warningTextView.setText("Route Completed!"); 
			warningTextView.setTextColor(Color.GREEN);
			startButton.setClickable(false);
			stopButton.setClickable(false);
			startButton.setAlpha(.5f);
			stopButton.setAlpha(.5f);
			if (timer != null) {
				timer.cancel();
				timer = null;
				}
			running = false;
			writeToHistory();
		} else if ((running == false) && (createNewRoute == true)){
			warningTextView.setText("Press Start to begin");
			warningTextView.setTextColor(Color.GREEN);
			startButton.setClickable(true);
			stopButton.setClickable(true);
			startButton.setAlpha(1);
			stopButton.setAlpha(1);
		} else {
		}
    }
        
    
    
    private void writeToHistory() {

    	String date=getDate();
    	
    	double distance=0;
    	//Calculate distance(in km) from latitude & longitude
    	if (createNewRoute == false){
    		distance=Math.floor(calcDistance(lat1,long1,lat2,long2)*100)/100;
    		}
    	else if (createNewRoute == true){
    		distance=Math.floor(calcDistance(newRouteStartLat,newRouteStartLong,newRouteFinishLat,newRouteFinishLong)*100)/100;
    		}
    	
    	//Calculate Average Speed (in km/hr) calculated with point period of 10 secs
    	double avg_speed=Math.floor((360*distance)/newPoints.size())*100/100;
    	
    	
    			
		//Write to history database table
    	db.addHistory(user_id, route_id, date, distance, avg_speed);
    	
    	//Write to points database table
    	for (int i=0;i<newPoints.size();i++){
    		db.addPoint(newPoints.get(i).getUser_id(), newPoints.get(i).getRoute_id(), newPoints.get(i).getLatitude(), newPoints.get(i).getLongitude(),user_repetition);
    	}
    	
    	//evaluation
    	evaluation.writeToLog("evaluation.txt"," ,Finished:YES");

    	
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder().setCategory("Achievement").setAction("Finish Route").setLabel("Finish").build());
    	
    	passIntent();
    
    }
    
	public void passIntent(){
		   Intent intent2 = new Intent(this, Finish.class);	   
			   intent2.putExtra("LAT1", lat1);
			   intent2.putExtra("LONG1", long1);
			   intent2.putExtra("LAT2", lat2);
			   intent2.putExtra("LONG2", long2);
			   intent2.putExtra("USER_ID", user_id);
			   intent2.putExtra("COMPETITOR_ID", competitor_id);
			   intent2.putExtra("ROUTE_ID", route_id);
			   intent2.putExtra("CREATENEWROUTE",createNewRoute);
			   intent2.putExtra("ROUTE_NAME", route_name);
			   intent2.putExtra("FEEDBACK", feedback);
			   intent2.putExtra("REPETITION", user_repetition);
			   startActivity(intent2); 
	}
    
    private boolean isAhead(double latitude,double longitude){
    	double a = calcDistance(lat1,long1,current_lat,current_long);
    	double b = calcDistance(lat1,long1,latitude,longitude);
    	if (a>b) return true;
    	else return false;
    }
    
    private double calcDistance(double latitude1,double longitude1,double latitude2,double longitude2){
    	double earthRadius=6373;
    	double dLat=Math.toRadians(latitude2-latitude1);
    	double dLong=Math.toRadians(longitude2-longitude1);
    	double sindLat=Math.sin(dLat/2);
    	double sindLong=Math.sin(dLong/2);
    	double a=Math.pow(sindLat, 2)+Math.pow(sindLong, 2)*Math.cos(Math.toRadians(latitude1))*Math.cos(Math.toRadians(latitude2));
    	double c=2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    	double dist = earthRadius*c;
    	return dist;
    }
    
    private String getDate() {
    	//Get date
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    	final String date = simpleDateFormat.format(calendar.getTime());
    	return date;
    }


	/**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d(LocationUtils.APPTAG, getString(R.string.play_services_available));

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {        
                Log.d(LocationUtils.APPTAG, "Play services not available");
                return false;
        		}
    }
    
    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
    	Toast.makeText(this, "Connected to Google Location Services", Toast.LENGTH_SHORT).show();
            startPeriodicUpdates();
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                e.printStackTrace();
            }
        } else {

            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }


    @Override
    public void onLocationChanged(Location location) {

    	if (marker3 == null) {
			marker3 = googleMap.addMarker(markerOptions3);							
		}
    	current_lat = location.getLatitude();
        current_long = location.getLongitude();
        marker3.setPosition(new LatLng(current_lat,current_long));
        Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();

        
        Points point = new Points(user_id, route_id, current_lat, current_long, user_repetition);
        newPoints.add(point);
		
        checkProximity();
    }
    
    /**
     * In response to a request to start updates, send a request
     * to Location Services
     */
    private void startPeriodicUpdates() {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
    }

    /**
     * In response to a request to stop updates, send a request to
     * Location Services
     */
    private void stopPeriodicUpdates() {
    	
        mLocationClient.removeLocationUpdates(this);
    }
    
    /**
     * Show a dialog returned by Google Play services for the
     * connection error code
     *
     * @param errorCode An error code returned from onConnectionFailed
     */
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

        }
    }

    /**
     * Define a DialogFragment to display the error dialog generated in
     * showErrorDialog.
     */
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /**
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
