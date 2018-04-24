package com.gson8.myscreenshot.video;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

public class MusicUtils {

	/**
	 * �õ�ý����е�ȫ������
	 */
	public static ArrayList<Music> getAllSongs(Context context) {
		Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ARTIST, MediaColumns.DATA,MediaStore.Audio.Media.DURATION },
				MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);

		if (c == null || c.getCount() == 0) {
			return null;
		}
		ArrayList<Music> musics=new ArrayList<Music>();
		int id = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
		int name = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
		int singername = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
		int url = c.getColumnIndex(MediaColumns.DATA);
		int duration=c.getColumnIndex(MediaStore.Audio.Media.DURATION);
		while(c.moveToNext()){
			Music m=new Music();
			m.setId(c.getLong(id));
			m.setName(c.getString(name));
			m.setSingerName(c.getString(singername));
			m.setUrl(c.getString(url));
			m.setDuration(c.getInt(duration));
			musics.add(m);
		}
		c.close();
		return musics;
	}

	public static Cursor query(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		ContentResolver resolver = context.getContentResolver();
		return resolver.query(uri, projection, selection, selectionArgs,
				sortOrder);
	}
	
}
