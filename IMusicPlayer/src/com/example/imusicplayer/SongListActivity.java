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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;
import functions.SongsManager;

public class SongListActivity extends Activity {

	private SongsManager sm;
	private ArrayList<String> songList;
	
	private ListView showSongs;
	private Button searchBt;
	private EditText searchText;
	
	private DatabaseClass db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);

		db = DatabaseClass.getInstance();

		sm = new SongsManager();
		showSongs = (ListView) findViewById(R.id.songs_drawer);
		searchBt = (Button) findViewById(R.id.bt_search);
		searchText = (EditText) findViewById(R.id.edit_text_song_search);

		setONClick();
		
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
		getMenuInflater().inflate(R.menu.song_list, menu);
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

	// Show all possible Songs
	public void showSongList() {
		ArrayList<HashMap<String, String>> allSongList = sm.getPlayList();
		songList = new ArrayList<String>();

		for (HashMap<String, String> hm : allSongList) {
			songList.add(hm.get("songTitle"));
		}
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		showSongs.setAdapter(adapter);
	}

	// Shows songs from PlayList
	public void showSongsFromPlayList(String playlist) {
		songList = new ArrayList<String>();
		if(playlist == null) {
			return;
		}
		songList.addAll(db.showSongInPlaylist(playlist));
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		showSongs.setAdapter(adapter);
	}
	
	//Shows searched songs
	public void showSearchedSongs(ArrayList<String> searchedSongs) {
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedSongs);
		showSongs.setAdapter(adapter);
	}

	// Set on Click Listener from Songs
	public void setONClick() {
		showSongs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String song = (String) parent.getAdapter().getItem(position);
				Bundle b = new Bundle();
				b.putString(Constants.SONGKEY, song);
				Intent songScreen = new Intent(getApplicationContext(), SongActivity.class);
				songScreen.putExtras(b);
				startActivity(songScreen);
			}
		});
		searchBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> searchedSongs = new ArrayList<String>();
				for(String song : songList) {
					if(song.contains(searchText.getText().toString())) {
						searchedSongs.add(song);
					}
				}
				searchText.setText("");
				showSearchedSongs(searchedSongs);
			}
		});
	}

}
