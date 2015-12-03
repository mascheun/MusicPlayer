package com.example.imusicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);

		db = DatabaseClass.getInstance();

		sm = new SongsManager();
		showSongs = (ListView) findViewById(R.id.songs_drawer);

		setONClickSong();
		
		Bundle b = getIntent().getExtras();
		int mode = b.getInt(Constants.MODE);
		switch (mode) {
		case 1:
			showSongList();
			break;
		case 2:
			String pl = b.getString(Constants.PLAYLISTKEY);
			showSongsFromPlayList(pl);
			break;
		default:
			break;
		}

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

	public ArrayList<String> fillAllSongsInList() {
		ArrayList<HashMap<String, String>> allSongList = sm.getPlayList();
		ArrayList<String> songList = new ArrayList<String>();

		for (HashMap<String, String> hm : allSongList) {
			songList.add(hm.get("songTitle"));
		}
		return songList;
	}

	// Show all possible Songs
	public void showSongList() {
		ArrayList<String> songList = new ArrayList<String>();
		songList.addAll(fillAllSongsInList());
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		showSongs.setAdapter(adapter);
	}

	// Shows songs from PlayList
	public void showSongsFromPlayList(String playlist) {
		ArrayList<String> songList = new ArrayList<String>();
		if(playlist == null) {
			return;
		}
		System.out.println("Test111");
		songList.addAll(db.showSongInPlaylist(playlist));
		System.out.println("Test222");
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		showSongs.setAdapter(adapter);
	}

	// Set on Click Listener from Songs
	public void setONClickSong() {
		showSongs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String song = (String) parent.getAdapter().getItem(position);
				sm.playSong(position, sm.getPlayList(), song);
				Intent songScreen = new Intent(getApplicationContext(), SongActivity.class);
				startActivity(songScreen);
			}
		});
	}

}
