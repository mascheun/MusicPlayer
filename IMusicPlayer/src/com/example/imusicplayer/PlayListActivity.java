package com.example.imusicplayer;

import java.util.ArrayList;

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

public class PlayListActivity extends Activity {
	
	private ListView playLists;
	private DatabaseClass db;
	SongListActivity sla;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list);
		
		db = new DatabaseClass(this);
		sla = new SongListActivity();
		
		playLists = (ListView) findViewById(R.id.playlist_drawer);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_list, menu);
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
	

	// Show all possible PlayLists
	public void showPlayList() {
		playLists.setVisibility(ListView.VISIBLE);

		ArrayList<String> allPlayLists = new ArrayList<String>(); // TODO hier
																	// datenbankabfrage
																	// einbauen
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allPlayLists);
		playLists.setAdapter(adapter);
	}



	// Set on Click Listener from PlayLists
	public void setONClickPlayList() {
		playLists.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String playlist = (String)parent.getAdapter().getItem(position);
				sla.showSongsFromPlayList(playlist);
				Intent nextScreen = new Intent(getApplicationContext(), SongListActivity.class);
				startActivity(nextScreen);
			}
		});
	}
}
