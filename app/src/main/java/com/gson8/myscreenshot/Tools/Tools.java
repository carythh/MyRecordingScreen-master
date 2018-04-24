package com.gson8.myscreenshot.Tools;

import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

public class Tools {
	/** 水平方向模糊�? */
	private static float hRadius = 10;
	/** 竖直方向模糊�? */
	private static float vRadius = 10;
	/** 模糊迭代�? */
	private static int iterations = 7;
	private static int ScreenWidth;
	private static int ScreenHeight;
	private static Point point;

	/** 现在的activity **/
	public static int getScreenWidth() {
		return ScreenWidth;
	}

	public static int getScreenHeight() {
		return ScreenHeight;
	}

	private static Editor editor;
	private static Tools jTools;
	private static SharedPreferences sharedPreferences;

	public Tools(Activity activity) {
		// graphics
		// point = new Point();
		// // 得到Display参数
		// activity.getWindowManager().getDefaultDisplay().getSize(point);
		// // 得到屏幕宽度
		// ScreenWidth = point.x;
		// // 得到屏幕高度
		// ScreenHeight = point.y;

		ScreenHeight = getpoitx(false, activity);
		ScreenWidth = getpoitx(true, activity);
		// 读取shared数据
		sharedPreferences = activity.getSharedPreferences("luping",
				Context.MODE_WORLD_READABLE);
		// 得到编辑状�?
		editor = sharedPreferences.edit();

	}

	private int getpoitx(boolean xory, Activity activity) {
		// Point outSize = new Point();
		if (xory) {
			return activity.getWindowManager().getDefaultDisplay().getWidth();
		} else {
			return activity.getWindowManager().getDefaultDisplay().getHeight();
		}
	}

	/**
	 * 初始化工具类
	 * 
	 * @param activity
	 */
	public static Tools Initialize(Activity activity) {
		if (null != jTools) {
			return jTools;
		} else {
			jTools = new Tools(activity);
			return jTools;
		}
	}

	/**
	 * 向sharedPreferences写入数据
	 */
	public void EditSP(String key, Set<String> values) {
		editor.putStringSet(key, values);
		editor.commit();
	}

	public void EditSP(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void EditSP(String key, Boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void EditSP(String key, Float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public void EditSP(String key, Integer value) {
		editor.putInt(key, value);
		editor.commit();
	}


	public void EditSP(String key, Long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	/**
	 * 从sharedPreferences读取数据
	 */
	public String ReadSP(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	public Boolean ReadSP(String key, Boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	public Integer ReadSP(String key, Integer defValue) {
		return sharedPreferences.getInt(key, defValue);
	}



	public Float ReadSP(String key, Float defValue) {
		return sharedPreferences.getFloat(key, defValue);
	}

	public Long ReadSP(String key, Long defValue) {
		return sharedPreferences.getLong(key, defValue);
	}

	public void Clear(String key, Integer value) {
		editor.putInt(key, value);
		editor.clear().commit();
	}



	/**
	 * 处理�?��的log日志
	 */
	public static void PrintLog(String tag, Exception e) {
		Log.d(tag, e.toString());
	}

	/**
	 * 处理自定义log日志
	 */
	public static void PrintLog(String tag, String msg) {
		Log.d(tag, msg);
	}

	/**
	 * 显示短toast
	 */
	public static void ToastShot(Activity activity, String message) {
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示长toast
	 */
	public static void ToastLong(Activity activity, String message) {
		Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * 显示自定义toast
	 */
	public static void ToastCommon(Activity activity, String message, int delay) {
		Toast.makeText(activity, message, delay).show();
	}

	/**
	 * 得到listview逐条加载动画
	 * 
	 * @return
	 */
	public static LayoutAnimationController getListAnim() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}

	/**
	 * 高斯处理
	 * 
	 * @param bmp
	 * @return
	 */
	public static Drawable BoxBlurFilter(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < iterations; i++) {
			blur(inPixels, outPixels, width, height, hRadius);
			blur(outPixels, inPixels, height, width, vRadius);
		}
		blurFractional(inPixels, outPixels, width, height, hRadius);
		blurFractional(outPixels, inPixels, height, width, vRadius);
		bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	private static void blur(int[] in, int[] out, int width, int height,
			float radius) {
		int widthMinus1 = width - 1;
		int r = (int) radius;
		int tableSize = 2 * r + 1;
		int divide[] = new int[256 * tableSize];

		for (int i = 0; i < 256 * tableSize; i++)
			divide[i] = i / tableSize;

		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;
			int ta = 0, tr = 0, tg = 0, tb = 0;

			for (int i = -r; i <= r; i++) {
				int rgb = in[inIndex + clamp(i, 0, width - 1)];
				ta += (rgb >> 24) & 0xff;
				tr += (rgb >> 16) & 0xff;
				tg += (rgb >> 8) & 0xff;
				tb += rgb & 0xff;
			}

			for (int x = 0; x < width; x++) {
				out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
						| (divide[tg] << 8) | divide[tb];

				int i1 = x + r + 1;
				if (i1 > widthMinus1)
					i1 = widthMinus1;
				int i2 = x - r;
				if (i2 < 0)
					i2 = 0;
				int rgb1 = in[inIndex + i1];
				int rgb2 = in[inIndex + i2];

				ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
				tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
				tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
				tb += (rgb1 & 0xff) - (rgb2 & 0xff);
				outIndex += height;
			}
			inIndex += width;
		}
	}

	private static void blurFractional(int[] in, int[] out, int width,
			int height, float radius) {
		radius -= (int) radius;
		float f = 1.0f / (1 + 2 * radius);
		int inIndex = 0;

		for (int y = 0; y < height; y++) {
			int outIndex = y;

			out[outIndex] = in[0];
			outIndex += height;
			for (int x = 1; x < width - 1; x++) {
				int i = inIndex + x;
				int rgb1 = in[i - 1];
				int rgb2 = in[i];
				int rgb3 = in[i + 1];

				int a1 = (rgb1 >> 24) & 0xff;
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;
				int a2 = (rgb2 >> 24) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = rgb2 & 0xff;
				int a3 = (rgb3 >> 24) & 0xff;
				int r3 = (rgb3 >> 16) & 0xff;
				int g3 = (rgb3 >> 8) & 0xff;
				int b3 = rgb3 & 0xff;
				a1 = a2 + (int) ((a1 + a3) * radius);
				r1 = r2 + (int) ((r1 + r3) * radius);
				g1 = g2 + (int) ((g1 + g3) * radius);
				b1 = b2 + (int) ((b1 + b3) * radius);
				a1 *= f;
				r1 *= f;
				g1 *= f;
				b1 *= f;
				out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
				outIndex += height;
			}
			out[outIndex] = in[width - 1];
			inIndex += width;
		}
	}

	private static int clamp(int x, int a, int b) {
		return (x < a) ? a : (x > b) ? b : x;
	}


}