package com.example.musicproject.fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicproject.R;
import com.example.utilities.Constant;
import com.example.utilities.SetOnFragmentChangeListener;

public class PlayListFragment extends Fragment implements OnClickListener,
		OnItemLongClickListener, OnItemClickListener {

	private View mRootView;
	private ListView mPlaylist;
	private Dialog mDialog;
	private EditText playlistEditText;
	private ArrayList<String> mList;
	private TextView noListTv;
	private SharedPreferences.Editor mEditor;
	
	private ArrayAdapter<String> mAdapter;
	
	private Set<String> hashSet;
	private int deletePosition;
	private SetOnFragmentChangeListener mCallBack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.playlist_fragment, container,
				false);
		initView();

		return mRootView;
	}

	private void initView() {

		mDialog = new Dialog(getActivity());
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mPlaylist = (ListView) mRootView.findViewById(R.id.PLAYLIST_ID);
		noListTv = (TextView) mRootView.findViewById(R.id.NoPlayListId);
		mRootView.findViewById(R.id.PLAYLIST_ADD).setOnClickListener(this);
		
		mEditor = getActivity().getSharedPreferences(Constant.PLAYLIST_PREF,
				Activity.MODE_PRIVATE).edit();

		if (mList.size() == 0) {

			mPlaylist.setVisibility(View.GONE);
			noListTv.setVisibility(View.VISIBLE);
		}

		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, mList);
		mPlaylist.setAdapter(mAdapter);
		mPlaylist.setOnItemClickListener(this);
		mPlaylist.setOnItemLongClickListener(this);

	}

	@Override
	public void onClick(View v) {

		mDialog.setContentView(R.layout.playlist_add_dialog);
		mDialog.setCancelable(true);

		Button doneBtn = (Button) mDialog.findViewById(R.id.SubmitBtn);
		playlistEditText = (EditText) mDialog.findViewById(R.id.PlayListName);
		doneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String playlistName = String.valueOf(playlistEditText.getText());
				if (playlistName.equals("")) {
					Toast.makeText(getActivity(), "Enter Playlist Name",
							Toast.LENGTH_SHORT).show();
				} else {

					mList.add(playlistName);
					mAdapter.notifyDataSetChanged();
					mPlaylist.setVisibility(View.VISIBLE);
					noListTv.setVisibility(View.GONE);
					Toast.makeText(getActivity(), "Playlist made",
							Toast.LENGTH_SHORT).show();
					mDialog.cancel();
				}

			}
		});
		mDialog.show();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		SharedPreferences playlistPref = getActivity().getSharedPreferences(
				Constant.PLAYLIST_PREF, Activity.MODE_PRIVATE);
		hashSet = playlistPref.getStringSet(Constant.PLAYLIST_PREF_KEY, null);
		if (hashSet == null) {
			mList = new ArrayList<String>();
		} else {

			mList = new ArrayList<String>(hashSet);
		}
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
		hashSet = new HashSet<String>(mList);
		mEditor.putStringSet(Constant.PLAYLIST_PREF_KEY, hashSet);
		mEditor.commit();
		mCallBack = null;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		deletePosition = position;
		mDialog.setContentView(R.layout.playlist_delete_dialog);
		mDialog.setCancelable(false);
		Button doneBtn = (Button) mDialog.findViewById(R.id.DoneBtn);
		Button cancelBtn = (Button) mDialog.findViewById(R.id.CancelBtn);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.cancel();

			}
		});
		doneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mList.remove(deletePosition);
				mAdapter.notifyDataSetChanged();
				mDialog.cancel();

			}
		});
		mDialog.show();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		
		mCallBack.onFragmentChangeListener(Constant.SONGLIST_FRAGMENT);
	}

}
