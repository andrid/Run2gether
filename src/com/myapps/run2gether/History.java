package com.myapps.fitnessApp;

public class History {
	private int history_id;
	private int user_id;
	private int route_id;
	private String date;
	private double distance;
	private double avg_speed;
	
	public History(int history_id,int user_id,int route_id,String date, double distance,double avg_speed){
		this.history_id=history_id;
		this.user_id=user_id;
		this.route_id=route_id;
		this.date=date;
		this.distance=distance;
		this.avg_speed=avg_speed;
	}
	
	public int getHistory_id(){
		return history_id;
	}
	
	public int getRoute_id(){
		return route_id;
	}
	
	public int getUser_id(){
		return user_id;
	}
	
	public String getDate(){
		return date;
	}
	
	public double getDistance(){
		return distance;
	}
	
	public double getAvg_speed(){
		return avg_speed;
	}
}
