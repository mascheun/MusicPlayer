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
    public SongsManager(){
 
    }
 
    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList(){
    	File home = Environment.getExternalStorageDirectory();

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }
        // return songs list array
        return songsList;
    }
 
    /**
     * Class to filter files which are having .mp3 extension
     * */
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
    
	// Mediaplayer
	MediaPlayer mp = new MediaPlayer();
	
	/**
     * Function to play a song
     * @param songIndex - index of song
     * */
    public void  playSong(int songIndex, ArrayList<HashMap<String, String>> songsList, String songname){
        // Play song
        try {
            mp.reset();
            String songPath = "";
            for(HashMap<String, String> hm: songsList) {
            	if(hm.get("songTitle").equals(songname)) {
            		songPath = hm.get("songPath");
            		break;
            	}
            }
            mp.setDataSource(songPath);
            mp.prepare();
            mp.start();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}