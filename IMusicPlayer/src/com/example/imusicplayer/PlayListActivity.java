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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;
import functions.Item;
import functions.ListViewWithChkBoxAdapter;
import functions.SongsManager;

public class PlayListActivity extends Activity {

	private SongsManager sm;
	private ListView playLists;
	private ListView editPlView;
	private DatabaseClass db;
	private RelativeLayout homePL;
	private RelativeLayout addPL;
	private LinearLayout withCbView;
	private LinearLayout plConfig;
	private RelativeLayout editPL;
	private Button addPlOk;
	private Button addPlCancel;
	private Button cbButtonOk;
	private Button cbButtonCancel;
	private Button confPlAddSong;
	private Button confPlDeleteSong;
	private Button confPlCancel;
	private EditText writePlName;
	private ListViewWithChkBoxAdapter lvAdapter;
	private ListView viewWithCheckbock;
	private ListView songInConfig;
	private String editedPl = "";
	public int mode = -1;

	private ArrayList<Item> itemList = new ArrayList<Item>();
	private ArrayList<HashMap<String, String>> songsOnSDCard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list);

		db = DatabaseClass.getInstance();
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		sm = new SongsManager();

		playLists = (ListView) findViewById(R.id.playlist_drawer);
		editPlView = (ListView) findViewById(R.id.edit_pl_view);
		songInConfig = (ListView) findViewById(R.id.songs_in_cofig_pl);

		homePL = (RelativeLayout) findViewById(R.id.HomePlayListView);
		addPL = (RelativeLayout) findViewById(R.id.AddPlayListView);
		withCbView = (LinearLayout) findViewById(R.id.WithCBView);
		editPL = (RelativeLayout) findViewById(R.id.EditPlayListView);
		plConfig = (LinearLayout) findViewById(R.id.PlayListConfigView);

		addPlOk = (Button) findViewById(R.id.buttonOkAdd);
		addPlCancel = (Button) findViewById(R.id.buttonCancelAdd);
		cbButtonOk = (Button) findViewById(R.id.cb_ok);
		cbButtonCancel = (Button) findViewById(R.id.cb_cancel);

		confPlAddSong = (Button) findViewById(R.id.add_song);
		confPlDeleteSong = (Button) findViewById(R.id.delete_song);
		confPlCancel = (Button) findViewById(R.id.cancel_config);

		writePlName = (EditText) findViewById(R.id.editTextPlName);
		viewWithCheckbock = (ListView) findViewById(R.id.delete_pl_view);

		setONClickListener();
		songsOnSDCard = sm.getPlayList();
		showPlayList(playLists);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Gibt den ActionBar-Buttons Funktionen
		switch (item.getItemId()) {
		case R.id.add_playlist:
			editPL.setVisibility(RelativeLayout.INVISIBLE);
			withCbView.setVisibility(RelativeLayout.INVISIBLE);
			homePL.setVisibility(RelativeLayout.INVISIBLE);
			addPL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.delete_playlist:
			showPlayListToDelete();
			editPL.setVisibility(RelativeLayout.INVISIBLE);
			addPL.setVisibility(RelativeLayout.INVISIBLE);
			homePL.setVisibility(RelativeLayout.INVISIBLE);
			withCbView.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.edit_playlist:
			showPlayList(editPlView);
			withCbView.setVisibility(RelativeLayout.INVISIBLE);
			addPL.setVisibility(RelativeLayout.INVISIBLE);
			homePL.setVisibility(RelativeLayout.INVISIBLE);
			editPL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.song_list:
			Intent songListScreen = new Intent(getApplicationContext(), SongListActivity.class);
			Bundle b = new Bundle();
			b.putInt(Constants.MODE, Constants.REGULARSONG); // Your id
			songListScreen.putExtras(b); // Put your id to your next Intent
			startActivity(songListScreen);
			return true;
		case R.id.exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Show all possible PlayLists
	public void showPlayList(ListView view) {
		view.setVisibility(ListView.VISIBLE);

		ArrayList<String> allPlayLists = new ArrayList<String>();

		allPlayLists = db.showPlayLists();

		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allPlayLists);
		view.setAdapter(adapter);
	}

	// Show all possible PlayLists for deleting
	public void showPlayListToDelete() {
		ArrayList<String> allPlayLists = new ArrayList<String>();

		allPlayLists = db.showPlayLists();
		itemList = new ArrayList<Item>();

		for (String plName : allPlayLists) {
			itemList.add(new Item(plName));
		}

		lvAdapter = new ListViewWithChkBoxAdapter(itemList, this);
		viewWithCheckbock.setAdapter(lvAdapter);
		mode = Constants.MODEDELETINGPL;
	}

	private void showAddSongToPlayList(String playlist) {
		ArrayList<String> songList = new ArrayList<String>();
		songList.addAll(fillAllSongsInList());
		itemList = new ArrayList<Item>();

		for (String songName : songList) {
			itemList.add(new Item(songName));
		}

		lvAdapter = new ListViewWithChkBoxAdapter(itemList, this);
		viewWithCheckbock.setAdapter(lvAdapter);
		mode = Constants.MODEADDSONGTOPL;
	}

	private void showDeleteSongFromPlayList(String playlist) {
		ArrayList<String> songList = new ArrayList<String>();
		songList.addAll(db.showSongInPlaylist(playlist));
		itemList = new ArrayList<Item>();

		for (String songName : songList) {
			itemList.add(new Item(songName));
		}

		lvAdapter = new ListViewWithChkBoxAdapter(itemList, this);
		viewWithCheckbock.setAdapter(lvAdapter);
		mode = Constants.MODEDELETESONGFROMPL;
	}

	// Shows songs from PlayList
	public void showSongsFromPlayList(String playlist) {
		ArrayList<String> songList = new ArrayList<String>();
		if (playlist == null) {
			return;
		}
		songList.addAll(db.showSongInPlaylist(playlist));
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		songInConfig.setAdapter(adapter);
	}

	public ArrayList<String> fillAllSongsInList() {
		ArrayList<HashMap<String, String>> allSongList = songsOnSDCard;
		ArrayList<String> songList = new ArrayList<String>();

		for (HashMap<String, String> hm : allSongList) {
			songList.add(hm.get("songTitle"));
		}
		return songList;
	}

	// Set on Click Listener from PlayLists
	public void setONClickListener() {
		playLists.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String playlist = (String) parent.getAdapter().getItem(position);
				Intent nextScreen = new Intent(getApplicationContext(), SongListActivity.class);
				Bundle b = new Bundle();
				b.putInt(Constants.MODE, Constants.SONGSFROMPLAYLIST); // Your
																		// id
				b.putString(Constants.PLAYLISTKEY, playlist);
				nextScreen.putExtras(b); // Put your id to your next Intent
				startActivity(nextScreen);
				finish();
			}
		});

		editPlView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String playlist = (String) parent.getAdapter().getItem(position);
				showSongsFromPlayList(playlist);
				editedPl = playlist;
				editPL.setVisibility(RelativeLayout.INVISIBLE);
				plConfig.setVisibility(LinearLayout.VISIBLE);

			}
		});

		addPlOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String plName = "";
				plName = writePlName.getText().toString();
				writePlName.setText("");
				db.addPlayList(plName);
				addPL.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
				showPlayList(playLists);
			}
		});

		addPlCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				writePlName.setText("");
				addPL.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
			}
		});

		confPlAddSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showAddSongToPlayList(editedPl);
				plConfig.setVisibility(LinearLayout.INVISIBLE);
				withCbView.setVisibility(RelativeLayout.VISIBLE);
			}
		});

		confPlDeleteSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDeleteSongFromPlayList(editedPl);
				plConfig.setVisibility(LinearLayout.INVISIBLE);
				withCbView.setVisibility(RelativeLayout.VISIBLE);
			}
		});

		confPlCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				plConfig.setVisibility(LinearLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
			}
		});

		cbButtonOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				switch (mode) {
				// Mode for deleting Playlists
				case 1:
					for (Item item : itemList) {
						if (item.isSelected()) {
							db.deletePlayList(item.getName());
						}
					}
					withCbView.setVisibility(RelativeLayout.INVISIBLE);
					homePL.setVisibility(RelativeLayout.VISIBLE);
					showPlayList(playLists);
					break;
				// Mode for Adding songs to Playlists
				case 2:
					for (Item item : itemList) {
						if (item.isSelected()) {
							db.addSongToPlayList(editedPl, item.getName());
						}
					}
					withCbView.setVisibility(RelativeLayout.INVISIBLE);
					homePL.setVisibility(RelativeLayout.VISIBLE);
					showPlayList(playLists);
					break;
				// Mode for deleting songs from Playlists
				case 3:
					for (Item item : itemList) {
						if (item.isSelected()) {
							db.deleteSongFromPlayList(editedPl, item.getName());
						}
					}
					withCbView.setVisibility(RelativeLayout.INVISIBLE);
					homePL.setVisibility(RelativeLayout.VISIBLE);
					showPlayList(playLists);
					break;
				default:
					break;
				}

			}
		});

		cbButtonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				withCbView.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
			}
		});

	}

}
