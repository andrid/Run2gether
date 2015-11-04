package com.myapps.fitnessApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static String DATABASE_PATH = "/databases/";
	private SQLiteDatabase db;
	private Context context;
	
	// Database Version
	private static final int DATABASE_VERSION = 1;
	
	// Database Name
	private static final String DATABASE_NAME = "fitnessDb";
	
	// Routes table name
	private static final String TABLE_ROUTES = "routes";
	
	// Users table name
	private static final String TABLE_USERS = "users";
	
	// History table name
	private static final String TABLE_HISTORY = "history";
	
	// Points table name
	private static final String TABLE_POINTS = "points";
	
	// Routes Table Columns names
	private static final String KEY_ROUTE_ID = "route_id";
	private static final String KEY_START_LATITUDE = "start_latitude";
	private static final String KEY_START_LONGITUDE = "start_longitude";
	private static final String KEY_FINISH_LATITUDE = "finish_latitude";
	private static final String KEY_FINISH_LONGITUDE = "finish_longitude";
	private static final String KEY_ROUTE_NAME = "route_name";
	
	// Users Table Columns names
	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_USERNAME = "username";
	private static final String KEY_PASSWORD = "password";
	
	// History Table Columns names
	private static final String KEY_HISTORY_ID = "history_id";
	private static final String KEY_USER_ID2 = "user_id2";
	private static final String KEY_ROUTE_ID2 = "route_id2";
	private static final String KEY_DATE = "date";
	private static final String KEY_DISTANCE = "distance";
	private static final String KEY_AVG_SPEED = "avg_speed";
	
	// Points Table Columns names
	private static final String KEY_POINT_NUMBER = "point_number";
	private static final String KEY_USER_ID3 = "user_id3";
	private static final String KEY_ROUTE_ID3 = "route_id3";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_REPETITION = "repetition";
	
	public void openDataBase() throws SQLException{
		if (!databaseExists()) {
			try {
				copyDataBase();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//Open the database
		String myPath = context.getFilesDir().getAbsolutePath() + DATABASE_PATH + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	private boolean databaseExists() {
		SQLiteDatabase control = null;
		try {
			String myPath =  context.getFilesDir().getAbsolutePath() + DATABASE_PATH + DATABASE_NAME;
			control = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			control = null;
		}
	
		if (control != null) {
			control.close();
		}
		return control != null ? true : false;
	}
	
	private void copyDataBase() throws IOException {
		InputStream is = context.getAssets().open(DATABASE_NAME);

		File dir = new File(context.getFilesDir(), DATABASE_PATH);
		dir.mkdir();
		File file = new File(dir, DATABASE_NAME);
		
		OutputStream os = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) > 0) {
			os.write(buffer, 0, length);
		}

		os.flush();
		os.close();
		is.close();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		openDataBase();
	}
	
	public void cleanDatabase(String table){
		db.execSQL("delete from "+ table);
		db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name='"+table+"'");
	}
	
	//Login: Get user_id from username and password
	int login(String username, String password){
		Cursor cursor = db.rawQuery("SELECT user_id FROM users WHERE username = ? AND password = ? ",new String[] {username,password});
		if(cursor.moveToFirst()){
			int result = cursor.getInt(0);
			return result;
		} else {
			return 0;
		}

	}
	
	//Getting all routes
	List<Route> readRoutes(){
		List<Route> resultList = new ArrayList<Route>();
		Cursor cursor = db.query(TABLE_ROUTES,new String[] {KEY_ROUTE_ID,KEY_START_LATITUDE,KEY_START_LONGITUDE,KEY_FINISH_LATITUDE,KEY_FINISH_LONGITUDE,KEY_ROUTE_NAME},null, null, null, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				Route route = new Route(cursor.getInt(0) ,cursor.getDouble(1) , cursor.getDouble(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5));
				resultList.add(route);
				cursor.moveToNext();
			}
		}
		return resultList;
	}
	
	//Getting all userids from users that ran a specific route
	List<Integer> readUsers(int routeid){
		List<Integer> resultList = new ArrayList<Integer>();
		Cursor cursor = db.query(true, TABLE_POINTS, new String[] {KEY_USER_ID3}, KEY_ROUTE_ID3 + "=" + routeid, null, null, null, KEY_USER_ID3, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				int userid = cursor.getInt(0);
				resultList.add(userid);
				cursor.moveToNext();
			}
		}
		return resultList;
	}
	
	//Get username from userid
	String getUsername(int userid){
		Cursor cursor = db.query(TABLE_USERS, new String[] {KEY_USERNAME}, KEY_USER_ID + "='" + userid + "'", null, null, null, null);
		cursor.moveToFirst(); 			
		String result = cursor.getString(0);
		return result;
	}
	
	//Get userid from username
	int getUserId(String username){
		Cursor cursor = db.query(TABLE_USERS, new String[] {KEY_USER_ID}, KEY_USERNAME + "='" + username + "'", null, null, null, null);
		cursor.moveToFirst(); 
		int result = cursor.getInt(0);
		return result;
	}
	
	//Get Last Route id (to create new)
	int getNewRouteId(){
		Cursor cursor = db.query(TABLE_ROUTES,new String[] {KEY_ROUTE_ID,KEY_START_LATITUDE,KEY_START_LONGITUDE,KEY_FINISH_LATITUDE,KEY_FINISH_LONGITUDE,KEY_ROUTE_NAME},null, null, null, null, null);
		int result = cursor.getCount()+1;
		return result;
	}
	
	//Get Route from Route_Name
	Route getRouteByName(String routeName){
		Cursor cursor = db.query(TABLE_ROUTES,new String[] {KEY_ROUTE_ID,KEY_START_LATITUDE,KEY_START_LONGITUDE,KEY_FINISH_LATITUDE,KEY_FINISH_LONGITUDE,KEY_ROUTE_NAME},KEY_ROUTE_NAME + "='" + routeName + "'", null, null, null, null);
		cursor.moveToFirst();
		Route route = new Route(cursor.getInt(0) ,cursor.getDouble(1) , cursor.getDouble(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5));
		return route;
	}
	
	//Get Route from route_id
	Route getRouteByRouteId(int route_id){
		Cursor cursor = db.query(TABLE_ROUTES,new String[] {KEY_ROUTE_ID,KEY_START_LATITUDE,KEY_START_LONGITUDE,KEY_FINISH_LATITUDE,KEY_FINISH_LONGITUDE,KEY_ROUTE_NAME},KEY_ROUTE_ID + "='" + route_id + "'", null, null, null, null);
		cursor.moveToFirst();
		Route route = new Route(cursor.getInt(0) ,cursor.getDouble(1) , cursor.getDouble(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5));
		return route;
	}
	
	//Get Route Name from route_id
	String getRouteName(int route_id){
		Cursor cursor = db.query(TABLE_ROUTES, new String[] {KEY_ROUTE_NAME}, KEY_ROUTE_ID + "='" + route_id + "'", null, null, null, null);
		cursor.moveToFirst(); 			
		String result = cursor.getString(0);
		return result;
	}
	
	//read History Table for a userid and put the contents on a List
	List<History> getHistoryByUserId(int userid){
		List<History> resultList = new ArrayList<History>();
		Cursor cursor = db.query(TABLE_HISTORY,new String[] {KEY_HISTORY_ID,KEY_USER_ID2,KEY_ROUTE_ID2,KEY_DATE,KEY_DISTANCE,KEY_AVG_SPEED},KEY_USER_ID2 + "=" + userid,null, null, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				History history = new History(cursor.getInt(0),cursor.getInt(1), cursor.getInt(2),cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5));
				resultList.add(history);
				cursor.moveToNext();
			}
		}		
		return resultList;
	}
	
	//read History Table and return last record
	History getLastHistoryRecord(){
		Cursor cursor = db.rawQuery("SELECT * FROM history ORDER BY history_id DESC LIMIT 1",null);
		cursor.moveToFirst();
		History history = new History(cursor.getInt(0),cursor.getInt(1), cursor.getInt(2),cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5));
		return history;		
	}
	
	//read Points Table for a routeid and put the contents on a List
	List<Points> getPointsByRouteId(int routeid){
		List<Points> resultList = new ArrayList<Points>();
		Cursor cursor = db.query(TABLE_POINTS,new String[] {KEY_USER_ID3,KEY_ROUTE_ID3,KEY_LATITUDE,KEY_LONGITUDE,KEY_REPETITION},KEY_ROUTE_ID3 + "=" + routeid,null, null, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				Points points = new Points(cursor.getInt(0),cursor.getInt(1), cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(4));
				resultList.add(points);
				cursor.moveToNext();
			}
		}
		return resultList;
	}
	
	//read Points Table and return a list of users that ran a specific route
	List<Integer> getUsersByRouteId(int routeid,int userid){
		List<Integer> resultList = new ArrayList<Integer>();
		Cursor cursor = db.query(true, TABLE_POINTS,new String[] {KEY_USER_ID3}, KEY_ROUTE_ID3 + "=" + routeid + "and" + KEY_USER_ID3 + "!=" + userid , null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				resultList.add(Integer.parseInt(cursor.getString(0)));
				cursor.moveToNext();
			}
		}
		return resultList;
	}
	
	//read Points Table and return a list with the repetitions for a route for a single user
	List<Integer> getRepetitionsList(int routeid,int userid){
		List<Integer> resultList = new ArrayList<Integer>();
		Cursor cursor = db.query(true, TABLE_POINTS,new String[] {KEY_REPETITION}, KEY_ROUTE_ID3 + "=" + routeid + "and" + KEY_USER_ID3 + "!=" + userid , null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				resultList.add(Integer.parseInt(cursor.getString(0)));
				cursor.moveToNext();
			}
		}
		return resultList;
	}
	
	//read Points Table and return the points for a repetition
	List<Points> getPointsByRepetition(String routeid,String userid,String repetition){
		List<Points> resultList = new ArrayList<Points>();
		Cursor cursor = db.rawQuery("SELECT point_number,user_id3,route_id3,latitude,longitude,repetition FROM points WHERE route_id3 = ? AND user_id3 = ? AND repetition = ? ORDER BY point_number",new String[] {routeid,userid,repetition});
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				Points points = new Points(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getInt(5));
				resultList.add(points);
				cursor.moveToNext();
			}
		}
		return resultList;
	}
	
	//read Points Table and return the number of repetitions
	int getRepetitions(String routeid,String userid){
		Cursor cursor = db.rawQuery("SELECT distinct(repetition) FROM points WHERE route_id3 = ? AND user_id3 = ?", new String[] {routeid,userid});
		int repetitions = cursor.getCount();
		return repetitions;
	}
	
	//read Routes Table to check if a new route name already exists
	boolean routeNameExists(String route_name){
		Cursor cursor = db.query(TABLE_ROUTES, new String[] {KEY_ROUTE_NAME}, KEY_ROUTE_NAME + "='" + route_name + "'", null, null, null, null);
		if (cursor.moveToFirst()) {
			return true;
		}else {
			return false;
		}			
	}
	
	
	//write to Points Table
	void addPoint(int user_id,int route_id,double latitude,double longitude,int repetition){
		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID3, user_id);
		values.put(KEY_ROUTE_ID3,route_id);
		values.put(KEY_LATITUDE,latitude);
		values.put(KEY_LONGITUDE,longitude);
		values.put(KEY_REPETITION, repetition);
		
		db.insert(TABLE_POINTS, null, values);
	}
	
	//write to History Table
	void addHistory(int user_id,int route_id,String date,double distance,double avg_speed){
		ContentValues values = new ContentValues();
		values.put(KEY_USER_ID2, user_id);
		values.put(KEY_ROUTE_ID2,route_id);
		values.put(KEY_DATE,date);
		values.put(KEY_DISTANCE,distance);
		values.put(KEY_AVG_SPEED,avg_speed);
		
		db.insert(TABLE_HISTORY, null, values);
	}
	
	//write to Route Table
	void addRoute(double start_latitude,double start_longitude,double finish_latitude,double finish_longitude,String route_name){
		ContentValues values = new ContentValues();
		values.put(KEY_START_LATITUDE,start_latitude);
		values.put(KEY_START_LONGITUDE,start_longitude);
		values.put(KEY_FINISH_LATITUDE,finish_latitude);
		values.put(KEY_FINISH_LONGITUDE,finish_longitude);
		values.put(KEY_ROUTE_NAME,route_name);
		
		db.insert(TABLE_ROUTES, null, values);
	}
	
	//write to Users Table
	void addUser(String username,String password){
		ContentValues values = new ContentValues();
		values.put(KEY_USERNAME,username);
		values.put(KEY_PASSWORD,password);
		
		db.insert(TABLE_USERS, null, values);
	}

}
