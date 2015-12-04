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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;
import functions.SeekBarHelper;
import functions.SongsManager;

public class SongListActivity extends Activity {

	private SongsManager sm;
	private ArrayList<String> songList;

	private SeekBarHelper seekBarThread;

	private ListView showSongs;
	private Button searchBt;
	private EditText searchText;
	private LinearLayout songListLayout;
	private LinearLayout playSongView;

	private int playedSongPos = -1;

	private TextView playedSongTv;
	private TextView currentSongTimePos;
	private TextView maxSongTimePos;
	private ImageButton btPlay;
	private ImageButton btStop;
	private ImageButton btNextSong;
	private ImageButton btPreviousSong;
	private ImageButton loopSong;
	private ImageButton randomSong;
	private SeekBar songProgressBar;

	private DatabaseClass db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_list);

		db = DatabaseClass.getInstance();

		sm = new SongsManager();
		seekBarThread = new SeekBarHelper();
		seekBarThread.setSongManager(sm);

		songListLayout = (LinearLayout) findViewById(R.id.song_list_layout);
		showSongs = (ListView) findViewById(R.id.songs_drawer);
		searchBt = (Button) findViewById(R.id.bt_search);
		searchText = (EditText) findViewById(R.id.edit_text_song_search);

		// SongActivity Ansicht
		playSongView = (LinearLayout) findViewById(R.id.Play_Song_View);
		playedSongTv = (TextView) findViewById(R.id.song_name_textview);
		btPlay = (ImageButton) findViewById(R.id.imageButton3);
		btStop = (ImageButton) findViewById(R.id.imageButton1);
		btNextSong = (ImageButton) findViewById(R.id.imageButton2);
		btPreviousSong = (ImageButton) findViewById(R.id.imageButton4);
		randomSong = (ImageButton) findViewById(R.id.imageButton6);
		loopSong = (ImageButton) findViewById(R.id.imageButton5);
		songProgressBar = (SeekBar) findViewById(R.id.seekBar1);
		currentSongTimePos = (TextView) findViewById(R.id.songCurrentDurationLabel);
		maxSongTimePos = (TextView) findViewById(R.id.songTotalDurationLabel);

		seekBarThread.setSongProgressBar(songProgressBar);
		seekBarThread.start();

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
		if (playlist == null) {
			return;
		}
		songList.addAll(db.showSongInPlaylist(playlist));
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songList);
		showSongs.setAdapter(adapter);
	}

	// Shows searched songs
	public void showSearchedSongs(ArrayList<String> searchedSongs) {
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchedSongs);
		showSongs.setAdapter(adapter);
	}
	
	public String showProgress(int progress) {
		String output = "";
		output = progress / 60 + ":" + progress % 60;
		
		return output;
	}
	
	public void setProgressBarSettings() {
		songProgressBar.setMax(sm.getDuration()/1000);
		maxSongTimePos.setText(showProgress(sm.getDuration()/1000));
	}

	// Set on Click Listener from Songs
	public void setONClick() {
		showSongs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String song = (String) parent.getAdapter().getItem(position);
				playedSongPos = position;
				sm.playSong(sm.getPlayList(), song);
				setProgressBarSettings();
				playedSongTv.setText(song);
				songListLayout.setVisibility(LinearLayout.INVISIBLE);
				playSongView.setVisibility(RelativeLayout.VISIBLE);

			}
		});
		searchBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> searchedSongs = new ArrayList<String>();
				for (String song : songList) {
					if (song.contains(searchText.getText().toString())) {
						searchedSongs.add(song);
					}
				}
				searchText.setText("");
				showSearchedSongs(searchedSongs);
			}
		});
		btPlay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sm.startSong();
				seekBarThread.setRunProgressBar(true);
			}
		});
		btStop.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sm.stopSong();
				seekBarThread.setRunProgressBar(false);
			}
		});
		randomSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!randomSong.isActivated()) {
					loopSong.setActivated(false);
				}
				randomSong.setActivated(!randomSong.isActivated());
			}
		});
		loopSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!loopSong.isActivated()) {
					randomSong.setActivated(false);
				}
				loopSong.setActivated(!loopSong.isActivated());
			}
		});
		btNextSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				seekBarThread.setRunProgressBar(false);
				songProgressBar.setProgress(0);
				playedSongPos++;
				if (playedSongPos >= songList.size()) {
					playedSongPos = 0;
				}
				String song = songList.get(playedSongPos);
				sm.playSong(sm.getPlayList(), song);
				setProgressBarSettings();
				playedSongTv.setText(song);

			}
		});
		btPreviousSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				seekBarThread.setRunProgressBar(false);
				songProgressBar.setProgress(0);
				playedSongPos--;
				if (playedSongPos <= -1) {
					playedSongPos = songList.size() - 1;
				}
				String song = songList.get(playedSongPos);
				sm.playSong(sm.getPlayList(), song);
				setProgressBarSettings();
				playedSongTv.setText(song);
			}
		});
		songProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				sm.setSongTimePosition(songProgressBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				currentSongTimePos.setText(showProgress(songProgressBar.getProgress()));
			}
		});
	}

}
