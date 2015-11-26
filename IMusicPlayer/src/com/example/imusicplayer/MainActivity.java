package com.example.imusicplayer;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import database.DatabaseClass;
import functions.SongsManager;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle;
	private String[] drawerTitles;
	private String[] drawerSubtitles;
	private int[] drawerIcons;
	private DatabaseClass db;
	private SongsManager sm = new SongsManager();
	SongListActivity sla;
	PlayListActivity pla;
	SongActivity sa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		sla = new SongListActivity();
		pla = new PlayListActivity();
		sa = new SongActivity();

		db = new DatabaseClass(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		mTitle = getTitle();

		initializeAllGuiObjects();

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		createMenue();

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
		menu.findItem(R.id.song_list).setVisible(!drawerOpen);
		menu.findItem(R.id.playlist_manager).setVisible(!drawerOpen);
		menu.findItem(R.id.exit).setVisible(!drawerOpen);
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
		case R.id.song_list:
			Intent songScreen = new Intent(getApplicationContext(), SongListActivity.class);
			startActivity(songScreen);
			return true;
		case R.id.playlist_manager:
			Intent playListScreen = new Intent(getApplicationContext(), PlayListActivity.class);
			startActivity(playListScreen);
			return true;
		case R.id.exit:
			sla.finish();
			pla.finish();
			sa.finish();
			finish();
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

	// Initialize all GUI Objects
	public void initializeAllGuiObjects() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

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

}
