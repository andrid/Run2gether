package com.myapps.fitnessApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Finish extends Activity {
	int user_id,route_id,user_repetition;
	boolean createNewRoute;
	DatabaseHandler db;
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		
		db = new DatabaseHandler(getApplicationContext());
		
		MediaPlayer mp = new MediaPlayer();
		AssetFileDescriptor descriptor;
		try {
			descriptor = getAssets().openFd("applause.wav");
	 			mp.setDataSource( descriptor.getFileDescriptor(),descriptor.getStartOffset(), descriptor.getLength() );
	 			descriptor.close();
	 			mp.prepare();
	 			mp.start();
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Can't play audio file!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		getDataFromIntent();
		
		if(createNewRoute==true){
			new HttpAsyncTask().execute("https://fill-your-servername-here/routes");
		} else{
			new HttpAsyncTask2().execute("https://fill-your-servername-here/points");
		}
	}
	
    public void getDataFromIntent(){
    	Intent intent = getIntent();
    	user_id = intent.getExtras().getInt("USER_ID");
    	route_id = intent.getExtras().getInt("ROUTE_ID");
		createNewRoute=intent.getExtras().getBoolean("CREATENEWROUTE");
		user_repetition = intent.getExtras().getInt("REPETITION");
    }
	
	public static String PUT(String url,Route route){
        InputStream inputStream = null;
        String result = "";
        try {
        	 
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // 2. make PUT request to the given URL
            HttpPut httpPut = new HttpPut(url);
 
            String json = "";
            
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("route_id",route.getRoute_id());
            jsonObject.accumulate("start_latitude", route.getStart_latitude());
            jsonObject.accumulate("start_longitude",route.getStart_longitude());
            jsonObject.accumulate("finish_latitude",route.getFinish_latitude());
            jsonObject.accumulate("finish_longitude",route.getFinish_longitude());
            jsonObject.accumulate("description",route.getDescription());
            
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
 
            // 6. set httpPut Entity
            httpPut.setEntity(se);
 
            // 7. Set some headers to inform server about the type of the content   
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
 
            // 8. Execute PUT request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);
 
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        // 11. return result
        return result;
    }
	
	public static String PUT2(String url,List<Points> listPoints){
        InputStream inputStream = null;
        String result = "";
        try {
        	 
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // 2. make PUT request to the given URL
            HttpPut httpPut = new HttpPut(url);
 
            String json = "";
            JSONArray jsonArray = new JSONArray();
            
            for (int i=0;i<listPoints.size();i++){
            // 3. build jsonObject and put to jsonArray
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("point_number",listPoints.get(i).getPointNumber());
            jsonObject.accumulate("user_id3", listPoints.get(i).getUser_id());
            jsonObject.accumulate("route_id3",listPoints.get(i).getRoute_id());
            jsonObject.accumulate("latitude",listPoints.get(i).getLatitude());
            jsonObject.accumulate("longitude",listPoints.get(i).getLongitude());
            jsonObject.accumulate("repetition",listPoints.get(i).getRepetition());
            jsonArray.put(jsonObject);            
            }
            
            // 4. convert JSONObject to JSON to String
            json = jsonArray.toString();
            
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
 
            // 6. set httpPut Entity
            httpPut.setEntity(se);
 
            // 7. Set some headers to inform server about the type of the content   
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
 
            // 8. Execute PUT request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);
 
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        // 11. return result
        return result;
    }
	
	public static String PUT3(String url,History history){
        InputStream inputStream = null;
        String result = "";
        try {
        	 
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // 2. make PUT request to the given URL
            HttpPut httpPut = new HttpPut(url);
 
            String json = "";
            
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("history_id",history.getHistory_id());
            jsonObject.accumulate("user_id2",history.getUser_id());
            jsonObject.accumulate("route_id2",history.getRoute_id() );
            jsonObject.accumulate("date",history.getDate());
            jsonObject.accumulate("distance",history.getDistance());
            jsonObject.accumulate("average_speed",history.getAvg_speed());
            
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);
 
            // 6. set httpPut Entity
            httpPut.setEntity(se);
 
            // 7. Set some headers to inform server about the type of the content   
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
 
            // 8. Execute PUT request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPut);
 
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        // 11. return result
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
	
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
    	@Override
        protected String doInBackground(String... urls) {
    		
    		Route route = db.getRouteByRouteId(route_id);  		
    		return PUT(urls[0],route);
    	}
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Sent Route!", Toast.LENGTH_LONG).show();
            new HttpAsyncTask2().execute("https://fill-your-servername-here/points");
       }
        
        @Override
        protected void onPreExecute() {
           if (progressDialog == null) {
        	   progressDialog = new ProgressDialog(Finish.this);
        	   progressDialog.setMessage("Synchronizing with server,(1/3 Routes) please wait...");
        	   progressDialog.show();
        	   progressDialog.setCanceledOnTouchOutside(false);
        	   progressDialog.setCancelable(false);
           }
        }
    }
    
    private class HttpAsyncTask2 extends AsyncTask<String, Void, String> {
    	@Override
        protected String doInBackground(String... urls) {
    		
    		List<Points> listPoints =  new ArrayList<Points>();
    		listPoints = db.getPointsByRepetition(Integer.toString(route_id), Integer.toString(user_id), Integer.toString(user_repetition));  		
    		return PUT2(urls[0],listPoints);
    		

    	}
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Sent Points!", Toast.LENGTH_LONG).show();
            new HttpAsyncTask3().execute("https://fill-your-servername-here/history");
       }
        
        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
            		progressDialog = new ProgressDialog(Finish.this);
                	progressDialog.setMessage("Synchronizing with server,(2/3 Points) please wait...");
                	progressDialog.show();
                	progressDialog.setCanceledOnTouchOutside(false);
                	progressDialog.setCancelable(false);
                } else {    	
                	progressDialog.setMessage("Synchronizing with server,(2/3 Points) please wait...");
                }
        	}
    }
    
    private class HttpAsyncTask3 extends AsyncTask<String, Void, String> {
    	@Override
        protected String doInBackground(String... urls) {
    		
    		History history = db.getLastHistoryRecord();  		
    		return PUT3(urls[0],history);
    		

    	}
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Sent History!", Toast.LENGTH_LONG).show();
            
           if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                }
       }
        
        @Override
        protected void onPreExecute() {
           progressDialog.setMessage("Synchronizing with server,(3/3 History) please wait...");
        }
    }
        
    
	
	//if user presses the Return to Login button
	public void returnToLogin(View v){
    	Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	//if user presses the Exit App button
	public void exitApp2(View v){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
