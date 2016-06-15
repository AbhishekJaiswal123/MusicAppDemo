package com.example.musicproject;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.example.models.MUsicModels;
import com.example.musicproject.application.MyApplication;
import com.example.musicproject.service.MusicService;
import com.example.utilities.AnimateFirstDisplayListener;
import com.example.utilities.PositionGlobal;
import com.example.utilities.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MusicPlayerActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {

	private ArrayList<MUsicModels> SONG_LIST;
	private ImageButton play_btn;
	private ImageButton song_list;
	private ImageView AlbumImage;
	private TextView mCurrentDuration;
	private TextView mTotalDuration;
	public SeekBar ProgressBar;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener;
	private Utilities lUtilityObj;
	private Handler mHandler = new Handler();
	private TextView mSongName;
	private Animation myAnimation;
	private MyApplication myApp;
	private PositionGlobal mPositionGlobal;
	private String NEXT_BTN = "1";
	private String PREV_BTN = "0";
	private String POSITION = "position";
	private SharedPreferences.Editor editor;
	private String MY_PREF = "pref";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_musicplayer);

		initView();
		play_btn.setOnClickListener(this);
		ProgressBar.setOnSeekBarChangeListener(this);

	}

	public void initView() {

		doRegisterReciever();
		myApp = (MyApplication) getApplication();
		mPositionGlobal = myApp.mPositionGlobal;
		animateFirstListener = new AnimateFirstDisplayListener();
		myApp.mImageLoader.destroy();
		mImageLoader = myApp.mImageLoader;
		lUtilityObj = new Utilities(getApplicationContext());
		myAnimation = AnimationUtils.loadAnimation(this, R.anim.text_animation);
		song_list = (ImageButton) findViewById(R.id.btnList);
		editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
		findViewById(R.id.btn);
		play_btn = (ImageButton) findViewById(R.id.btnPlay);
		findViewById(R.id.btnNext).setOnClickListener(this);
		findViewById(R.id.btnPrevious).setOnClickListener(this);
		song_list.setOnClickListener(this);
		// previous = (ImageButton)findViewById(R.id.btnPrevious);
		// next = (ImageButton)findViewById(R.id.btnNext);

		AlbumImage = (ImageView) findViewById(R.id.ALBUM_IMAGE);
		ProgressBar = (SeekBar) findViewById(R.id.ProgressBar);
		mCurrentDuration = (TextView) findViewById(R.id.CURRENT_TIME);
		mTotalDuration = (TextView) findViewById(R.id.TOTAL_DURATION);
		mSongName = (TextView) findViewById(R.id.SONG_NAME);
		mSongName.startAnimation(myAnimation);
		mSongName.setSelected(true);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.ic_stub).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(1000))
				.displayer(new RoundedBitmapDisplayer(20)).build();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));

		Intent lIntent = getIntent();

		SONG_LIST = new ArrayList<MUsicModels>();
		if (lIntent != null) {

			SONG_LIST = lUtilityObj.getSongList();

			if (lIntent.hasExtra("Song_Position")) {

				editor.putInt(POSITION, lIntent.getIntExtra("Song_Position", 0));
			    editor.commit();
			    
				if (MusicService.lMediaPlayer != null) {

					MusicService.lMediaPlayer.stop();
					MusicService.lMediaPlayer.release();

				}

				if (isMyServiceRunning(MusicService.class)) {

					Intent mIntent = new Intent(MusicPlayerActivity.this,
							MusicService.class);
					stopService(mIntent);

				}

				mPositionGlobal.position = lIntent.getIntExtra("Song_Position",
						0);
				play_btn.setImageResource(R.drawable.img_btn_pause);
				playSongThruService();

			} else {
				if (mPositionGlobal.position == -1) {

					SharedPreferences prefs = getSharedPreferences(MY_PREF,
							MODE_PRIVATE);
					int position = prefs.getInt(POSITION, 0);
					
						mPositionGlobal.position = position;
					play_btn.setImageResource(R.drawable.img_btn_pause);
					playSongThruService();

				} else {
					if (MusicService.lMediaPlayer.isPlaying()) {
						play_btn.setImageResource(R.drawable.img_btn_pause);
					} else {
						play_btn.setImageResource(R.drawable.img_btn_play);
					}

					loadAlbumImage(SONG_LIST.get(mPositionGlobal.position).AlbumID);
					mSongName
							.setText(SONG_LIST.get(mPositionGlobal.position).SongName);
					updateProgressBar();
				}
			}

		}

	}

	public void playSongThruService() {

		loadAlbumImage(SONG_LIST.get(mPositionGlobal.position).AlbumID);
		mSongName.setText(SONG_LIST.get(mPositionGlobal.position).SongName);
		Intent mIntent = new Intent(MusicPlayerActivity.this,
				MusicService.class);
		mIntent.putExtra("Song_Postion", mPositionGlobal.position);
		startService(mIntent);
		// play_btn.setImageResource(R.drawable.stop);
		
		
		ProgressBar.setProgress(0);
		ProgressBar.setMax(100);
    	updateProgressBar();
		
		

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnPlay:

			if (MusicService.lMediaPlayer.isPlaying()) {
				if (MusicService.lMediaPlayer != null) {
					MusicService.lMediaPlayer.pause();

					play_btn.setImageResource(R.drawable.img_btn_play);
				}
			} else {
				// Resume song
				if (MusicService.lMediaPlayer != null) {
					MusicService.lMediaPlayer.start();

					play_btn.setImageResource(R.drawable.img_btn_pause);
				}
			}
			break;
		case R.id.btnNext:

			if (MusicService.lMediaPlayer.isPlaying()) {

				mHandler.removeCallbacks(mUpdateTimeTask);
			}

			sendPosition(NEXT_BTN);
			ProgressBar.setProgress(0);
			ProgressBar.setMax(100);
			updateProgressBar();

			break;
		case R.id.btnPrevious:

			if (MusicService.lMediaPlayer.isPlaying()) {

				mHandler.removeCallbacks(mUpdateTimeTask);
			}

			sendPosition(PREV_BTN);
			ProgressBar.setProgress(0);
			ProgressBar.setMax(100);
			updateProgressBar();

			break;

		case R.id.btnList:
			mHandler.removeCallbacks(mUpdateTimeTask);
			Intent lIntent = new Intent(getApplicationContext(), MainActivity_Fragment.class);
			lIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity(lIntent);
			MusicPlayerActivity.this.finish();
			overridePendingTransition(R.anim.slidingout, R.anim.slidingin);

		}

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = MusicService.lMediaPlayer.getDuration();
		int currentPosition = lUtilityObj.progressToTimer(
				seekBar.getProgress(), totalDuration);

		// forward or backward to certain seconds
		MusicService.lMediaPlayer.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();

	}

	private void loadAlbumImage(String pAlbumId) {

		String BP = lUtilityObj.getMusicBitmap(pAlbumId);
		mImageLoader
				.displayImage(BP, AlbumImage, options, animateFirstListener);

	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = MusicService.lMediaPlayer.getDuration();
			long currentDuration = MusicService.lMediaPlayer
					.getCurrentPosition();
			mCurrentDuration.setText(""
					+ lUtilityObj.FormatTime(currentDuration));
			mTotalDuration.setText("" + lUtilityObj.FormatTime(totalDuration));
			int progress = (int) (lUtilityObj.getProgressPercentage(
					currentDuration, totalDuration));
			ProgressBar.setProgress(progress);
			mHandler.postDelayed(this, 100);
		}
	};

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				System.out.println("service Running :::" + "true");
				return true;
			}
		}
		System.out.println("service Running :::" + "false");
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		mHandler.removeCallbacks(mUpdateTimeTask);
		overridePendingTransition(R.anim.slidingout, R.anim.slidingin);
	}

	class MyMusicReciever extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			int lpos = intent.getIntExtra("pos", 0);

			if (lpos == -10) {

				mHandler.removeCallbacks(mUpdateTimeTask);

				System.exit(0);
			} else {
				mSongName.setText(SONG_LIST.get(lpos).SongName);
				loadAlbumImage(SONG_LIST.get(lpos).AlbumID);
			}

		}

	}

	public static final String RECIEVER_FILTER = "com.reciever.intent";
	private MyMusicReciever musicReciever;

	public void doRegisterReciever() {
		musicReciever = new MyMusicReciever();
		System.out.println(":::Registering MyMusicReciever::::");
		registerReceiver(musicReciever, new IntentFilter(RECIEVER_FILTER));
	}

	public void doUnRegisterReciever() {
		System.out.println(":::UnRegistering MyMusicReciever::::");
		unregisterReceiver(musicReciever);
	}

	private void sendPosition(String msg) {

		Intent lIntent = new Intent();
		lIntent.setAction(MusicService.RECIEVER_FILTER_SER);
		lIntent.putExtra("BUTTON_ACTION", msg);
		sendBroadcast(lIntent);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnRegisterReciever();
	}

}
