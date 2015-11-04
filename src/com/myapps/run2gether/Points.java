package com.myapps.fitnessApp;

public class Points {
	private int point_number;
	private int user_id;
	private int route_id;
	private double latitude;
	private double longitude;
	private int repetition;
	
	public Points(int point_number,int user_id, int route_id, double latitude, double longitude, int repetition){
		this.point_number=point_number;
		this.user_id=user_id;
		this.route_id=route_id;
		this.latitude=latitude;
		this.longitude=longitude;
		this.repetition=repetition;
	}
	
	public Points(int user_id, int route_id, double latitude, double longitude, int repetition){
		this.user_id=user_id;
		this.route_id=route_id;
		this.latitude=latitude;
		this.longitude=longitude;
		this.repetition=repetition;
	}
	
	
	
	public int getPointNumber(){
		return point_number;
	}
	
	public int getUser_id(){
		return user_id;
	}
	
	public int getRoute_id(){
		return route_id;
	}
		
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public int getRepetition(){
		return repetition;
	}

}
