package functions;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.imusicplayer.R;

import android.media.MediaPlayer;
import android.os.Environment;
import android.provider.ContactsContract.Directory;
 
public class SongsManager {
    // SDCard Path
    final String MEDIA_PATH = new String("storage/");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
 
    // Constructor
    public SongsManager(){
 
    }
 
    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     * */
    public ArrayList<HashMap<String, String>> getPlayList(){
    	File home = new File(MEDIA_PATH);//Environment.getExternalStorageDirectory();
        System.out.println(home.getPath());
        if(home.list() != null) {
            System.out.println(home.list());
        } else {
            System.out.println("is null");
        }
        System.out.println(home.getUsableSpace());
        System.out.println("TEst1");
        return songsList;
    }
//        if (home.listFiles(new FileExtensionFilter()).length > 0) {
//        	System.out.println("TEst2");
//            for (File file : home.listFiles(new FileExtensionFilter())) {
//            	System.out.println("TEst3");
//                HashMap<String, String> song = new HashMap<String, String>();
//                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
//                song.put("songPath", file.getPath());
// 
//                System.out.println("TEst4");
//                // Adding each song to SongList
//                songsList.add(song);
//            }
//        }
//        // return songs list array
//        return songsList;
//    }
 
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
    public void  playSong(int songIndex, ArrayList<HashMap<String, String>> songsList){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
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