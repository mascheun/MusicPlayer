package functions;

import java.util.ArrayList;
import java.util.Set;

import com.example.imusicplayer.MainActivity;

import android.bluetooth.*;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class BluetoothManager {

  private static final int REQUEST_ENABLE_BT = 1;
  public BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

  public boolean isBluetoothAvailable() {
    boolean available = true;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      available = false;
    }

    return available;
  }

  public void enableBluetooth() {
    if (!mBluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      MainActivity.instance.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
  }

  public ArrayList<String> findDevices() {
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    ArrayList<String> allBluetoothDevices = new ArrayList<String>();
    // If there are paired devices
    if (pairedDevices.size() > 0) {
      // Loop through paired devices
      for (BluetoothDevice device : pairedDevices) {
        // Add the name and address to an array adapter to show in a ListView
        allBluetoothDevices.add(device.getName() + "\n" + device.getAddress());
      } 
      
      return allBluetoothDevices;
      
    }
    
    return null;
  }

}
