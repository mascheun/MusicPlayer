package functions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.media.MediaPlayer;
import android.os.Environment;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH = new String("storage/sdcard/Music/testLied");
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	// Constructor
	public SongsManager() {

	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 */
	public ArrayList<HashMap<String, String>> getPlayList() {
		File home = Environment.getExternalStorageDirectory();
		songsList = new ArrayList<HashMap<String, String>>();

		getPaths(home, songsList);

		return songsList;
	}

	/**
	 * Class to filter files which are having .mp3 extension
	 */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}

	private static ArrayList<HashMap<String, String>> getPaths(File file, ArrayList<HashMap<String, String>> list) {
		if (file == null || list == null || !file.isDirectory())
			return null;
		File[] fileArr = file.listFiles();
		for (File f : fileArr) {
			if (f.isDirectory()) {
				getPaths(f, list);
			}
			String name = f.getName();
			if (name.endsWith(".mp3") || name.endsWith(".MP3")) {
				HashMap<String, String> song = new HashMap<String, String>();
				song.put("songTitle", f.getName().substring(0, (f.getName().length() - 4)));
				song.put("songPath", f.getPath());

				// Adding each song to SongList
				list.add(song);
			}
		}
		return list;
	}

	// Mediaplayer
	MediaPlayer mp = new MediaPlayer();

	/**
	 * Function to play a song
	 */
	public void playSong(ArrayList<HashMap<String, String>> songsList, String songname) {
		// Play song
		try {
			mp.reset();
			String songPath = "";
			for (HashMap<String, String> hm : songsList) {
				if (hm.get("songTitle").equals(songname)) {
					songPath = hm.get("songPath");
					break;
				}
			}
			mp.setDataSource(songPath);
			mp.prepare();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startSong() {
		mp.start();
	}
	
	public void stopSong() {
		mp.pause();
	}
	
	public int getDuration() {
		return mp.getDuration();

	}
	
	public int getCurrentPosition() {
		return mp.getCurrentPosition();
	}
	
	public void setSongTimePosition(int songPosition) {
		mp.seekTo(songPosition*1000);
	}
	
}