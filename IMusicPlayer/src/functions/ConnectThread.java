package functions;

import com.example.imusicplayer.MainActivity;

import android.bluetooth.BluetoothAdapter;

public class ConnectThread extends Thread {
	
	private BluetoothAdapter adapter;
	private MainActivity activity;
	private int strength = -9999999;
	private String name = "";
	
	public ConnectThread(MainActivity activity, BluetoothAdapter adapter) {
		this.activity = activity;
		this.adapter = adapter;
	}
	private boolean run = true;
    @Override
    public void run() {
        while (run) {
            adapter.startDiscovery();
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            for(String[] dev :activity.deviceListStrength) {
            	if(Integer.parseInt(dev[1]) > strength) {
            		name = dev[0];
            	}
            }
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
