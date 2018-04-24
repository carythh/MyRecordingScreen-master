package com.gson8.myscreenshot.fileselector.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTestUtil {
	private static final int seconds_of_1minute = 60;
	private static final int seconds_of_30minutes = 30 * 60;
	private static final int seconds_of_1hour = 60 * 60;
	private static final int seconds_of_1day = 24 * 60 * 60;
	private static final int seconds_of_15days = seconds_of_1day * 15;
	private static final int seconds_of_30days = seconds_of_1day * 30;
	private static final int seconds_of_6months = seconds_of_30days * 6;
	private static final int seconds_of_1year = seconds_of_30days * 12;
	/**
	 * 获取当前的时间
	 * 月日时分秒
	 * 
	 * @return
	 */
	public static String FormatTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
		String date = sDateFormat.format(new Date());
		return date;
	}
	/**
	 *  获取当前的时间
	 *  年月日
	 * @return
	 */
	public static String FormatTimea() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
		String date = sDateFormat.format(new Date());
		return date;
	}
	/**
	 * 时间戳转换
	 * 年月日
	 * */
    public static String getDateToString(String timechuo) {
    	
    	Date date = null;
		SimpleDateFormat sDateFormat = null;
		try {
			long time = new Long(timechuo);
			date = new Date(time*1000L);
			sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		} catch (NumberFormatException e) {
			return "";
		}
        
        return sDateFormat.format(date);
    }
	/**
	 * 将时间戳转为代表"距现在多久之前"的字符串
	 * @param timeStr	时间戳
	 * @return
	 */
	public static String getStandardDate(final String timeStr) {
		try {
		//Date或者String转化为时间戳
		SimpleDateFormat formata =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String timea="1970-01-06 11:45:55";
		Date datea = formata.parse(timeStr);
//		System.out.print("Format To times:"+date.getTime());
		StringBuffer sb = new StringBuffer();

		long t = Long.parseLong(datea.getTime()+"");
		long time = System.currentTimeMillis() - (t*1000);
		long mill = (long) Math.ceil(time /1000);//秒前

		long minute = (long) Math.ceil(time/60/1000.0f);// 分钟前

		long hour = (long) Math.ceil(time/60/60/1000.0f);// 小时

		long day = (long) Math.ceil(time/24/60/60/1000.0f);// 天前

		if (day - 1 > 0) {
			sb.append(day + "天");
		} else if (hour - 1 > 0) {
			if (hour >= 24) {
				sb.append("1天");
			} else {
				sb.append(hour + "小时");
			}
		} else if (minute - 1 > 0) {
			if (minute == 60) {
				sb.append("1小时");
			} else {
				sb.append(minute + "分钟");
			}
		} else if (mill - 1 > 0) {
			if (mill == 60) {
				sb.append("1分钟");
			} else {
				sb.append(mill + "秒");
			}
		} else {
			sb.append("刚刚");
		}
		if (!sb.toString().equals("刚刚")) {
			sb.append("前");

		}
			return sb.toString();
		}catch (Exception e){

		}
		return timeStr+"异常";
	}
	/**
	 * @return timtPoint距离现在经过的时间，分为
	 *         刚刚，1-29分钟前，半小时前，1-23小时前，1-14天前，半个月前，1-5个月前，半年前，1-xxx年前
	 */
	public static String getTimeElapse(final String timeStr) {
		//Date或者String转化为时间戳
		SimpleDateFormat formata =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String timea="1970-01-06 11:45:55";
		Date datea = null;
		try {
			datea = formata.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long nowTime = new Date().getTime() / 1000;
		//createTime是发表文章的时间
		long oldTime = datea.getTime()/1000;
		//elapsedTime是发表和现在的间隔时间
		long elapsedTime = nowTime - oldTime;
		if (elapsedTime < seconds_of_1minute) {
			return "刚刚";
		}
		if (elapsedTime < seconds_of_30minutes) {
			return elapsedTime / seconds_of_1minute + "分钟前";
		}
		if (elapsedTime < seconds_of_1hour) {
			return "半小时前";
		}
		if (elapsedTime < seconds_of_1day) {
			return elapsedTime / seconds_of_1hour + "小时前";
		}
		if (elapsedTime < seconds_of_15days) {
			return elapsedTime / seconds_of_1day + "天前";
		}
		if (elapsedTime < seconds_of_30days) {
			return "半个月前";
		}
		if (elapsedTime < seconds_of_6months) {
			return elapsedTime / seconds_of_30days + "月前";
		}
		if (elapsedTime < seconds_of_1year) {
			return "半年前";
		}
		if (elapsedTime >= seconds_of_1year) {
			return elapsedTime / seconds_of_1year + "年前";
		}
		return "";
	}

}
