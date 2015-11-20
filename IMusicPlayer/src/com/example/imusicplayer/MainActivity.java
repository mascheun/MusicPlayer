package com.example.imusicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import com.example.imusicplayer.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import database.DatabaseClass;
import functions.SongsManager;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private SongsManager sm = new SongsManager();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private String[] drawerTitles;
	private String[] drawerSubtitles;
	private int[] drawerIcons;
	private DatabaseClass db;
	Button button;
	Button button1;
	ListView showSongs;
	ListView playLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		db = new DatabaseClass(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		mTitle = getTitle();

		initializeAllGuiObjects();

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		createMenue();

		setONClickSong();
		
		setONClickPlayList();

		//showSongList(sm.getPlayList());

		// sm.playSong(1, sm.getPlayList());

	}

	// Fügt das Menü hinzu / ActionBar Einträge
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// Versteckt die ActionBar-Einträge, sobald der Drawer ausgefahren is
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.add_playlist).setVisible(!drawerOpen);
		menu.findItem(R.id.edit_playlist).setVisible(!drawerOpen);
		menu.findItem(R.id.delete_playlist).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Öffnet und schließt den Navigation Drawer bei Klick auf den Titel/das
		// Icon in der ActionBar
		if (item.getItemId() == android.R.id.home) {
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
		}

		// Gibt den ActionBar-Buttons Funktionen
		switch (item.getItemId()) {
		case R.id.add_playlist:
			Toast.makeText(getApplicationContext(),
                    "Add Playlist Clicked",Toast.LENGTH_SHORT).show();
			showSongList(sm.getPlayList());
			return true;
		case R.id.edit_playlist:
			Toast.makeText(getApplicationContext(),
                    "Add Playlist Clicked",Toast.LENGTH_SHORT).show();
			return true;
		case R.id.delete_playlist:
			Toast.makeText(getApplicationContext(),
                    "Add Playlist Clicked",Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Listener für die Navigation Drawer Einträge - Achtung: Zählung beginnt
	// bei 0!
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {

			} else if (position == 1) {

			} else if (position == 2) {

			}

			mDrawerList.setItemChecked(position, true);
			mTitle = drawerTitles[position];
			getActionBar().setTitle(mTitle);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	//Initialize all GUI Objects
	public void initializeAllGuiObjects() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		showSongs = (ListView) findViewById(R.id.songs_drawer);
		playLists = (ListView) findViewById(R.id.playlist_drawer);

	}

	// Create the menue
	public void createMenue() {
		// Hole die Titel aus einem Array aus der strings.xml
		drawerTitles = getResources().getStringArray(R.array.drawerTitles_array);
		// Setzt die Icons zu den Einträgen
		drawerIcons = new int[] { android.R.drawable.ic_menu_manage, android.R.drawable.ic_menu_edit,
				android.R.drawable.ic_menu_delete };

		// Erstellt den neuen MenuAdapter aus der Klasse MenuListAdapter
		MenuListAdapter mMenuAdapter = new MenuListAdapter(this, drawerTitles, drawerSubtitles, drawerIcons);
		mDrawerList.setAdapter(mMenuAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// Bereitet die ActionBar auf den Navigation Drawer vor
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Fügt den Navigation Drawer zur ActionBar hinzu
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	// Show all possible Songs
	public void showSongList(ArrayList<HashMap<String, String>> allSongListHash) {

		ArrayList<String> allSongList = new ArrayList<String>();

		for (HashMap<String, String> hm : allSongListHash) {
			allSongList.add(hm.get("songTitle"));
		}

		showSongs.setVisibility(ListView.VISIBLE);

		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allSongList);
		showSongs.setAdapter(adapter);
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

	// Set on Click Listener from Songs
	public void setONClickSong() {
		showSongs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String song = (String)parent.getAdapter().getItem(position);
				sm.playSong(position, sm.getPlayList(), song);
				showSongs.setVisibility(ListView.INVISIBLE);
			}
		});
	}

	// Set on Click Listener from PlayLists
	public void setONClickPlayList() {
		playLists.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String playlist = (String)parent.getAdapter().getItem(position);
				playLists.setVisibility(ListView.INVISIBLE);
				showSongList(playlist);
			}
		});
	}

	// Shows songs from PlayList
	public void showSongList(String playlist) {

		showSongs.setVisibility(ListView.VISIBLE);
		ArrayList<String> allSongList = db.showSongInPlaylist(playlist);

		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allSongList);
		showSongs.setAdapter(adapter);
	}

}
