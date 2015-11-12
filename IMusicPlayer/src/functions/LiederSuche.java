package functions;

import java.util.ArrayList;
import java.util.HashMap;

public class LiederSuche {
	
	public ArrayList<HashMap<String, String>> getSearchedSongs(ArrayList<HashMap<String, String>> allSongs, String searchText){
		
		ArrayList<HashMap<String, String>> finalList = new ArrayList<HashMap<String, String>>();
		for(HashMap<String, String> singleSong : allSongs) {
			String songName = singleSong.get("songTitle");
			if(songName.contains(searchText)) {
				finalList.add(singleSong);
			}
		}
        return finalList;
    }
}
