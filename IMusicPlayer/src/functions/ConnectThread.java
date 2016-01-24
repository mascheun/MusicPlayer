package functions;

import com.example.imusicplayer.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.widget.Toast;

public class ConnectThread extends Thread {

  private BluetoothAdapter adapter;
  private MainActivity activity;
  private int strength = -9999999;
  private String name = "";

  public ConnectThread(MainActivity activity, BluetoothAdapter adapter) {
    activity.printToast("Assign values");
    this.activity = activity;
    this.adapter = adapter;
  }

  private boolean run = true;

  @Override
  public void run() {
    
    while (run) {
      activity.printToast("Enter While");
      adapter.startDiscovery();
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      for (String[] dev : activity.deviceListStrength) {
        if (Integer.parseInt(dev[1]) > strength) {
          activity.printToast("Got new value for " + name + " and rssi " + strength);
          name = dev[0];
        }
      }
      activity.printToast("Connect to " + name);
      activity.connectDev(activity.deviceList.get(name));
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public boolean isRun() {
    return run;
  }

  public void setRun(boolean run) {
    this.run = run;
  }

  public void doSomething() {
    // mach noch was
  }

}
