package com.example.imusicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;
import functions.SongsManager;

public class SongListActivity extends Activity {
	
	private SongsManager sm;
	private ListView showSongs;
	private DatabaseClass db;
	ArrayList<String> songList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);
		
		db = new DatabaseClass(this);
		
		sm = new SongsManager();
		songList = new ArrayList<String>();
		
		fillAllSongsInList();
		
		showSongs = (ListView) findViewById(R.id.songs_drawer);
		
		showSongList();
		
		setONClickSong();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void fillAllSongsInList() {
		ArrayList<HashMap<String, String>> allSongList = sm.getPlayList();
		songList = new ArrayList<String>();

		for (HashMap<String, String> hm : allSongList) {
			songList.add(hm.get("songTitle"));
		}
	}
	
	// Show all possible Songs
	public void showSongList() {
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		showSongs.setAdapter(adapter);
	}
	
	// Shows songs from PlayList
	public void showSongsFromPlayList(String playlist) {
		songList = new ArrayList<String>();
		songList = db.showSongInPlaylist(playlist);
	}
	
	// Set on Click Listener from Songs
	public void setONClickSong() {
		showSongs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String song = (String)parent.getAdapter().getItem(position);
				sm.playSong(position, sm.getPlayList(), song);
			}
		});
	}
	
}
