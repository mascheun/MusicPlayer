package com.example.imusicplayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import database.DatabaseClass;
import functions.Item;
import functions.ListViewWithChkBoxAdapter;
import functions.SeekBarHelper;
import functions.SongsManager;

public class MainActivity extends Activity {

	private SongsManager sm;
	private ArrayList<String> songList;

	private SeekBarHelper seekBarThread;

	private ListView showSongs;
	private Button searchBt;
	private EditText searchText;
	private LinearLayout songListLayout;
	private LinearLayout playSongView;

	private int playedSongPos = -1;

	private int strength = -9999999;
	private String name = "";
	private boolean run = true;

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

	private Boolean randomActive = false;
	private Boolean loopActive = false;
	private Boolean bluetThread = false;
	private Boolean finishedReceive = false;
	private Boolean taskExecuted = false;

	private ArrayList<HashMap<String, String>> songsOnSDCard;

	private ListView showPlayListView;
	private ListView editPlView;
	private ListView showConfigPlSongs;
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
	private String editedPl = "";
	public int mode = -1;
	public ArrayList<String[]> deviceListStrength = new ArrayList<String[]>();

	private ArrayList<Item> itemList = new ArrayList<Item>();

	private DatabaseClass db;
	protected static final String TAG = "ZS-A2dp";
	private final int REQUEST_ENABLE_BT = 1;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothA2dp mBluetLoudspeaker;

	public HashMap<String, BluetoothDevice> deviceList;
	private List<BluetoothDevice> devList;
	private UpdateConnection task = new UpdateConnection();

	private class UpdateConnection extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... device) {
		  taskExecuted = true;
			while (true) {
				while (run) {
					mBluetoothAdapter.startDiscovery();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return name;
					}
					mBluetoothAdapter.cancelDiscovery();
					try {
            Thread.sleep(3000);
          } catch (InterruptedException e) {
            e.printStackTrace();
            return name;
          }
				}
				while (!run) {
					// Do Nothing
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		registerReceiver(mReceiver, new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		db = DatabaseClass.getInstance();
		db.setActivity(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		deviceList = new HashMap<String, BluetoothDevice>();
		devList = new ArrayList<BluetoothDevice>();

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

		// PlaylistActivity Ansicht
		showPlayListView = (ListView) findViewById(R.id.playlist_drawer);
		editPlView = (ListView) findViewById(R.id.edit_pl_view);
		showConfigPlSongs = (ListView) findViewById(R.id.songs_in_config_pl);

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

		setONClick();

		songsOnSDCard = sm.getPlayList();

		showPlayList(showPlayListView);
		showSongList();

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
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
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.played_song:
			allLayoutsInvisible();
			playSongView.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.song_list:
			showSongList();
			allLayoutsInvisible();
			songListLayout.setVisibility(LinearLayout.VISIBLE);
			return true;
		case R.id.playlist_manager:
			allLayoutsInvisible();
			homePL.setVisibility(LinearLayout.VISIBLE);
			return true;
		case R.id.add_playlist:
			allLayoutsInvisible();
			addPL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.delete_playlist:
			showPlayListToDelete();
			allLayoutsInvisible();
			withCbView.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.edit_playlist:
			showPlayList(editPlView);
			allLayoutsInvisible();
			editPL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.bluetooth_activator:
			showBluetoothdevices();
			return true;
		case R.id.exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void allLayoutsInvisible() {
		withCbView.setVisibility(RelativeLayout.INVISIBLE);
		addPL.setVisibility(RelativeLayout.INVISIBLE);
		homePL.setVisibility(RelativeLayout.INVISIBLE);
		editPL.setVisibility(RelativeLayout.INVISIBLE);
		playSongView.setVisibility(LinearLayout.INVISIBLE);
		songListLayout.setVisibility(LinearLayout.INVISIBLE);
		plConfig.setVisibility(LinearLayout.INVISIBLE);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// ******************************BLUETOOTH**************************************************

	public boolean isBluetoothAvailable() {
		boolean available = true;
		if (mBluetoothAdapter == null) {
			available = false;
		}

		return available;
	}

	public boolean enableBluetooth() {
		boolean enableBluet = true;
		if (!mBluetoothAdapter.isEnabled()) {

			enableBluet = false;
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			try {
				this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} catch (Exception e) {
			  Log.d("MainActivity", "Ask for enable bluetooth failed");
			}

		}

		return enableBluet;
	}

	boolean mIsA2dpReady = false;

	void setIsA2dpReady(boolean ready) {
		mIsA2dpReady = ready;
	}

	public synchronized void showBluetoothdevices() {
		if (isBluetoothAvailable()) {
			if (!enableBluetooth()) {
				return;
			}
			deviceList.clear();
			findDevices();
			if (deviceList == null) {
				return;
			}
			if (bluetThread == false) {
				setRun(true);
				if(!taskExecuted) {
				  task.execute("");
				}
				bluetThread = true;
			} else {
				setRun(false);
				bluetThread = false;
			}

		}

	}

	private void setRun(boolean b) {
		this.run = b;

	}

	public void findDevices() {

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				String dev = device.getName() + "\n" + device.getAddress();
				deviceList.put(dev, device);
				devList.add(device);
			}

		}

	}

	public void connectDev(BluetoothDevice device) {
		Method connect = null;
		mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.A2DP);
		try {
			connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
		} catch (NoSuchMethodException e) {
			Log.d("MainActivity", "can not create connect method");
		}

		try {
		  if(mBluetLoudspeaker != null && device != null) {
		    connect.invoke(mBluetLoudspeaker, device);
		  }
		} catch (IllegalAccessException e) {
			Log.d("MainActivity", "Illegal Access");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Log.d("MainActivity", "Illegal Argument");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.d("MainActivity", "Invocation Target");
			e.printStackTrace();
		}
//
		// Close proxy connection after use.
		mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetLoudspeaker);
	}

	private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
		public void onServiceConnected(int profile, BluetoothProfile proxy) {
			if (profile == BluetoothProfile.A2DP) {
				mBluetLoudspeaker = (BluetoothA2dp) proxy;
			}
		}

		public void onServiceDisconnected(int profile) {
			if (profile == BluetoothProfile.A2DP) {
				mBluetLoudspeaker = null;
			}
		}
	};

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			finishedReceive = false;
			String[] Values = new String[2];
			String action = intent.getAction();
			Boolean newEntry = true;
			int counter = 0;
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				for(String[] entry : deviceListStrength) {
				  if(entry[0].equals(name)) {
				    Values[0] = name;
				    Values[1] = Integer.toString(rssi);
				    deviceListStrength.set(counter, Values);
				    newEntry = false;
				    break;
				  }
				  counter++;
				}
				if(newEntry) {
  				Values[0] = name;
  				Values[1] = Integer.toString(rssi);
  				deviceListStrength.add(Values);
				}
			}

			if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
				if (state == BluetoothA2dp.STATE_CONNECTED) {
					setIsA2dpReady(true);
				} else if (state == BluetoothA2dp.STATE_DISCONNECTED) {
					setIsA2dpReady(false);
				}
			} else if (action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
				int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
				if (state == BluetoothA2dp.STATE_PLAYING) {
					Log.d(TAG, "A2DP start playing");
				} else {
					Log.d(TAG, "A2DP stop playing");
				}
			}
			finishedReceive = true;
			checkIfChangeConnect();
		}

	};

	private void checkIfChangeConnect() {
		if (finishedReceive) {
			for (String[] dev : deviceListStrength) {
				int s = Integer.parseInt(dev[1]);
				if (s > strength) {
					strength = s;
					name = dev[0];
				}
			}
			if (strength != -9999999) {
			  for(BluetoothDevice dev : devList) {
			    if(dev.getName().equals(name)) {
			      connectDev(dev);
			    }
			  }
			}
		}

	}

	// Songlist
	// view*****************************************************************************************

	// Show all possible Songs
	public void showSongList() {
		songListLayout.setVisibility(LinearLayout.VISIBLE);
		songList = new ArrayList<String>();

		for (HashMap<String, String> hm : songsOnSDCard) {
			songList.add(hm.get("songTitle"));
		}
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.text_layout, songList);
		showSongs.setAdapter(adapter);
		songListLayout.setVisibility(LinearLayout.VISIBLE);
	}

	// Shows songs from PlayList
	public void showSongsFromPlayList(String playlist) {
		songList = new ArrayList<String>();
		if (playlist == null) {
			return;
		}
		songList.addAll(db.showSongInPlaylist(playlist));
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.text_layout, songList);
		showSongs.setAdapter(adapter);
		showConfigPlSongs.setAdapter(adapter);
	}

	// Shows searched songs
	public void showSearchedSongs(ArrayList<String> searchedSongs) {
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.text_layout, searchedSongs);
		showSongs.setAdapter(adapter);
	}

	public String showProgress(int progress) {
		String output = "";
		output = progress / 60 + ":" + progress % 60;

		return output;
	}

	public void setProgressBarSettings() {
		songProgressBar.setMax(sm.getDuration() / 1000 - 1);
		maxSongTimePos.setText(showProgress(sm.getDuration() / 1000 - 1));
	}

	// Set on Click Listener from Songs
	public void setONClick() {
		showSongs.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String song = (String) parent.getAdapter().getItem(position);
				playedSongPos = position;
				sm.playSong(songsOnSDCard, song);
				setProgressBarSettings();
				playedSongTv.setText(song);
				songListLayout.setVisibility(LinearLayout.INVISIBLE);
				playSongView.setVisibility(RelativeLayout.VISIBLE);
				sm.startSong();
				seekBarThread.setRunProgressBar(true);

			}
		});
		searchBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ArrayList<String> searchedSongs = new ArrayList<String>();
				for (String song : songList) {
					if (song.toLowerCase(Locale.US).contains(searchText.getText().toString().toLowerCase(Locale.US))) {
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
				if (!randomActive) {
					loopActive = false;
					loopSong.setImageResource(R.drawable.replay_icon);
					randomSong.setImageResource(R.drawable.shuffle_icon_active);
					randomActive = true;
				} else {
					randomSong.setImageResource(R.drawable.shuffle_icon);
					randomActive = false;
				}
				randomSong.setActivated(!randomSong.isActivated());
			}
		});
		loopSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!loopActive) {
					randomActive = false;
					randomSong.setImageResource(R.drawable.shuffle_icon);
					loopSong.setImageResource(R.drawable.replay_icon_active);
					loopActive = true;
				} else {
					loopSong.setImageResource(R.drawable.replay_icon);
					loopActive = false;
				}
			}
		});
		btNextSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				playedSongPos++;
				if (playedSongPos >= songList.size()) {
					playedSongPos = 0;
				}
				String song = songList.get(playedSongPos);
				sm.playSong(songsOnSDCard, song);
				setProgressBarSettings();
				playedSongTv.setText(song);
				sm.startSong();

			}
		});
		btPreviousSong.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				playedSongPos--;
				if (playedSongPos <= -1) {
					playedSongPos = songList.size() - 1;
				}
				String song = songList.get(playedSongPos);
				sm.playSong(songsOnSDCard, song);
				setProgressBarSettings();
				playedSongTv.setText(song);
				sm.startSong();
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
				if (songProgressBar.getProgress() == songProgressBar.getMax()) {
					System.out.println("Test1");
					if (loopActive) {
						songProgressBar.setProgress(0);
						sm.startSong();
					}
					if (randomActive) {
						songProgressBar.setProgress(0);
						int randomSong;
						randomSong = (int) (Math.random() * songList.size());
						sm.playSong(songsOnSDCard, songList.get(randomSong));
						playedSongTv.setText(songList.get(randomSong));
						setProgressBarSettings();
						sm.startSong();
					}
				}
			}
		});
	}

	// Playlist
	// view*****************************************************************************************

	// Show all possible PlayLists
	public void showPlayList(ListView view) {
		view.setVisibility(ListView.VISIBLE);

		ArrayList<String> allPlayLists = new ArrayList<String>();

		allPlayLists = db.showPlayLists();

		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.text_layout, allPlayLists);
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
		showPlayListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String playlist = (String) parent.getAdapter().getItem(position);
				showSongsFromPlayList(playlist);
				allLayoutsInvisible();
				songListLayout.setVisibility(LinearLayout.VISIBLE);
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
				showPlayList(showPlayListView);
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
					showPlayList(showPlayListView);
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
					showPlayList(showPlayListView);
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
					showPlayList(showPlayListView);
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
