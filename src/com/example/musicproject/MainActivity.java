package com.example.musicproject;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.example.adapter.MusicAdapter;
import com.example.models.MUsicModels;
import com.example.utilities.Utilities;


  public class MainActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener {

	
	private ListView lMusicList;
	private ArrayList<MUsicModels> lSongList;
	private MusicAdapter lAdapter;
	private Utilities lUtil;
	private String MY_PREF = "pref";
	private String POSITION = "position";
	private SharedPreferences.Editor editor;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        
        lUtil = new Utilities(getApplicationContext());
        lSongList = new ArrayList<MUsicModels>();
        lMusicList = (ListView)findViewById(R.id.MusicListID);
        lSongList = lUtil.getSongList();
        lAdapter = new MusicAdapter(MainActivity.this,this, R.layout.list_row, lSongList);
        lMusicList.setAdapter(lAdapter);
        lMusicList.setOnItemClickListener(this);
        findViewById(R.id.buttonFloat).setOnClickListener(this);
        editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();
        
    
    }



@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	
	Intent lIntent = new Intent(MainActivity.this, MusicPlayerActivity.class);
	lIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	lIntent.putExtra("Song_Position", position);
	editor.putInt(POSITION, position);
    editor.commit();
	startActivity(lIntent);
	overridePendingTransition(R.anim.slidingout1,R.anim.slidingin1);
}



@Override
public void onClick(View v) {
	Intent navIntent = new Intent(MainActivity.this, MusicPlayerActivity.class);
	startActivity(navIntent);
	overridePendingTransition(R.anim.slidingout1,R.anim.slidingin1);
}



}
