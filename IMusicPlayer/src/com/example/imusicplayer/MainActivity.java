package com.example.imusicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;

public class MainActivity extends Activity {

	private DatabaseClass db;
	private ListView devices;
	SongListActivity sla;
	PlayListActivity pla;
	public static MainActivity instance = null;
	private static final int REQUEST_ENABLE_BT = 1;
	public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothDevice device1;
	private static final UUID MY_UUID = UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB");

	private ConnectThread connectThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;

		setContentView(R.layout.activity_main);

		sla = new SongListActivity();
		pla = new PlayListActivity();

		db = DatabaseClass.getInstance();
		db.setActivity(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		initializeAllGuiObjects();

		setONClickListener();

		showBluetoothdevices();

		switchToSonglist();

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

	// ************************Bluetoothtest

	// Set on Click Listener from PlayLists
	public void setONClickListener() {
		devices.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				connectThread = new ConnectThread(device1);
				connectThread.run();
			}
		});
	}

	// Shows songs from PlayList
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

	public ArrayList<String> findDevices() {

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		ArrayList<String> allBluetoothDevices = new ArrayList<String>();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				allBluetoothDevices.add(device.getName() + "\n" + device.getAddress());
				device1 = device;
			}

			return allBluetoothDevices;

		}

		return null;
	}

	// TODO
	private class ConnectThread extends Thread {

		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				Toast.makeText(MainActivity.this, "create rfcomm socket", Toast.LENGTH_LONG).show();
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) {
				Toast.makeText(MainActivity.this, "can't create rfcomm socket", Toast.LENGTH_LONG).show();
			}
			Toast.makeText(MainActivity.this, "assign socket", Toast.LENGTH_LONG).show();
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			Toast.makeText(MainActivity.this, "cancel discovery", Toast.LENGTH_LONG).show();
			mBluetoothAdapter.cancelDiscovery();

			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
				Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					Toast.makeText(MainActivity.this, "connection closed", Toast.LENGTH_LONG).show();
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;
			}

			// Do work to manage the connection (in a separate thread)
			// manageConnectedSocket(mmSocket);
		}

		/** Will cancel an in-progress connection, and close the socket */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

}
