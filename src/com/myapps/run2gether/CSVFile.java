package com.myapps.fitnessApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import android.content.Context;

public class CSVFile {
	private Context context;
	
	// constructor
	public CSVFile(Context context) {
		this.context = context.getApplicationContext();
	}
	
	
	
	//Write to Points CSV file
	public void writePoints(String filename,String user_id,String route_id,String latitude,String longitude){
		File file = new File(android.os.Environment.getExternalStorageDirectory(),filename);
		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(file,true);
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
			e1.printStackTrace();
		}

		try {
		  outputStream.write(user_id.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(route_id.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(latitude.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(longitude.getBytes());
		  outputStream.write(";".getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	//Write to History CSV file
	public void writeHistory(String filename,String user_id,String route_id,String date,String distance,String avg_speed,String rank){
		File file = new File(android.os.Environment.getExternalStorageDirectory(),filename);
		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(file,true);
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
			e1.printStackTrace();
		}

		try {
		  outputStream.write(user_id.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(route_id.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(date.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(distance.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(avg_speed.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(rank.getBytes());
		  outputStream.write(";".getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	//Write to Route CSV file
	public void writeRoutes(String filename,String start_latitude,String start_longitude,String finish_latitude,String finish_longitude,String route_name){
		File file = new File(android.os.Environment.getExternalStorageDirectory(),filename);
		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(file,true);
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
			e1.printStackTrace();
		}

		try {
		  outputStream.write(start_latitude.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(start_longitude.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(finish_latitude.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(finish_longitude.getBytes());
		  outputStream.write(",".getBytes());
		  outputStream.write(route_name.getBytes());
		  outputStream.write(";".getBytes());
		  outputStream.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	//Write to log file for troubleshooting
	public void writeToLog(String filename,String data){
		File file = new File(android.os.Environment.getExternalStorageDirectory(),filename);
		FileOutputStream outputStream=null;
		try {
			outputStream = new FileOutputStream(file,true);
		} catch (FileNotFoundException e1) {
			System.out.println("File not found");
			e1.printStackTrace();
		}
		
		try {
			outputStream.write(data.getBytes());
			outputStream.write(",".getBytes());
			outputStream.close();
		} catch (Exception e) {
			  e.printStackTrace();
		}
	}

}
