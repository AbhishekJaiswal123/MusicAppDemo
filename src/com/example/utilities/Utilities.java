package com.example.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.models.MUsicModels;

public class Utilities {

	private Context mContext;
	private Cursor lCursor;
	private MUsicModels model;
	private ArrayList<MUsicModels> lSongList;
	public NotificationManager notificationManager;

	public Utilities(Context pContext) {

		mContext = pContext;
	}

	public Utilities() {
	}

	public String getMusicBitmap(String pAlbum_ID) {

		Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
		Uri uri = ContentUris.withAppendedId(sArtworkUri,
				Integer.valueOf(pAlbum_ID));

		return uri.toString();

	}

	@SuppressLint("DefaultLocale") public String FormatTime(long millis) {

		String hms = String.format(
				"%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(millis)));

		return hms;

	}

	public int getProgressPercentage(long currentDuration, long totalDuration) {
		Double percentage = (double) 0;

		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		// calculating percentage
		percentage = (((double) currentSeconds) / totalSeconds) * 100;

		return percentage.intValue();
	}

	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double) progress) / 100) * totalDuration);

		// return current duration in milliseconds
		return currentDuration * 1000;
	}

	// public void setNotification(String pSongName,String pArtist){
	// String ns = Context.NOTIFICATION_SERVICE;
	// notificationManager = (NotificationManager)mContext.getSystemService(ns);
	//
	// @SuppressWarnings("deprecation")
	// Notification notification = new Notification
	// (R.drawable.ic_action_play, null, System.currentTimeMillis());
	//
	// RemoteViews notificationView = new RemoteViews
	// (mContext.getPackageName(), R.layout.notification_for_music);
	// notificationView.setTextViewText(R.id.nSongName, pSongName);
	// notificationView.setTextViewText(R.id.nSongArtist, pArtist);
	// //the intent that is started when the notification is clicked (works)
	// Intent notificationIntent = new Intent(mContext,PlayerActivity.class);
	//
	// PendingIntent pendingNotificationIntent = PendingIntent.getActivity
	// (mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	//
	// notification.contentView = notificationView;
	// notification.contentIntent = pendingNotificationIntent;
	// notificationManager.notify(1, notification);
	// }

	public ArrayList<MUsicModels> getSongList() {
		lSongList = new ArrayList<MUsicModels>();
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		// your projection statement
		String[] projection = { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM

		};

		// query
		lCursor = mContext.getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
				selection, null, null);

		if (lCursor != null) {
			lCursor.moveToFirst();

			do {
				// while(lCursor.moveToNext()){
				model = new MUsicModels();
				model.SongId = lCursor.getInt(0);
				model.Image = lCursor.getString(7);
				model.SongName = lCursor.getString(2);
				model.Artist = lCursor.getString(1);
				model.SongLength = lCursor.getLong(5);
				model.AlbumID = lCursor.getString(6);

				Uri trackUri = ContentUris
						.withAppendedId(
								android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
								lCursor.getLong(0));

				model.Uri = trackUri.toString();

				lSongList.add(model);

			} while (lCursor.moveToNext());

		}

		lCursor.close();
		Collections.sort(lSongList, new MUsicModels.OrderByTextAZ());
		return lSongList;
	}

}
