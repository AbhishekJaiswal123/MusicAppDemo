package com.example.musicproject.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.musicproject.R;
import com.example.utilities.Constant;
import com.example.utilities.SetOnFragmentChangeListener;
import com.gc.materialdesign.views.LayoutRipple;
import com.nineoldandroids.view.ViewHelper;

public class FeaturesFragment extends Fragment implements OnClickListener {

	private View mRooView;
	private LayoutRipple mPlaylist;
	private LayoutRipple mFavourite;
	private LayoutRipple mAlbum;
	private LayoutRipple mRecent;
	private SetOnFragmentChangeListener mCallBack;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRooView = inflater.inflate(R.layout.fragment_list, container, false);
		initView();
		return mRooView;
	}

	private void initView() {
		mPlaylist = (LayoutRipple) mRooView.findViewById(R.id.playlist);
		mFavourite = (LayoutRipple) mRooView.findViewById(R.id.favourite);
		mAlbum = (LayoutRipple) mRooView.findViewById(R.id.album);
		mRecent = (LayoutRipple) mRooView.findViewById(R.id.recent);
		setOriginRiple(mAlbum);
		setOriginRiple(mPlaylist);
		setOriginRiple(mFavourite);
		setOriginRiple(mRecent);
		mPlaylist.setOnClickListener(this);

	}

	private void setOriginRiple(final LayoutRipple layoutRipple) {

		layoutRipple.post(new Runnable() {

			@Override
			public void run() {
				View v = layoutRipple.getChildAt(0);
				layoutRipple.setxRippleOrigin(ViewHelper.getX(v) + v.getWidth()
						/ 2);
				layoutRipple.setyRippleOrigin(ViewHelper.getY(v)
						+ v.getHeight() / 2);

				layoutRipple.setRippleColor(Color.parseColor("#1E88E5"));

				layoutRipple.setRippleSpeed(30);
			}
		});

	}

	@Override
	public void onClick(View v) {

		if(v == mPlaylist){
			if(mCallBack != null)
			   mCallBack.onFragmentChangeListener(Constant.PLAYLIST_FRAGMENT);
			
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		 try {
			 mCallBack = (SetOnFragmentChangeListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException("Activity must implement setOnFragmentChangeListener.");
	        }
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		mCallBack = null;
	}
	
	
	
}
