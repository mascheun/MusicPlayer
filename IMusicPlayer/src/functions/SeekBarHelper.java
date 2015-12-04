package functions;

import android.widget.SeekBar;

public class SeekBarHelper extends Thread {

	private SeekBar songProgressBar;
	private SongsManager sm;
	private Boolean runProgressBar = false;

	public Boolean getRunProgressBar() {
		return runProgressBar;
	}

	public void setRunProgressBar(Boolean runProgressBar) {
		this.runProgressBar = runProgressBar;
	}

	@Override
	public void run() {
		while (true) {
			while (runProgressBar) {
				try {
					int currentDuration = sm.getCurrentPosition();
					songProgressBar.setProgress(currentDuration / 1000);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void setSongManager(SongsManager sm) {
		this.sm = sm;
	}

	public void setSongProgressBar(SeekBar songProgressBar) {
		this.songProgressBar = songProgressBar;
	}
}
