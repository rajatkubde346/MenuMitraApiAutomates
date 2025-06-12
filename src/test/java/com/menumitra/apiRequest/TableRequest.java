package com.menumitra.apiRequest;

public class TableRequest 
{
	private int outlet_id;
	private int section_id;
	private int user_id;
	private String app_source;
	
	
	
	public int getOutlet_id()
	{
		return outlet_id;
	}
	public void setOutlet_id(int outlet_id) 
	{
		this.outlet_id = outlet_id;
	}
	public int getSection_id() {
		return section_id;
	}
	public void setSection_id(int section_id) 
	{
		this.section_id = section_id;
	}
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	
	public String getApp_source() {
		return app_source;
	}
	public void setApp_source(String app_source) {
		this.app_source = app_source;
	}
}
