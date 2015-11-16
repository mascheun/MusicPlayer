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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		db = new DatabaseClass(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();
		
		setContentView(R.layout.activity_main);
		
		mTitle = getTitle();
		 
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
 
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
        
        final EditText ed = (EditText) findViewById(R.id.editText1);
        
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	db.addPlayList(ed.getText().toString());
            }
        });
         
        final TextView tv = (TextView) findViewById(R.id.textView1);
         
        button1 = (Button) findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	ArrayList<String> lists = db.showPlayLists();
            	String s = "";
            	for(String text : lists) {
            		s = s + text + "\n";
            	}
            	tv.setText(s);
            }
        });
		
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
	
}
