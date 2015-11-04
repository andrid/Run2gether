package com.myapps.fitnessApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


public class MainActivity extends Activity {
	
	DatabaseHandler db;
	TextView login_message;
	Button buttonLogin;
	public static String username,password;
	public static int user_id;
	ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		db = new DatabaseHandler(getApplicationContext());
		
		login_message = (TextView) findViewById(R.id.login_message);
		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		
		//Check if you are connected to data network
        if(isConnected()){
        	login_message.setText("Please login");
        }
        else{
        	login_message.setText("Please connect to data network!");
        	buttonLogin.setClickable(false);
        	buttonLogin.setAlpha(.5f);
        }
		
		
		//Google Analytics
		Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
		t.setScreenName("MainActivity");
		t.send(new HitBuilders.ScreenViewBuilder().build());
	}

	
	/** Called when the user clicks the "Log in" button */
	public void login(View view) {
		EditText usernameText = (EditText) findViewById(R.id.usernameText);
		EditText passwordText = (EditText) findViewById(R.id.passwordText);
		username = usernameText.getText().toString();
		password = passwordText.getText().toString();
		user_id=db.login(username, password);
		if (username.length() > 0 && password.length() > 0) {
			try {
				if (user_id!=0){
					Toast.makeText(getApplicationContext(), "Successfully loged in!", Toast.LENGTH_SHORT).show();
					
					//Google Analytics
					Tracker t = ((fitnessApp) getApplication()).getTracker(fitnessApp.TrackerName.APP_TRACKER);
					t.send(new HitBuilders.EventBuilder().setCategory("Log-in").setAction("Username").setLabel(username).build());
					
					new HttpAsyncTask1().execute("https://fill-your-servername-here/routes");

				}else{
					Toast.makeText(getApplicationContext(), "Invalid username or password!", Toast.LENGTH_SHORT).show();
				}
				}catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Some problem occurred!", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(), "Username or Password is empty!", Toast.LENGTH_SHORT).show();
		}
		
	}
	
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result; 
    }
	
	private class HttpAsyncTask1 extends AsyncTask<String, Void, String> {
    	
    	
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	Toast.makeText(getBaseContext(), "Received routes", Toast.LENGTH_LONG).show();
        	db.cleanDatabase("routes");
        	
        	JSONArray jsonArray=null;
        	try {
				jsonArray = new JSONArray(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	
        		try {
        			for (int i=0; i<jsonArray.length();i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					db.addRoute(Double.parseDouble(jsonObject.getString("start_latitude")),Double.parseDouble(jsonObject.getString("start_longitude")),Double.parseDouble(jsonObject.getString("finish_latitude")),Double.parseDouble(jsonObject.getString("finish_longitude")),jsonObject.getString("description"));
        			}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
        		
        		new HttpAsyncTask2().execute("https://fill-your-servername-here/points");	
        }
        
        @Override
        protected void onPreExecute() {
           if (progressDialog == null) {
           progressDialog = new ProgressDialog(MainActivity.this);
           progressDialog.setMessage("Synchronizing with server,(1/4 Routes) please wait...");
           progressDialog.show();
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.setCancelable(false);
           }
        }		
	}
	
	private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {    	
    	
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	Toast.makeText(getBaseContext(), "Received points", Toast.LENGTH_LONG).show();
        	db.cleanDatabase("points");
        	
        	JSONArray jsonArray=null;
        	try {
				jsonArray = new JSONArray(result);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	
        	
        		try {
        			for (int i=0; i<jsonArray.length();i++){
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					db.addPoint(jsonObject.getInt("user_id3"),jsonObject.getInt("route_id3"),Double.parseDouble(jsonObject.getString("latitude")),Double.parseDouble(jsonObject.getString("longitude")),jsonObject.getInt("repetition"));
        			}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
        		
        		new HttpAsyncTask3().execute("https://fill-your-servername-here/history");	
        }
        
        @Override
        protected void onPreExecute() {           
           progressDialog.setMessage("Synchronizing with server,(2/4 Points) please wait...");           
        }
}
        
    	private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {        	
        	
            @Override
            protected String doInBackground(String... urls) {
     
                return GET(urls[0]);
            }
            
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
            	Toast.makeText(getBaseContext(), "Received history", Toast.LENGTH_LONG).show();
            	db.cleanDatabase("history");
            	
            	JSONArray jsonArray=null;
            	try {
    				jsonArray = new JSONArray(result);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
            	
            	
            		try {
            			for (int i=0; i<jsonArray.length();i++){
    					JSONObject jsonObject = jsonArray.getJSONObject(i);
    					db.addHistory(jsonObject.getInt("user_id2"),jsonObject.getInt("route_id2"),jsonObject.getString("date"),Double.parseDouble(jsonObject.getString("distance")),Double.parseDouble(jsonObject.getString("average_speed")));
            			}
    					
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
            		new HttpAsyncTask4().execute("https://fill-your-servername-here/users");
            }
        
        
        @Override
        protected void onPreExecute() {
           progressDialog.setMessage("Synchronizing with server,(3/4 History) please wait...");
        }
		
	}
    	
    	private class HttpAsyncTask4 extends AsyncTask<String, Void, String> {
        	
            @Override
            protected String doInBackground(String... urls) {
     
                return GET(urls[0]);
            }
            
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute(String result) {
            	Toast.makeText(getBaseContext(), "Received users", Toast.LENGTH_LONG).show();
            	db.cleanDatabase("users");
            	
            	JSONArray jsonArray=null;
            	try {
    				jsonArray = new JSONArray(result);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
            	
            	
            		try {
            			for (int i=0; i<jsonArray.length();i++){
    					JSONObject jsonObject = jsonArray.getJSONObject(i);
    					db.addUser(jsonObject.getString("username"), jsonObject.getString("password"));
    					}
    					
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}   
            		
                if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                }
                passIntent();                
            }        
        
        @Override
        protected void onPreExecute() {
           progressDialog.setMessage("Synchronizing with server,(4/4 Users) please wait...");
        }		
	}
	
	
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }
    
    public void passIntent(){
    	Intent intent = new Intent(this, RouteChoice.class);
		intent.putExtra("USERNAME", username);
		intent.putExtra("USER_ID", user_id);
		startActivity(intent);
    }
    
	//if user presses the Exit App button
	public void exitApp(View v){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
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
