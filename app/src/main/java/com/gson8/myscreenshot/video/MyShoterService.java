package com.gson8.myscreenshot.video;

/*
 * ScreenShot making by Syusuke/琴声悠扬 on 2016/5/31
 * E-Mail: Zyj7810@126.com
 * Package: gson8.com.vedioshot.shots.MyShoter
 * Description: null
 */

import android.app.IntentService;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyShoterService extends IntentService {

    private static final String TAG = MyShoterService.class.getSimpleName();

    private static final String VIDEO_MIME_TYPE = "video/avc";
    private static final int IFRAME_INTERVAL = 10;
    private static final int TIMEOUT_US = 10000;



    /**
     * 录制的分辨率
     */
    private int mWidth;
    private int mHeight;

    /**
     * 录制的比特率
     */
    private int mBitRate;

    /**
     * 录制的帧数
     */
    private int mFrameRate;     // 30 FPS

    /**
     * 这个数传 1 就可以
     */
    private int mDpi;

    /**
     * 文件保存路径
     */
    private String mSaveFilePath;

    private MediaProjection mMediaProjection;
    private MediaCodec mVideoEncoder;
    private Surface mSurface;
    private MediaMuxer mMuxer;

    private boolean mMuxerStarted = false;
    private int mVideoTrackIndex = -1;

    private AtomicBoolean mAtomicQuit = new AtomicBoolean(false);

    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private VirtualDisplay mVirtualDisplay;


    public boolean istype=true;
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public  MyShoterService getService() {
            Log.d("playerService", "getService");
            return MyShoterService.this;
        }
    }
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }
    public MyShoterService(String name) {
        super(name);
    }

    public MyShoterService() {
        super("MyShoterService!");
    }

    public void MyShoterSet(int width, int height, int bitRate, int dpi, int fps,
                           MediaProjection mediaProjection, String filePath) {
        this.mWidth = width;
        this.mHeight = height;
        this.mBitRate = bitRate;
        this.mDpi = dpi;
        this.mFrameRate = fps;
        this.mMediaProjection = mediaProjection;
        if(!filePath.endsWith(".mp4"))
            filePath += ".mp4";
        this.mSaveFilePath = filePath;
    }
    public void MyShoterService(MediaProjection mMediaProjection, String filePath) {
        MyShoterSet(360, 640, 1000000, 1, 5, mMediaProjection, filePath);
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void stopShot() {
        istype=false;
        mAtomicQuit.set(true);
    }
    @Override
    public void onDestroy() {

    }

    public void playMusic() {

        Log.e(TAG, "run: " + mWidth + "   " + mHeight + "  " + mBitRate + "  " + mDpi + "  " +
                mFrameRate);

        try {

            prepareEncoder();
            mMuxer = new MediaMuxer(mSaveFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            Log.e(TAG, "run: mMuxer init");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mVirtualDisplay =
                        mMediaProjection.createVirtualDisplay(TAG + "_Video", mWidth, mHeight, mDpi,
                                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mSurface, null, null);
            }
            shotDisplay();
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Muxur:::", e);
        } finally {
            releaseEncoder();
        }
    }

    private void prepareEncoder() throws IOException {

        MediaFormat format =
                MediaFormat.createVideoFormat(VIDEO_MIME_TYPE, mWidth, mHeight);

        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mFrameRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);

        mVideoEncoder = MediaCodec.createEncoderByType(VIDEO_MIME_TYPE);
        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        mSurface = mVideoEncoder.createInputSurface();

        mVideoEncoder.start();
    }


    private void shotDisplay() {
        while(!mAtomicQuit.get()) {
            int bufferIndex = mVideoEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);

            if(bufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                resetOutputFormat();
            } else if(bufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                SystemClock.sleep(10);
            } else if(bufferIndex >= 0) {
                if(!mMuxerStarted) {
                    throw new IllegalStateException("MediaMuxer is  not add the  addTrack format ");
                }

                encodeToVideoTrack(bufferIndex);

                mVideoEncoder.releaseOutputBuffer(bufferIndex, false);
            }
        }
    }

    private void resetOutputFormat() {

        if(mMuxerStarted) {
            throw new IllegalStateException("format changed");
        }
        MediaFormat newFormat = mVideoEncoder.getOutputFormat();

        mVideoTrackIndex = mMuxer.addTrack(newFormat);
        mMuxer.start();
        mMuxerStarted = true;
    }

    private void encodeToVideoTrack(int bufferIndex) {
        ByteBuffer encodeData = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            encodeData = mVideoEncoder.getOutputBuffer(bufferIndex);
        }

        if((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
            mBufferInfo.size = 0;
        }

        if(mBufferInfo.size == 0) {
            encodeData = null;
        } else {
            //还不清楚到哪里去的.
        }

        if(encodeData != null) {
            encodeData.position(mBufferInfo.offset);
            encodeData.limit(mBufferInfo.offset + mBufferInfo.size);

            mMuxer.writeSampleData(mVideoTrackIndex, encodeData, mBufferInfo);
        }


    }

    private void releaseEncoder() {
        if(mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }

        if(mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }

        if(mMediaProjection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection.stop();
            }
            mMediaProjection = null;
        }

        if(mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }

        mBufferInfo = null;
        mVideoTrackIndex = -1;

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String type = intent.getStringExtra("action");
        Log.d("intent", type);
        if (type.equals("start")) {

            Intent i=new Intent(this,PlayerReceiver.class);
            i.putExtra("action", "start");
            sendBroadcast(i);

            Statr();
        } else if (type.equals("stop")) {

            Intent i=new Intent(this,PlayerReceiver.class);
            i.putExtra("action", "stop");
            sendBroadcast(i);
            stopShot();
        }else if (type.equals("close")){

            Intent i=new Intent(this,PlayerReceiver.class);
            i.putExtra("action", "close");
            sendBroadcast(i);

        }

    }

    public void Statr(){
        istype=true;
        new MyTimerThread().start();
    }
    private class MyTimerThread extends Thread {
        @Override
        public void run() {
            playMusic();
        }
    }
}
