package com.myapps.fitnessApp;

public class Route {
	private int route_id;
	private double start_latitude;
	private double start_longitude;
	private double finish_latitude;
	private double finish_longitude;
	private String description;
	
	public Route(int route_id,double start_latitude,double start_longitude,double finish_latitude,double finish_longitude,String description){
		this.route_id=route_id;
		this.start_latitude=start_latitude;
		this.start_longitude=start_longitude;
		this.finish_latitude=finish_latitude;
		this.finish_longitude=finish_longitude;
		this.description=description;
	}
	
/*	public Route(double start_latitude,double start_longitude,double finish_latitude,double finish_longitude,String description){
		this.start_latitude=start_latitude;
		this.start_longitude=start_longitude;
		this.finish_latitude=finish_latitude;
		this.finish_longitude=finish_longitude;
		this.description=description;
	}*/
	
	public int getRoute_id(){
		return route_id;
	}
	
	public double getStart_latitude(){
		return start_latitude;
	}
	
	public double getStart_longitude(){
		return start_longitude;
	}
	
	public double getFinish_latitude(){
		return finish_latitude;
	}
	
	public double getFinish_longitude(){
		return finish_longitude;
	}
	
	public String getDescription(){
		return description;
	}
	
}


