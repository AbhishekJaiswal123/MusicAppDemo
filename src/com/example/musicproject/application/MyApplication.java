package com.example.musicproject.application;

import com.example.utilities.PositionGlobal;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Application;

public class MyApplication extends Application {

	public ImageLoader mImageLoader;
	public PositionGlobal mPositionGlobal;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mImageLoader = ImageLoader.getInstance();
		mPositionGlobal = PositionGlobal.getInstance();
	
	System.out.println("::::Application Initialised::::");
	}
	
	
}
