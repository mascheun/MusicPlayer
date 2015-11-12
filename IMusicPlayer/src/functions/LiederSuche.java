package functions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;
import functions.SongsManager.FileExtensionFilter;

public class LiederSuche {
	
	public ArrayList<HashMap<String, String>> getSearchedSongs(ArrayList<HashMap<String, String>> allSongs){
		
		for()

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

}
