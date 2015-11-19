package com.example.imusicplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.imusicplayer.R;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import database.DatabaseClass;
import functions.SongsManager;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	
	private SongsManager sm = new SongsManager();
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
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
		
		db = new DatabaseClass(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();
		
		setContentView(R.layout.activity_main);
		
		mTitle = getTitle();
		
		initializeAllGuiObjects();

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
 
        createMenue();
        
        getPlayList();
		
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
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Öffnet und schließt den Navigation Drawer bei Klick auf den Titel/das Icon in der ActionBar
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
 
        // Gibt den ActionBar-Buttons Funktionen
        switch (item.getItemId()) {
            case R.id.action_settings:
            	Toast.makeText(null, "Aktualisieren gedrückt!", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
 
    // Listener für die Navigation Drawer Einträge - Achtung: Zählung beginnt bei 0!
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
    
	 public void initializeAllGuiObjects() {
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);
	        showSongs = (ListView) findViewById(R.id.songs_drawer);
	        playLists = (ListView) findViewById(R.id.playlist_drawer);
		
	}
    
    public void createMenue() {
        // Hole die Titel aus einem Array aus der strings.xml
        drawerTitles = getResources().getStringArray(R.array.drawerTitles_array);
        // Setzt die Icons zu den Einträgen
        drawerIcons = new int[] {android.R.drawable.ic_menu_manage, android.R.drawable.ic_menu_edit, android.R.drawable.ic_menu_delete};
 
        // Erstellt den neuen MenuAdapter aus der Klasse MenuListAdapter
        MenuListAdapter mMenuAdapter = new MenuListAdapter(this, drawerTitles, drawerSubtitles, drawerIcons);
        mDrawerList.setAdapter(mMenuAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
 
        // Bereitet die ActionBar auf den Navigation Drawer vor
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
 
        // Fügt den Navigation Drawer zur ActionBar hinzu
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
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
    
    public void showSongList(ArrayList<String> allSongList) {
    	showSongs.setVisibility(ListView.VISIBLE);

        ListAdapter adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, allSongList);
        showSongs.setAdapter(adapter);
    }
    
    public void showPlayList(ArrayList<String> allPlayLists) {
    	playLists.setVisibility(ListView.VISIBLE);

    	
        ListAdapter adapter = new ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, allPlayLists);
        playLists.setAdapter(adapter);
    }
    
    public void setONClickSong() {
    	showSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    	      @Override
    	      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
    	    	  showSongs.setVisibility(ListView.INVISIBLE);
    	    	  final String item = (String) parent.getItemAtPosition(position);
//    	    	  sm.playSong(item);
    	      }

    	    });
    }
    
    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList(){
    	File home = Environment.getExternalStorageDirectory();

        if (home.listFiles(/*new FileExtensionFilter()*/).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }
        // return songs list array
        return songsList;
    }
    
    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
    
}
