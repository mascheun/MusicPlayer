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
    	System.out.println("AddedDatabase: " + playlist );
		playListDatabase.execSQL("INSERT INTO PlayList VALUES('" + playlist + "');");
		playListDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + playlist + " (name VARCHAR);");
	}
	
	public void addSong(String name) {
		songDatabase.execSQL("CREATE TABLE IF NOT EXISTS "+ name + " (song VARCHAR);");
	}
	
	public void addSongToPlayList(String playlist, String songName) {
		songDatabase.execSQL("INSERT INTO " + playlist + " VALUES('" + songName + "');");
	}
	
	public ArrayList<String> showPlayLists() {
		
		ArrayList<String> playLists = new ArrayList<String>();

        String selectQuery = "SELECT * FROM list;";
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
