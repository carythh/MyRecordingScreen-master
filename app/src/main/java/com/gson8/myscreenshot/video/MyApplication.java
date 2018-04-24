package com.gson8.myscreenshot.video;


import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.widget.Button;
import android.widget.TextView;

import com.gson8.myscreenshot.MainActivity;

public class MyApplication extends Application {

	public PlayerReceiver receiver;
	public MyShoter myShoter;
//	public MyShoterService myShoterService;
	public Notification notification;
	public NotificationManager notificationManager;

	public MainActivity main;
	public boolean video=false;
	private static MyApplication myApplication;
	@Override
	public void onCreate() {
		super.onCreate();
		myApplication=this;
	}

	public static MyApplication getInstance(){
		return myApplication;
	}
}
