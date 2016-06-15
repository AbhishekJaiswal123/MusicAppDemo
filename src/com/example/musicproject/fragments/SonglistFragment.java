package com.example.musicproject.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.adapter.MusicAdapter;
import com.example.models.MUsicModels;
import com.example.musicproject.MusicPlayerActivity;
import com.example.musicproject.R;
import com.example.musicproject.application.MyApplication;
import com.example.utilities.Constant;
import com.example.utilities.PositionGlobal;
import com.example.utilities.SetOnFragmentChangeListener;
import com.example.utilities.Utilities;

public class SonglistFragment extends Fragment implements OnClickListener,
		OnItemClickListener, OnItemLongClickListener {

	private View mRootView;
	private ListView lMusicList;
	private ArrayList<MUsicModels> lSongList;
	private MusicAdapter lAdapter;
	private Utilities lUtil;
	private Dialog mDialog;
	private SetOnFragmentChangeListener mCallBack;
	private SharedPreferences.Editor PREF_EDITOR;
	private int SELECTED_SONG_PLAYLIST;
	private MyApplication myApp;
	private PositionGlobal globalFlag;
	private ArrayList<String> playlistSongs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.activity_main, container, false);
		lUtil = new Utilities(getActivity());
		lSongList = new ArrayList<MUsicModels>();
		lMusicList = (ListView) mRootView.findViewById(R.id.MusicListID);
		lSongList = lUtil.getSongList();
		lAdapter = new MusicAdapter(getActivity(), getActivity(),
				R.layout.list_row, lSongList);
		lMusicList.setAdapter(lAdapter);
		myApp = (MyApplication) getActivity().getApplication();
		globalFlag = myApp.mPositionGlobal;
		playlistSongs = new ArrayList<String>();
		
		lMusicList.setOnItemClickListener(this);
		lMusicList.setOnItemLongClickListener(this);
		mRootView.findViewById(R.id.buttonFloat).setOnClickListener(this);

		return mRootView;
	}

	@Override
	public void onClick(View v) {
		Intent navIntent = new Intent(getActivity(), MusicPlayerActivity.class);
		startActivity(navIntent);
		getActivity().overridePendingTransition(R.anim.slidingout1,
				R.anim.slidingin1);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent lIntent = new Intent(getActivity(), MusicPlayerActivity.class);
		lIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		lIntent.putExtra("Song_Position", position);
		startActivity(lIntent);

		getActivity().overridePendingTransition(R.anim.slidingout1,
				R.anim.slidingin1);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Declaration of preference for storing selected song position

		PREF_EDITOR = getActivity().getSharedPreferences(
				Constant.SONG_TO_PLAYLIST_PREF, Activity.MODE_PRIVATE).edit();

		try {
			mCallBack = (SetOnFragmentChangeListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement setOnFragmentChangeListener.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		
		
		mCallBack = null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {

		SELECTED_SONG_PLAYLIST = position;
		
		mDialog = new Dialog(getActivity());
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mDialog.setContentView(R.layout.song_dialog);
		TextView addTxtVw = (TextView) mDialog
				.findViewById(R.id.ADDTOPLAYLIST_ID);
		addTxtVw.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Storing song position in preference

				PREF_EDITOR.putInt(Constant.SONG_TO_PLAYLIST_PREF_KEY, SELECTED_SONG_PLAYLIST);
				globalFlag.FLAG = 0;
				PREF_EDITOR.commit();
				mCallBack.onFragmentChangeListener(Constant.PLAYLIST_FRAGMENT);
				mDialog.cancel();
			}
		});
		mDialog.show();
		return true;
	}

}
