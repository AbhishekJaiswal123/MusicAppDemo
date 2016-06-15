package com.example.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.models.MUsicModels;
import com.example.musicproject.R;
import com.example.musicproject.application.MyApplication;
import com.example.utilities.AnimateFirstDisplayListener;
import com.example.utilities.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;



public class MusicAdapter extends ArrayAdapter<MUsicModels> {

	private Context mContext;
	private ArrayList<MUsicModels> mMusicList;
	private LayoutInflater mLayoutInflater;
	private ViewHolder lViewHolder;
	private Bitmap artwork;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ImageLoadingListener animateFirstListener ;
	private Utilities lUtilityObj;
	protected MyApplication myApp;
	private  Activity activity;
	
	
	
	
	public MusicAdapter(Activity act ,Context context, int resource, ArrayList<MUsicModels> pMusicList) {
		super(context, resource, pMusicList);
		
		activity = act;
		mContext = context;
		mMusicList = pMusicList;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myApp = (MyApplication)activity.getApplication();
		mImageLoader = myApp.mImageLoader;
		lUtilityObj = new Utilities();
		animateFirstListener = new AnimateFirstDisplayListener();
		
		
		
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
		mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		
	}
	
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.list_row, null);
			lViewHolder = new ViewHolder();
			
			lViewHolder.lSongName = (TextView)convertView.findViewById(R.id.SONGID);
			lViewHolder.lSongArtist = (TextView)convertView.findViewById(R.id.SONGARTISTID);
			lViewHolder.lSongDuration = (TextView)convertView.findViewById(R.id.SONGLENGTHID);
			lViewHolder.lAlbumImage = (ImageView)convertView.findViewById(R.id.MusicIconID);
			convertView.setTag(lViewHolder);
			
		}
		
		else{
			lViewHolder = (ViewHolder) convertView.getTag();
		}
		
		String lSongLength = lUtilityObj.FormatTime(mMusicList.get(position).SongLength);
		String BP = lUtilityObj.getMusicBitmap(mMusicList.get(position).AlbumID);
		
		mImageLoader.displayImage(BP, lViewHolder.lAlbumImage, options, animateFirstListener);
		lViewHolder.lSongName.setText(mMusicList.get(position).SongName);
		lViewHolder.lSongArtist.setText(mMusicList.get(position).Artist);
		lViewHolder.lSongDuration.setText(lSongLength);
		
		
	return convertView;
	}

public class ViewHolder{
	
	public ImageView lAlbumImage;
	public TextView lSongName;
	public TextView lSongDuration;
	public TextView lSongArtist;
	
	
}
  

}
