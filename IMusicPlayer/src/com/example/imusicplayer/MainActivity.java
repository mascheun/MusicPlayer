package com.example.imusicplayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;

public class MainActivity extends Activity {

	private DatabaseClass db;
	protected static final String TAG = "ZS-A2dp";
	private ListView devices;
	AudioManager mAudioManager;
	SongListActivity sla;
	PlayListActivity pla;
	public static MainActivity instance = null;
	private static final int REQUEST_ENABLE_BT = 1;
	public BluetoothAdapter mBluetoothAdapter;
	public BluetoothA2dp mBluetLoudspeaker;

	private HashMap<String, BluetoothDevice> deviceList;
	private List<BluetoothDevice> devList;
	private TextView rssi_msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;

		setContentView(R.layout.activity_main);

		sla = new SongListActivity();
		pla = new PlayListActivity();

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		registerReceiver(mReceiver, new IntentFilter(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED));
		registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

		rssi_msg = (TextView) findViewById(R.id.signal_list);
		rssi_msg.setText("Felix ist ein Noob");

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// mBluetoothAdapter.getProfileProxy(this, mA2dpListener,
		// BluetoothProfile.A2DP);

		db = DatabaseClass.getInstance();
		db.setActivity(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		deviceList = new HashMap<String, BluetoothDevice>();
		devList = new ArrayList<BluetoothDevice>();

		initializeAllGuiObjects();

		setONClickListener();

		showBluetoothdevices();

		switchToSonglist();

	}

	@Override
	protected void onDestroy() {
		// mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,
		// mBluetLoudspeaker);
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

		// Gibt den ActionBar-Buttons Funktionen
		switch (item.getItemId()) {
		case R.id.song_list:
			switchToSonglist();
			return true;
		case R.id.playlist_manager:
			Intent playListScreen = new Intent(getApplicationContext(), PlayListActivity.class);
			startActivity(playListScreen);
			return true;
		case R.id.exit:
			sla.finish();
			pla.finish();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void switchToSonglist() {
		Intent songListScreen = new Intent(getApplicationContext(), SongListActivity.class);
		Bundle b = new Bundle();
		b.putInt(Constants.MODE, Constants.REGULARSONG); // Your id
		songListScreen.putExtras(b); // Put your id to your next Intent
		startActivity(songListScreen);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// Initialize all GUI Objects
	public void initializeAllGuiObjects() {
		devices = (ListView) findViewById(R.id.divice_list);

	}

	// Set on Click Listener from PlayLists
	public void setONClickListener() {
		devices.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// connectDev(deviceList.get(devices.getItemAtPosition(position).toString()));
				Toast.makeText(MainActivity.this, "Clicked Device", Toast.LENGTH_SHORT).show();
				mBluetoothAdapter.startDiscovery();
				Toast.makeText(MainActivity.this, "End Discovery", Toast.LENGTH_SHORT).show();
			}
		});
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

			}

		}

		return enableBluet;
	}

	boolean mIsA2dpReady = false;

	void setIsA2dpReady(boolean ready) {
		mIsA2dpReady = ready;
		Toast.makeText(this, "A2DP ready ? " + (ready ? "true" : "false"), Toast.LENGTH_SHORT).show();

	}

	public void showBluetoothdevices() {
		if (isBluetoothAvailable()) {
			enableBluetooth();
			ArrayList<String> deviceList;

			deviceList = findDevices();
			if (deviceList == null) {
				return;
			}
			ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);
			devices.setAdapter(adapter);

		}

	}

	public ArrayList<String> findDevices() {

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		ArrayList<String> allBluetoothDevices = new ArrayList<String>();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				String dev = device.getName() + "\n" + device.getAddress();
				allBluetoothDevices.add(dev);
				deviceList.put(dev, device);
				devList.add(device);
			}

			return allBluetoothDevices;

		}

		return null;
	}

	public void connectDev(BluetoothDevice device) {
		Method connect = null;
		mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.A2DP);
		try {
			connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);

		} catch (NoSuchMethodException e) {
			Toast.makeText(MainActivity.this, "can not create connect method", Toast.LENGTH_LONG).show();
		}

		try {
			connect.invoke(mBluetLoudspeaker, device);
		} catch (IllegalAccessException e) {
			Toast.makeText(MainActivity.this, "Illegal Access", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			Toast.makeText(MainActivity.this, "Illegal Argument", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Toast.makeText(MainActivity.this, "Invocation Target", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

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
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
				String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
				rssi_msg.setText(rssi_msg.getText() + name + " => " + rssi + "dBm\n");
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
					Toast.makeText(MainActivity.this, "A2dp is playing", Toast.LENGTH_SHORT).show();
				} else {
					Log.d(TAG, "A2DP stop playing");
					Toast.makeText(MainActivity.this, "A2dp is stopped", Toast.LENGTH_SHORT).show();
				}
			}
		}

	};

}
