package com.example.musicproject.service;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.example.models.MUsicModels;
import com.example.musicproject.MusicPlayerActivity;
import com.example.musicproject.R;
import com.example.musicproject.application.MyApplication;
import com.example.utilities.PositionGlobal;
import com.example.utilities.Utilities;


public class MusicService extends Service implements OnCompletionListener, 
OnPreparedListener, OnAudioFocusChangeListener
{

	public static MediaPlayer lMediaPlayer;
	private Utilities lUtils;
	private ArrayList<MUsicModels> mSongList;
	public int mSongPosition ;
	private PositionGlobal mPositionGlobal;
	protected MyApplication myApp;
	private static String NEXT_BTN = "1";
	private  static String PREV_BTN = "0";
	public NotificationManager nm;
	public Notification mNotification;
	public static final String RECIEVER_FILTER_SER = "com.example.reciever.intent";
	public static final String ACTION_LISTNER = "com.example.service.notification.intent";
	private AudioManager am ;
	private String POSITION = "position";
	private SharedPreferences.Editor editor;
	private String MY_PREF = "pref";
	
	 
	 
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("::::::Service onCreate():::::");
		lMediaPlayer = new MediaPlayer();
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		doRegisterReciever();
		System.out.println(":::::::::Service onStartCommand() :::::");
		myApp = (MyApplication)getApplication();
		mPositionGlobal = myApp.mPositionGlobal;
		am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		lUtils = new Utilities(getApplicationContext());
		mSongList = new ArrayList<MUsicModels>();
		editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
		//mSongList = intent.getParcelableArrayListExtra("Song_List");
		mSongList = lUtils.getSongList();
		mPositionGlobal.position = intent.getIntExtra("Song_Postion", 0);
		registerActionListner();
		if(checkAudioFocus()){
			playSong(mPositionGlobal.position);
		}
		
		 mNotification = new Notification(R.drawable.ic_action_pause, "Musica", System.currentTimeMillis());
     //    nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		sendNotification();
		
		
		return START_NOT_STICKY;

	}
	public void playSong(int pSongPosition){
		

		try {
			
			if(lMediaPlayer.isPlaying()){
				lMediaPlayer.stop();
				lMediaPlayer.release();
				
			}	
		
			lMediaPlayer.setDataSource(mSongList.get(pSongPosition).Uri);
			lMediaPlayer.prepareAsync();
			lMediaPlayer.setOnPreparedListener(this);
			lMediaPlayer.setOnCompletionListener(this);
		    
		   
			
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
}
	
	


	@Override
	public void onCompletion(MediaPlayer mp) {
		System.out.println("::::Inside Service onCompletion:::::");  
		mPositionGlobal.position = mPositionGlobal.position + 1;
		playNextSong();
		
	}
	
	public void playNextSong(){
		
		
		System.out.println("::::Inside PLayNextSong::::::");
		
		 if(mPositionGlobal.position < mSongList.size()){
			editor.putInt(POSITION, mPositionGlobal.position);
			editor.commit();
			lMediaPlayer.reset();
			sendPosition(mPositionGlobal.position);
			
			if(checkAudioFocus()){
				playSong(mPositionGlobal.position);
			}
			
			 
		}
		if(mPositionGlobal.position==mSongList.size()){
			
			mPositionGlobal.position = 0 ;
			editor.putInt(POSITION, mPositionGlobal.position);
			editor.commit();
			 lMediaPlayer.reset();
			 sendPosition(mPositionGlobal.position);
			 if(checkAudioFocus()){
					playSong(mPositionGlobal.position);
				}
			
			
			
		}
			
	}


	@Override
	public void onPrepared(MediaPlayer mp) {
		
		lMediaPlayer.start();		
	}

	public void playPreviousSong(){
		
		
		
			if(mPositionGlobal.position >= 0){
				
				editor.putInt(POSITION, mPositionGlobal.position);
				editor.commit();
				lMediaPlayer.reset();
				sendPosition(mPositionGlobal.position);
				if(checkAudioFocus()){
					playSong(mPositionGlobal.position);
				}
				
				
				 
			}
			if(mPositionGlobal.position < 0){
				 mPositionGlobal.position = mSongList.size()-1 ;
				 editor.putInt(POSITION, mPositionGlobal.position);
				 editor.commit();
				 lMediaPlayer.reset();
				 sendPosition(mPositionGlobal.position);
				 if(checkAudioFocus()){
						playSong(mPositionGlobal.position);
					}
				
			}
   
	
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lMediaPlayer.release();
		doUnRegisterReciever();
		unRegisterActionListner();
		stopForeground(true);
		System.out.println("::::Service OnDestroy()::::");
	}
	
	private void sendPosition(int pPosition){
		 
		    Intent lIntent = new Intent();
			lIntent.setAction(MusicPlayerActivity.RECIEVER_FILTER);
			lIntent.putExtra("pos", pPosition);
			sendBroadcast(lIntent);
		
	}
	
//	public static Handler myHandler = new Handler(){
//		
//		public void handleMessage(Message msg) {
//			
//			Bundle bundleData = msg.getData();
//			
//			if(bundleData.getString("BUTTON_ACTION").equals("1")){
//				
//				//playNextSong();
//			}
//			
//			
//		};
//	};
	
//	public static class MyHandler extends Handler {
//	    private final WeakReference<MusicService> mService;
//
//	    public MyHandler(MusicService service) {
//	    	mService = new WeakReference<MusicService>(service);
//	    }
//
//	    @Override
//	    public  void handleMessage(Message msg) {
//	    	MusicService service = mService.get();
//	      if (service != null) {
//	        
//	    	  Bundle bundleData = msg.getData();
//				if(bundleData.getString("BUTTON_ACTION").equals("1")){
//					service.playNextSong();
//				}
//	    	  
//	      }
//	    }
//	  }

	  

	 class MyMusicServiceReciever extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			 String btn_action = intent.getStringExtra("BUTTON_ACTION");
			 
			 if(btn_action.equals(NEXT_BTN)){
				 mPositionGlobal.position = mPositionGlobal.position + 1;
				 playNextSong();
				 sendNotification();
				 
			 }else if(btn_action.equals(PREV_BTN)){
				 mPositionGlobal.position = mPositionGlobal.position - 1;
				 playPreviousSong();
				 sendNotification();
				 
			 }
		}
		 
	 }
	 
	 
	
	 private MyMusicServiceReciever musicService;
	 public void doRegisterReciever(){
		 musicService = new MyMusicServiceReciever();
		 System.out.println(":::Registering MyMusicServiceReciever::::");
		 registerReceiver(musicService, new IntentFilter(RECIEVER_FILTER_SER));
	 }
	 
	 public void doUnRegisterReciever(){
		 System.out.println(":::UnRegistering MyMusicServiceReciever::::");
		 unregisterReceiver(musicService);
	 }
	 
	
	 public  void sendNotification(){
		   
		   
            RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.notification_for_music);
        
   			 if(lMediaPlayer.isPlaying()){
   				contentView.setImageViewResource(R.id.nbtnPlay, R.drawable.ic_action_play);
   			 }else{
   				contentView.setImageViewResource(R.id.nbtnPlay, R.drawable.ic_action_pause);
   			 }
   		 
            contentView.setImageViewResource(R.id.nIconForSong, R.drawable.ic_launcher);
			contentView.setTextViewText(R.id.nSongName, mSongList.get(mPositionGlobal.position).SongName);
			contentView.setTextViewText(R.id.nSongArtist, mSongList.get(mPositionGlobal.position).Artist);
		
			mNotification.contentView = contentView;		
			Intent notificationIntent = new Intent(this, MusicPlayerActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_NEW_TASK );

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			mNotification.contentIntent = contentIntent;
			
			
			
			
			 Intent switchIntentPlay = new Intent("com.example.app.ACTION_PLAY");
			// switchIntentPlay.setAction(ACTION_LISTNER);
			// switchIntentPlay.putExtra("action", 1);
			
			 PendingIntent pendingSwitchIntentPlay = PendingIntent.getBroadcast(this, 0,switchIntentPlay,0);
			 contentView.setOnClickPendingIntent(R.id.nbtnPlay, 
					 pendingSwitchIntentPlay);
			 
			
			 Intent switchIntentPrevious = new Intent("com.example.app.ACTION_CANCEL");
			// switchIntentPrevious.setAction(ACTION_LISTNER);
			// switchIntentPrevious.putExtra("action", 0);
			
			 PendingIntent pendingSwitchIntentPrevious = PendingIntent.getBroadcast(this, 0,switchIntentPrevious, 0);
			 contentView.setOnClickPendingIntent(R.id.nbtnCancel, 
					 pendingSwitchIntentPrevious);
			 
			 
			 Intent switchIntentNext = new Intent("com.example.app.ACTION_NEXT");
			// switchIntentNext.setAction(ACTION_LISTNER);
			// switchIntentNext.putExtra("action", 2);
			PendingIntent pendingSwitchIntentNext = PendingIntent.getBroadcast(this, 0,switchIntentNext, 0);
			
			 contentView.setOnClickPendingIntent(R.id.nbtnNext, 
					 pendingSwitchIntentNext);
			
			 startForeground(1, mNotification);
		//    nm.notify(1, mNotification);
		    
		    
	
		
	}
	 
	 
	public class switchButtonListener extends BroadcastReceiver {
		 
		 @Override
		    public void onReceive(Context context, Intent intent) {
		      
			
                 String action = intent.getAction();
			
			  if(action.equals("com.example.app.ACTION_PLAY")){
				 
				 // RemoteViews notificationView = new RemoteViews(context.getPackageName(), R.layout.notification_for_music);
				  
	    		 if(lMediaPlayer.isPlaying()){
	    			 sendNotification();
	    			 lMediaPlayer.pause();
	    			 
	    		//	 Toast.makeText(context, ":::::pause ::::", Toast.LENGTH_SHORT).show();
	    			 
	    		 }
	    		 else
	    		 {
	    			 sendNotification();
	    			 lMediaPlayer.start();
	    			 
	    		//	 Toast.makeText(context, ":::::play ::::", Toast.LENGTH_SHORT).show();
	    		 }
			 
			 }
			  
			  if(action.equals("com.example.app.ACTION_CANCEL")){
				 
//				 Toast.makeText(context, ":::::Cancel ::::", Toast.LENGTH_SHORT).show();
//				  mPositionGlobal.position = mPositionGlobal.position - 1;
//				  playPreviousSong();
//				  sendNotification(intent);
				  //  sendPosition(-10);
				    lMediaPlayer.release();
					stopForeground(true);
					stopSelf();
					System.runFinalizersOnExit(true);
					System.exit(0);
    			 
    		 }
			  
			  
			  if(action.equals("com.example.app.ACTION_NEXT")){
				  mPositionGlobal.position = mPositionGlobal.position + 1;
				  playNextSong();
				  sendNotification();
    			 
    		//	 Toast.makeText(context, ":::::next ::::", Toast.LENGTH_SHORT).show();
    		 }		    
			  
		 }
		
	   }
	   
	   
//	private void buttonActionThruBroadcast(String msg){
//		 
//	    Intent lIntent = new Intent();
//		lIntent.setAction(MusicService.ACTION_LISTNER);
//		lIntent.putExtra("BUTTON_ACTION", msg);
//		sendBroadcast(lIntent);
//	
//}
	
	
	
	
	
	   private switchButtonListener Actionlistner;
		 public void registerActionListner(){
			 Actionlistner = new switchButtonListener();
			 IntentFilter intentFilter = new IntentFilter();
			    intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
			    // set the custom action
			    intentFilter.addAction("com.example.app.ACTION_PLAY");
			    intentFilter.addAction("com.example.app.ACTION_CANCEL");
			    intentFilter.addAction("com.example.app.ACTION_NEXT");
			    
			 System.out.println(":::Registering switchButtonListener::::");
			 registerReceiver(Actionlistner, intentFilter);
		 }
		 
		 public void unRegisterActionListner(){
			 System.out.println(":::UnRegistering switchButtonListener::::");
			 unregisterReceiver(Actionlistner);
		 }
	
		 
		 public boolean checkAudioFocus(){
				
				
				int result = am.requestAudioFocus(this,
				        // Use the music stream.
				        AudioManager.STREAM_MUSIC,
				        // Request permanent focus.
				        AudioManager.AUDIOFOCUS_GAIN);

				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				
				return true;
				}
				else 
					return false;
				
			}
		 
		 
		 @Override
		 public void onAudioFocusChange(int focusChange) {
		 	        if (focusChange ==  AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
		 	        	  sendNotification();
		 	        	lMediaPlayer.pause();
		 	        } 
		 	        else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
		 	        	     sendNotification();
		 	        		lMediaPlayer.start();
		 	        	
		 	        }
//		 	        else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//		 	                am.abandonAudioFocus(this);
//		 	            
//		 	            lMediaPlayer.stop();
//		 	            lMediaPlayer.release();
//		 	            stopForeground(true);
//						stopSelf();
//						System.runFinalizersOnExit(true);
//						System.exit(0);
//		 	        
//		 	        }

		 }
		 
}
