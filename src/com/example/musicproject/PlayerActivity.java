package com.example.musicproject;

import java.io.IOException;
import java.util.ArrayList;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
import com.example.utilities.AnimateFirstDisplayListener;
import com.example.utilities.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class PlayerActivity extends ActionBarActivity implements OnClickListener, OnSeekBarChangeListener, OnCompletionListener{

	private ArrayList<MUsicModels> SONG_LIST;
	private int mSongPosition;
	private static MediaPlayer lMediaPlay = null;
	private ImageButton play_btn;
	private ImageButton previous;
	private ImageButton next;
	private ImageButton backward;
	private ImageButton forward;
	private int mForwardSeekTime = 5000;
	private int mBackwardSeekTime = 5000;
	private ImageView AlbumImage;
	private static TextView mCurrentDuration;
	private TextView mTotalDuration;
	private static SeekBar ProgressBar;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener ;
	private Utilities lUtilityObj;
	private Handler mHandler = new Handler();;
	private TextView mSongName;
	private Animation myAnimation;
	protected MyApplication myApp;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_musicplayer);
		
		if(lMediaPlay != null){
		
			lMediaPlay.stop();
			lMediaPlay.release();
		}	
		lMediaPlay = new MediaPlayer();
	  	    
	    
		myApp = (MyApplication)getApplication();
		animateFirstListener = new AnimateFirstDisplayListener();
	    myApp.mImageLoader.destroy();
	    mImageLoader = myApp.mImageLoader;
		lUtilityObj = new Utilities(PlayerActivity.this);
		myAnimation = AnimationUtils.loadAnimation(this,R.anim.text_animation);
		
		play_btn = (ImageButton)findViewById(R.id.btnPlay);
		previous = (ImageButton)findViewById(R.id.btnPrevious);
		next = (ImageButton)findViewById(R.id.btnNext);
		//backward = (ImageButton)findViewById(R.id.btnBackward);
		//forward = (ImageButton)findViewById(R.id.btnForward);
		
		AlbumImage = (ImageView)findViewById(R.id.ALBUM_IMAGE);
		ProgressBar = (SeekBar)findViewById(R.id.ProgressBar);
		mCurrentDuration = (TextView)findViewById(R.id.CURRENT_TIME);
		mTotalDuration = (TextView)findViewById(R.id.TOTAL_DURATION);
		mSongName = (TextView)findViewById(R.id.SONG_NAME);
		mSongName.startAnimation(myAnimation);
		mSongName.setSelected(true);
		
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_stub)
		.showImageOnFail(R.drawable.ic_stub)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(1000))
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
		
		
		Intent lIntent = getIntent();
		
		SONG_LIST = new ArrayList<MUsicModels>();
		if(lIntent !=null){
			//SONG_LIST = lIntent.getParcelableArrayListExtra("Song_List");
			SONG_LIST = lUtilityObj.getSongList();
			
			mSongPosition = lIntent.getIntExtra("Song_Postion", 0);
		
		}
	
		loadAlbumImage(SONG_LIST.get(mSongPosition).AlbumID);
		mSongName.setText(SONG_LIST.get(mSongPosition).SongName);
		
		
		playSong(mSongPosition);
		play_btn.setOnClickListener(this); 
		ProgressBar.setOnSeekBarChangeListener(this);
	 
		next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(lMediaPlay.isPlaying()){
					
					mHandler.removeCallbacks(mUpdateTimeTask);
				}	
			   playNextSong();
			}
		});
	
	
		forward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int lCurrentPostion = lMediaPlay.getCurrentPosition();
				
				if(lCurrentPostion + mForwardSeekTime <= lMediaPlay.getDuration()){
					// forward song
					lMediaPlay.seekTo(lCurrentPostion + mForwardSeekTime);
				}else{
					// forward to end position
					lMediaPlay.seekTo(lMediaPlay.getDuration());
				}
				
			}
		});
		
		
		previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
               if(lMediaPlay.isPlaying()){
					
					mHandler.removeCallbacks(mUpdateTimeTask);
				}	
			   playPreviousSong();
			}
		});
		
		
		backward.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
               int lCurrentPostion = lMediaPlay.getCurrentPosition();
				
               if(lCurrentPostion > mBackwardSeekTime){
            	   
            	   if(lCurrentPostion - mBackwardSeekTime >= 0){
   					// forward song
   					lMediaPlay.seekTo(lCurrentPostion - mBackwardSeekTime);
   				   }else{
   					// forward to end position
   					lMediaPlay.seekTo(lMediaPlay.getDuration());
   				}
               }else {
            	   if(lMediaPlay.isPlaying()){
   					
   					    mHandler.removeCallbacks(mUpdateTimeTask);
   				   }	
   			           playPreviousSong(); 
            	   
               }
				
				
			}
		});
	}


	@Override
	public void onClick(View v) {
			
		switch (v.getId()) {
		case R.id.btnPlay:
			
			
			if(lMediaPlay.isPlaying()){
				if(lMediaPlay!=null){
					lMediaPlay.pause();
					// Changing button image to play button
					play_btn.setImageResource(R.drawable.img_btn_play);
				}
			}else{
				// Resume song
				if(lMediaPlay!=null){
					lMediaPlay.start();
					// Changing button image to pause button
					play_btn.setImageResource(R.drawable.img_btn_pause);
				}
			}
		
		}
	}



private void playSong(int pSongPosition){
	

		try {
			
			if(lMediaPlay.isPlaying()){
				lMediaPlay.stop();
				lMediaPlay.release();
				
			}	
			play_btn.setImageResource(R.drawable.img_btn_pause);
			lMediaPlay.setDataSource(SONG_LIST.get(pSongPosition).Uri);
			lMediaPlay.prepare();
			lMediaPlay.start();
		    ProgressBar.setProgress(0);
		    ProgressBar.setMax(100);
		    updateProgressBar();			
		    lMediaPlay.setOnCompletionListener(this);
		    
		   
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
}


@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	// TODO Auto-generated method stub
	
}


@Override
public void onStartTrackingTouch(SeekBar seekBar) {
	mHandler.removeCallbacks(mUpdateTimeTask);
	
}


@Override
public void onStopTrackingTouch(SeekBar seekBar) {
	mHandler.removeCallbacks(mUpdateTimeTask);
	int totalDuration = lMediaPlay.getDuration();
	int currentPosition = lUtilityObj.progressToTimer(seekBar.getProgress(), totalDuration);
	
	// forward or backward to certain seconds
	lMediaPlay.seekTo(currentPosition);
	
	// update timer progress again
	updateProgressBar();
	
}


@Override
public void onCompletion(MediaPlayer mp){
	
	  play_btn.setImageResource(R.drawable.img_btn_play);
	  playNextSong();
	
	
}

private void loadAlbumImage(String pAlbumId){
	
	String BP = lUtilityObj.getMusicBitmap(pAlbumId);
	mImageLoader.displayImage(BP, AlbumImage, options, animateFirstListener);
	
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
		   long totalDuration = lMediaPlay.getDuration();
		   long currentDuration = lMediaPlay.getCurrentPosition();
		  
		   // Displaying Total Duration time
		   mCurrentDuration.setText(""+lUtilityObj.FormatTime(currentDuration));
		   // Displaying time completed playing
		   mTotalDuration.setText(""+lUtilityObj.FormatTime(totalDuration));
		   
		   // Updating progress bar
		   int progress = (int)(lUtilityObj.getProgressPercentage(currentDuration, totalDuration));
		   //Log.d("Progress", ""+progress);
		   ProgressBar.setProgress(progress);
		   
		   // Running this thread after 100 milliseconds
	       mHandler.postDelayed(this, 100);
	   }
	};
	
private void playNextSong(){
	
	
	
	 mSongPosition = mSongPosition + 1;
	if(mSongPosition < SONG_LIST.size()){
		 mSongName.setText(SONG_LIST.get(mSongPosition).SongName);
		 loadAlbumImage(SONG_LIST.get(mSongPosition).AlbumID);
		 lMediaPlay.reset();
		 playSong(mSongPosition);
		 
	}
	if(mSongPosition==SONG_LIST.size()){
		
		 mSongPosition = 0 ;
		 mSongName.setText(SONG_LIST.get(mSongPosition).SongName);
		 loadAlbumImage(SONG_LIST.get(mSongPosition).AlbumID);
		 lMediaPlay.reset();
		 playSong(mSongPosition);
		
	}
		
}


private void playPreviousSong(){
	
	 mSongPosition = mSongPosition - 1;
		if(mSongPosition >= 0){
			 mSongName.setText(SONG_LIST.get(mSongPosition).SongName);
			 loadAlbumImage(SONG_LIST.get(mSongPosition).AlbumID);
			 lMediaPlay.reset();
			 playSong(mSongPosition);
			 
		}
		if(mSongPosition < 0){
			
			 mSongPosition = SONG_LIST.size()-1 ;
			 mSongName.setText(SONG_LIST.get(mSongPosition).SongName);
			 loadAlbumImage(SONG_LIST.get(mSongPosition).AlbumID);
			 lMediaPlay.reset();
			 playSong(mSongPosition);
			
		}
	
}

@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	 overridePendingTransition(R.anim.slidingout,R.anim.slidingin);
}
}


	
	

