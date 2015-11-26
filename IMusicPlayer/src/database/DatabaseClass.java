package database;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseClass {
	
	Activity activ;
	
	public DatabaseClass(Activity activ) {
		this.activ = activ;
	}
	
	SQLiteDatabase playListDatabase;
	SQLiteDatabase songDatabase;
	
	public void CreatePlayListDatabase() {
		playListDatabase = activ.openOrCreateDatabase("PlayLists", Activity.MODE_PRIVATE, null);
	}
	
	public void CreateSongDatabase(String PlayListName) {
		songDatabase = activ.openOrCreateDatabase(PlayListName, Activity.MODE_PRIVATE, null);
	}
	
	public void addPlayListTable() {
		playListDatabase.execSQL("CREATE TABLE IF NOT EXISTS PlayList (name VARCHAR);");
	}
	
	//Add the PlayList name in the Playlist Table and create a new Table for the playlist
	public void addPlayList(String playlist) {
		playListDatabase.execSQL("INSERT INTO PlayList VALUES('" + playlist + "');");
		playListDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + playlist + " (name VARCHAR);");
	}
	
	//delete the PlayList name in the Playlist Table and delete the Table for the playlist
	public void deletePlayList(String playlist) {
		playListDatabase.execSQL("DELETE FROM PlayList WHERE name = '" + playlist + "';");
		playListDatabase.execSQL("DROP TABLE " + playlist + ";");
	}
	
//	public void addSong(String name) {
//		songDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ name + " (song VARCHAR);");
//	}
	
	public void addSongToPlayList(String playlist, String songName) {
		playListDatabase.execSQL("INSERT INTO " + playlist + " VALUES('" + songName + "');");
	}
	
	public ArrayList<String> showPlayLists() {
		
		ArrayList<String> playLists = new ArrayList<String>();

        String selectQuery = "SELECT * FROM PlayList;";
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
        
        Cursor cursor =  playListDatabase.rawQuery(selectQuery, null);
        if (cursor == null) {
			return songs;
		}
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	songs.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
		
		return songs;
	}

}
