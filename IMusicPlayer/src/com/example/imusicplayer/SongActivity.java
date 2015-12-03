package com.example.imusicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import functions.SongsManager;

public class SongActivity extends Activity {
	
	private SongsManager sm;
	
	private ImageButton btPlay;
	private ImageButton btStop;
	private ImageButton btNextSong;
	private ImageButton btPreviousSong;
	private String songToPlay = "";
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song);
		
		sm = new SongsManager();
		
		btPlay = (ImageButton) findViewById(R.id.imageButton3);
		btStop = (ImageButton) findViewById(R.id.imageButton1);
		btNextSong = (ImageButton) findViewById(R.id.imageButton2);
		btPreviousSong = (ImageButton) findViewById(R.id.imageButton4);
		
		Bundle b = getIntent().getExtras();
		songToPlay = b.getString(Constants.SONGKEY);
		
		setONClick();
		
		sm.playSong(sm.getPlayList(), songToPlay);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.song, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Set on Click Listener from Songs
	public void setONClick() {
		btPlay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sm.startSong();
			}
		});
		btStop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sm.stopSong();
			}
		});
		btNextSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});
		btPreviousSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

			}
		});
	}
	
}
