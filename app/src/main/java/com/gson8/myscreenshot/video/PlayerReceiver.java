package com.gson8.myscreenshot.video;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gson8.myscreenshot.R;

public class PlayerReceiver extends BroadcastReceiver {

	MyApplication myApplication=MyApplication.getInstance();
	
	public PlayerReceiver(){
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getStringExtra("action");
		Log.d("action", action);
			if(action.equals("close")){
//				if(MyApplication.getInstance().mShoter != null) {
//					MyApplication.getInstance().mShoter.stopShot();
//					MyApplication.getInstance().mShoter = null;
//				}

				myApplication.main.shot();
				myApplication.main.mp3(1);


//				myApplication.mBtnShotStart.setText("开始录制屏幕");
			//	myApplication.notificationManager.cancelAll();

			}else if(action.equals("stop")){
//				myApplication.notification.contentView.setImageViewResource(R.id.paly_pause_music, R.drawable.music_play);
				myApplication.notification.contentView.setTextViewText(R.id.st,"录制状态：开始录制");
				myApplication.notification.contentView.setTextViewText(R.id.close,"开始录制");
				myApplication.notificationManager.notify(1, myApplication.notification);
			}else if(action.equals("start")){
//				myApplication.notification.contentView.setImageViewResource(R.id.paly_pause_music, R.drawable.music_play);
				myApplication.notification.contentView.setTextViewText(R.id.close,"停止录制");
				myApplication.notification.contentView.setTextViewText(R.id.st,"录制状态：正在录制");
				myApplication.notificationManager.notify(1, myApplication.notification);
			}else if(action.equals("app")){
//				if(myApplication.mBtnShotStart.getText().equals("开始录制屏幕")){
//
//				}else{
//
//				}
////				myApplication.notification.contentView.setImageViewResource(R.id.paly_pause_music, R.drawable.music_play);
//				myApplication.notification.contentView.setTextViewText(R.id.st,"录制状态：开始录制");
//				myApplication.notificationManager.notify(1, myApplication.notification);
			}
	}

}
