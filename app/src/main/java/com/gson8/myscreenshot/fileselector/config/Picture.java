package com.gson8.myscreenshot.fileselector.config;

import android.graphics.Bitmap;

public class Picture {
	
	private Bitmap bitmap;
	private String path;
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Picture(Bitmap bitmap, String path) {
		super();
		this.bitmap = bitmap;
		this.path = path;
	}
	public Picture() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	

}
