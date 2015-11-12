package database;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseClass extends Activity {
	
	SQLiteDatabase playListDatabase;
	SQLiteDatabase songDatabase;
	
	public void CreatePlayListDatabase() {
		playListDatabase = openOrCreateDatabase("PlayLists", MODE_PRIVATE, null);
	}
	
	public void CreateSongDatabase(String PlayListName) {
		songDatabase = openOrCreateDatabase(PlayListName, MODE_PRIVATE, null);
	}
	
	public void addPlayList(String name) {
		playListDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ name + " (song VARCHAR);");
	}
	
	public void addSong(String name) {
		songDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ name + " (song VARCHAR);");
	}
	
	public void addSongToPlayList(String playlist, String songName) {
		songDatabase.execSQL("INSERT INTO " + playlist + " VALUES('" + songName + "');");
	}
	
	public ArrayList<String> showPlayLists() {
		
		ArrayList<String> playLists = new ArrayList<String>();

        String selectQuery = "SELECT * FROM PlayLists;";
        Cursor cursor = playListDatabase.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	playLists.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
		
		return playLists;
	}
	
	public ArrayList<String> showSongInPlaylist(String playlist) {
		
		ArrayList<String> songs = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + playlist + ";";
        
        Cursor cursor =  songDatabase.rawQuery(selectQuery, null);
        
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	songs.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
		
		return songs;
	}

}
