package com.example.musicproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.example.musicproject.fragments.FeaturesFragment;
import com.example.musicproject.fragments.PlayListFragment;
import com.example.musicproject.fragments.SonglistFragment;
import com.example.utilities.Constant;
import com.example.utilities.SetOnFragmentChangeListener;

public class MainActivity_Fragment extends Activity implements OnClickListener,
		SetOnFragmentChangeListener {

	private SonglistFragment mListFragment;
	private FragmentTransaction mTransaction;
	private FeaturesFragment mFeatureFragment;
	private Fragment mCurrentFragment;
	private FragmentManager mFragManger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_grid);
		initView();
	}

	private void initView() {

		findViewById(R.id.switchView).setOnClickListener(this);

		mListFragment = new SonglistFragment();
		mFeatureFragment = new FeaturesFragment();
		mCurrentFragment = mListFragment;
		mTransaction = getFragmentManager().beginTransaction();
		mTransaction.replace(R.id.container, mListFragment);
		
		mTransaction.commit();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switchView:

			mTransaction = getFragmentManager().beginTransaction();
			if (mCurrentFragment instanceof SonglistFragment) {

				mCurrentFragment = mFeatureFragment;
				mTransaction.replace(R.id.container, mFeatureFragment);
				mTransaction.setCustomAnimations(R.anim.slidingout1,
						R.anim.slidingin1);
				
				mTransaction.commit();
			} else {
				mCurrentFragment = mListFragment;
				mTransaction.replace(R.id.container, mListFragment);
				mTransaction.setCustomAnimations(R.anim.slidingout1,
						R.anim.slidingin1);
				
				mTransaction.commit();
			}

			break;

		}

	}

	@Override
	public void onFragmentChangeListener(String fragment) {
		Fragment mFragment = null;

		if (fragment.equalsIgnoreCase(Constant.PLAYLIST_FRAGMENT)) {
			mFragment = new PlayListFragment();
		}
		if (fragment.equalsIgnoreCase(Constant.SONGLIST_FRAGMENT)) {
			mCurrentFragment = mListFragment;
			mFragment = new SonglistFragment();
		}

		if (mFragment != null) {

			mFragManger = getFragmentManager();
			mFragManger.beginTransaction().replace(R.id.container, mFragment)
					.setCustomAnimations(R.anim.slidingout1, R.anim.slidingin1)
					.commit();
		}
	}

}
