package com.example.imusicplayer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothSocket;
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
	BluetoothDevice device1;
	public BluetoothA2dp mBluetLoudspeaker;
	private static final UUID MY_UUID = UUID.fromString("0000110A-0000-1000-8000-00805F9B34FB");

	private ConnectThread connectThread;
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String action = intent.getAction();
        
        if(BluetoothDevice.ACTION_FOUND.equals(action)) {
            int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
            String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            Toast.makeText(MainActivity.this, name + " => " + rssi + "dBm\n", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "receive intent for action : " + action);
        if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
            if (state == BluetoothA2dp.STATE_CONNECTED) {
                setIsA2dpReady(true);
                //playMusic();
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

boolean mIsA2dpReady = false;
void setIsA2dpReady(boolean ready) {
    mIsA2dpReady = ready;
    Toast.makeText(this, "A2DP ready ? " + (ready ? "true" : "false"), Toast.LENGTH_SHORT).show();
    mBluetoothAdapter.startDiscovery();
}

private ServiceListener mA2dpListener = new ServiceListener() {

  @Override
  public void onServiceConnected(int profile, BluetoothProfile a2dp) {
      Log.d(TAG, "a2dp service connected. profile = " + profile);
      if (profile == BluetoothProfile.A2DP) {
        mBluetLoudspeaker = (BluetoothA2dp) a2dp;
          if (mAudioManager.isBluetoothA2dpOn()) {
              setIsA2dpReady(true);
              //playMusic();
              
          } else {
              Log.d(TAG, "bluetooth a2dp is not on while service connected");
          }
      }
  }

  @Override
  public void onServiceDisconnected(int profile) {
      setIsA2dpReady(false);
  }

};

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
	    
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    mBluetoothAdapter.getProfileProxy(this, mA2dpListener , BluetoothProfile.A2DP);


		db = DatabaseClass.getInstance();
		db.setActivity(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();

		initializeAllGuiObjects();

		setONClickListener();

		showBluetoothdevices();

		switchToSonglist();

	}
	
	@Override
	protected void onDestroy() {
	  mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, mBluetLoudspeaker);
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

		//private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
//			BluetoothSocket tmp = null;
			mmDevice = device;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
//			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
//				Toast.makeText(MainActivity.this, "create rfcomm socket", Toast.LENGTH_LONG).show();
				//TODO Hier kann es sein das dies ein Bluetooth 4 standard ist und der Lautsprecher einen geringeren Standard verwendet
//				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
//			} catch (IOException e) {
//				Toast.makeText(MainActivity.this, "can't create rfcomm socket", Toast.LENGTH_LONG).show();
//			}
//			Toast.makeText(MainActivity.this, "assign socket", Toast.LENGTH_LONG).show();
//			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
//			Toast.makeText(MainActivity.this, "cancel discovery", Toast.LENGTH_LONG).show();
//			mBluetoothAdapter.cancelDiscovery();
//
//			try {
//				// Connect the device through the socket. This will block
//				// until it succeeds or throws an exception
//				mmSocket.connect();
//				Toast.makeText(MainActivity.this, "connected", Toast.LENGTH_LONG).show();
//			} catch (IOException connectException) {
//				// Unable to connect; close the socket and get out
//				try {
//					Toast.makeText(MainActivity.this, "connection closed", Toast.LENGTH_LONG).show();
//					mmSocket.close();
//				} catch (IOException closeException) {
//				}
//				return;
//			}
		  
		  connectDev(mmDevice);

			// Do work to manage the connection (in a separate thread)
			// manageConnectedSocket(mmSocket);
		}

		/** Will cancel an in-progress connection, and close the socket */
//		public void cancel() {
//			try {
//				mmSocket.close();
//			} catch (IOException e) {
//			}
//		}
		
		
	}
	public void connectDev(BluetoothDevice device) {
	  Method connect = null;
	  mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.A2DP);
	  try {
      connect = BluetoothA2dp.class.getDeclaredMethod("connect", BluetoothDevice.class);
      
    } catch (NoSuchMethodException e) {
      Toast.makeText(MainActivity.this, "can not create connect method", Toast.LENGTH_LONG).show();
    }
    
    // ... call functions on mBluetoothHeadset
	  
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
    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,mBluetLoudspeaker);
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

}
