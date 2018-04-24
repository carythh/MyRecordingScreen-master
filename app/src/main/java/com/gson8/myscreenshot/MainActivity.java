package com.gson8.myscreenshot;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.gson8.myscreenshot.Tools.LMTool;
import com.gson8.myscreenshot.Tools.Tools;
import com.gson8.myscreenshot.fileselector.config.FileConfig;
import com.gson8.myscreenshot.fileselector.dialog.FileDialog;
import com.gson8.myscreenshot.fileselector.utils.DateTestUtil;
import com.gson8.myscreenshot.video.AudioEncoder;
import com.gson8.myscreenshot.video.AudioRecorder;
import com.gson8.myscreenshot.video.MyShoter;
import com.gson8.myscreenshot.video.DpiBean;
import com.gson8.myscreenshot.video.MyApplication;
import com.gson8.myscreenshot.video.MyShoterService;
import com.gson8.myscreenshot.video.PlayerReceiver;
import com.gson8.myscreenshot.video.SpinnerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.gson8.myscreenshot.Tools.LMTool.DismissDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    public static final int REQUEST_CODE = 0x123;
    public static final int REQUEST_SDCARD_CODE = 0x1243;
    public static final int REQUEST_SETTING_CODE = 0x1223;
    private static final String TAG = "TAGGG";

    private final int PERMISSION_REQ = 345;
    private Button mBtnShotStart,id_video_mp3,id_video_mp3and;
    private Button mBtnOp;

    private Spinner mDpiSizeSp;
    private SpinnerAdapter mDpiAdapter;

    private Spinner mBitRateSp;
    private SpinnerAdapter mBitRateAdapter;

    private Spinner mFpsSp;
    private SpinnerAdapter mFpsAdapter;

    private Switch mShowTouchSwitch;


    //初始化分辨率
    private DpiBean mDpiSizeBean;
    private List<DpiBean> mSizes;


    //初始化bitRate
    private int mBitRate = 600000;         //6 Mbps
    private Integer[] BIT_RATE_DATA =
            {500000, 1000000, 2000000, 2500000, 3000000, 4000000, 5000000, 6000000, 8000000,
                    10000000, 12000000};

    //初始化FPS
    private int mFps = 5;
    private Integer[] FPS_DATA = {5, 6, 8, 10, 12, 15, 18, 20, 24, 30};

    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;


    private File mFile=null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private AudioRecorder audioRecorder;
    private AudioEncoder audioEncoder;
    private File filemp3=null;
    private Handler myHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(MainActivity.this,"合并异常",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this,"合并成功",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"请录制",Toast.LENGTH_SHORT).show();
                    break;

            }
            LMTool.DismissDialog();
            super.handleMessage(msg);
        }
    };

    private Tools tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApplication.getInstance().main=this;
        initView();

        tools =Tools.Initialize(this);

        LMTool.NowActivity=this;
        initDPIData();

        initEvent();

        mMediaProjectionManager =
                (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);

        checkSDCardPermission();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    /**
     * 双击退出函数
     */
    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            MyApplication.getInstance().notificationManager.cancelAll();
            finish();
//            System.exit(0);
        }
    }
    /**
     * @param keyCode
     * @param event
     * @return false执行完操作不拦截，true执行完操作就拦截
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(MyApplication.getInstance().myShoter!=null) {
                MyApplication.getInstance().myShoter.stopShot();
                MyApplication.getInstance().myShoter = null;
                mBtnShotStart.setText("开始录制屏幕");
                Toast.makeText(this, "录制已经退出", Toast.LENGTH_SHORT).show();
            }else{

                ExitApp(); // 调用双击退出函数

            }
            return false;
            //	myApplication.notificationManager.cancelAll();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {

        mBtnShotStart = (Button) findViewById(R.id.id_shot_video);
        id_video_mp3 = (Button) findViewById(R.id.id_video_mp3);
        id_video_mp3and = (Button) findViewById(R.id.id_video_mp3and);
        mBtnOp = (Button) findViewById(R.id.id_video_op);
        mDpiSizeSp = (Spinner) findViewById(R.id.id_sp_select_dpi);
        mBitRateSp = (Spinner) findViewById(R.id.id_sp_select_mbps);
        mFpsSp = (Spinner) findViewById(R.id.id_sp_select_fps);
        mShowTouchSwitch = (Switch) findViewById(R.id.id_show_touch);

//����Notification
        NotificationManager manager = showCustomView();
        MyApplication.getInstance().notificationManager = manager;

    }

    private NotificationManager showCustomView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.music_notification);

//        Intent reActivity=new Intent(this,MainActivity.class);
//        PendingIntent pIntent=PendingIntent.getActivity(this, 0, reActivity, 0);
//        remoteViews.setOnClickPendingIntent(R.id.ll_parent, pIntent);
        //���ð�ť�¼�
//        Intent pauaseOrStartIntent=new Intent(this,PlayerReceiver.class);
//        pauaseOrStartIntent.putExtra("action", "app");
//        PendingIntent pausepi = PendingIntent.getBroadcast(this, 1, pauaseOrStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.st, pausepi);//----���ö�Ӧ�İ�ťID���

//        Intent reActivity=new Intent(this,MainActivity.class);
//        PendingIntent pIntent=PendingIntent.getActivity(this, 0, reActivity, 0);
//        remoteViews.setOnClickPendingIntent(R.id.ll_parent, pIntent);
//        Intent pauaseOrStartIntent=new Intent(this,PlayerReceiver.class);
//        pauaseOrStartIntent.putExtra("action", "close");
//        PendingIntent pausepi = PendingIntent.getService(this, 1, pauaseOrStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.close, pausepi);

        Intent closeIntent = new Intent(this, PlayerReceiver.class);
        closeIntent.putExtra("action", "close");
        closeIntent.setAction("com.gson8.myscreenshot.video");
        PendingIntent closepi = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.close, closepi);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
        builder.setContent(remoteViews).setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setTicker("录屏大师");
        Notification notification = builder.build();
        MyApplication.getInstance().notification = notification;
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
        return manager;
    }

    private void initDPIData() {
        mSizes = new ArrayList<>();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDpiSizeBean = new DpiBean(metrics.widthPixels, metrics.heightPixels);
        if (mSizes.size() == 0) {
            int h = mDpiSizeBean.getHeight();
            if (h > 1920)
                mSizes.add(mDpiSizeBean);
            if (h == 1920)
                mSizes.add(new DpiBean(1080, 1920));
            if (h >= 1280)
                mSizes.add(new DpiBean(720, 1280));
            if (h >= 960)
                mSizes.add(new DpiBean(540, 960));
            if (h >= 854)
                mSizes.add(new DpiBean(480, 854));
            if (h >= 640)
                mSizes.add(new DpiBean(360, 640));
            if (h >= 426)
                mSizes.add(new DpiBean(240, 426));
        }
    }

    private void initEvent() {
        mBtnShotStart.setOnClickListener(this);
        mBtnOp.setOnClickListener(this);
        mShowTouchSwitch.setOnCheckedChangeListener(this);
        id_video_mp3.setOnClickListener(this);
        id_video_mp3and.setOnClickListener(this);

        mDpiAdapter = new SpinnerAdapter(this, mSizes.toArray());
        mDpiSizeSp.setAdapter(mDpiAdapter);

        mBitRateAdapter = new SpinnerAdapter(this, BIT_RATE_DATA);
        mBitRateSp.setAdapter(mBitRateAdapter);

        mFpsAdapter = new SpinnerAdapter(this, FPS_DATA);
        mFpsSp.setAdapter(mFpsAdapter);


        mDpiSizeSp.setOnItemSelectedListener(this);
        mBitRateSp.setOnItemSelectedListener(this);
        mFpsSp.setOnItemSelectedListener(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkSDCardPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            //没有权限,去申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SDCARD_CODE);

        }
    }

    private void checkTwoSettings() {
        boolean retVal = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(this);
        }
        if (!retVal) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * 显示触摸操作
     *
     * @param b
     */
    public void showTouchDot(boolean b) {
        if (b) {
            Settings.System.putInt(getContentResolver(),
                    "show_touches", 1);
        } else {
            Settings.System.putInt(getContentResolver(),
                    "show_touches", 0);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_SDCARD_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "没有写入内存卡的权限", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_SETTING_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            } else {
                checkTwoSettings();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.id_show_touch:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(this)) {
                        mShowTouchSwitch.setChecked(false);
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_SETTING_CODE);
                    } else {
                        showTouchDot(isChecked);
                    }
                } else {
                    // < 6.0
                    showTouchDot(isChecked);
                }
                break;
        }
    }

    public void mp3(int type) {
        //判断是否有该权限
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            //用户拒绝该权限时 下次再点击 则会显示相关解释（用户拒绝时 shouldShowRequestPermissionRationale 下次访问返回true）
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(MainActivity.this,"jie shi",Toast.LENGTH_LONG).show();
//                Log.e(TAG, "onClick: shouldShowRequestPermissionRationale true" );
                return;
            } else {
//                Log.e(TAG, "onClick: shouldShowRequestPermissionRationale false" );
            }
            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO},PERMISSION_REQ);
//            Log.e(TAG, "onClick: requestPermissions");
            return;
        }
//        Toast.makeText(MainActivity.this,"had write permission!",Toast.LENGTH_LONG).show();

        if(audioRecorder!=null){


            audioRecorder.stopAudioRecording();
            audioEncoder.stop();
            audioRecorder=null;
            id_video_mp3.setText("录制音频");
        }else{
            id_video_mp3.setText("停止录制音频");
            getRandomFileName();
            if(type==1) {
                filemp3 = new File(Environment.getExternalStorageDirectory() + "/a_video/", "Video_m" +
                        DateTestUtil.FormatTimea() + ".mp4");
                tools.EditSP("file2",filemp3.getAbsolutePath());
            }else{
                filemp3 = new File(Environment.getExternalStorageDirectory() + "/a_video/", "Video_" +
                        DateTestUtil.FormatTimea() + ".m4a");
            }
            if (!filemp3.exists()) {
                filemp3.getParentFile().mkdirs();
                try {
                    filemp3.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            audioRecorder = AudioRecorder.getInstance(filemp3);
            audioEncoder = new AudioEncoder();
            audioRecorder.setAudioEncoder(audioEncoder);
            audioRecorder.startAudioRecording();
        }


    }
    public void shot() {
        Intent i = new Intent(this,PlayerReceiver.class);

        if (MyApplication.getInstance().myShoter!=null) {
            MyApplication.getInstance().myShoter.stopShot();
            MyApplication.getInstance().myShoter = null;
            mBtnShotStart.setText("开始录制屏幕");
            i.putExtra("action", "stop");


        } else {
            i.putExtra("action", "start");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent captureIntent = null;
                captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(captureIntent, REQUEST_CODE);
            }

        }
        sendBroadcast(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            startShotNow(resultCode, data);
        }
        if (requestCode == REQUEST_SETTING_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    //检查返回结果
                    Toast.makeText(this, "OK",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "没有修改系统设置的权限",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void startShotNow(int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection =
                    mMediaProjectionManager.getMediaProjection(resultCode, data);
        }

        if (mMediaProjection == null) {
            Log.e(TAG, "MediaProjection is null");
            return;
        }

        mFile = new File(getRandomFileName());

        tools.EditSP("file1",mFile.getAbsolutePath());
        if (!mFile.exists()) {
            mFile.getParentFile().mkdirs();
            try {
                mFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "startShotNow: " + mDpiSizeBean.getHeight() + "  " + mBitRate + "  " + mFps);

        MyApplication.getInstance().myShoter = new MyShoter(mDpiSizeBean.getWidth(), mDpiSizeBean.getHeight(), mBitRate, 1,
                mFps, mMediaProjection, mFile.getAbsolutePath());
        new Thread(MyApplication.getInstance().myShoter).start();
        mBtnShotStart.setText("停止录制");
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (MyApplication.getInstance().mShoter != null) {
//            MyApplication.getInstance().mShoter.stopShot();
//            MyApplication.getInstance().mShoter = null;
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_shot_video:
                shot();
                mp3(1);
                break;
            case R.id.id_video_op:
                opTheVideo();
                break;
            case R.id.id_video_mp3:
                mp3(0);
                break;
            case R.id.id_video_mp3and:

                    mux();

                break;
        }
    }
    public void mux()
    {
        LMTool.ShowDialog();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Message();
                    try {
                        String audioEnglish =tools.ReadSP("file2","");
                        String video =tools.ReadSP("file1","");
                    if(filemp3!=null&&mFile!=null&&!(audioEnglish.equals(""))) {

                        Movie countVideo = MovieCreator.build(video);
                        Movie countAudioEnglish = MovieCreator.build(audioEnglish);

                        Track audioTrackEnglish = countAudioEnglish.getTracks().get(0);
                        countVideo.addTrack(audioTrackEnglish);
                        {

                            Container out = new DefaultMp4Builder().build(countVideo);
                            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/a_video/Video" +
                                    DateTestUtil.FormatTimea()
                                    + ".mp4"));
                            out.writeContainer(fos.getChannel());
                            fos.close();
                        }

                        try{
                            File file1=new File(audioEnglish);
                            file1.delete();
                            File file2=new File(video);
                            file2.delete();

                            mFile=null;
                            filemp3=null;
                        }catch (Exception e){
                            mFile=null;
                            filemp3=null;
                        }

                        message.what = 1;

                    }else{
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        message.what =2;
//            Toast.makeText(this,"请录制",Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException c) {
                            c.printStackTrace();
                        }
                        message.what =0;

                }
                    myHandler.sendMessage(message);
                }
            }).start();

    }
    private void opTheVideo() {

        FileConfig config = new FileConfig();

        FileDialog fileDialog = new FileDialog(this, config);
        fileDialog.showDialog();
//        fileDialog.setOnFileSelectFinish(OnFileSelectFinish);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.id_sp_select_dpi:
                mDpiSizeBean = mSizes.get(position);
                break;
            case R.id.id_sp_select_mbps:
                mBitRate = BIT_RATE_DATA[position];
                break;
            case R.id.id_sp_select_fps:
                mFps = FPS_DATA[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * 返回一个文件名
     *
     * @return
     */
    public String getRandomFileName() {
        return Environment.getExternalStorageDirectory() + "/a_video/Video_" +
                DateTestUtil.FormatTimea()
                + ".mp4";
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.gson8.myscreenshot/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.gson8.myscreenshot/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


}
