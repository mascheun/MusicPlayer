package database;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseClass {
	Activity activ;

	private static DatabaseClass instance;

	private DatabaseClass() {
	}

	public static DatabaseClass getInstance() {
		if (DatabaseClass.instance == null) {
			DatabaseClass.instance = new DatabaseClass();
		}
		return DatabaseClass.instance;
	}
	
	public void setActivity(Activity activ) {
		this.activ = activ;
	}

	SQLiteDatabase currentDatabase;

	public void CreatePlayListDatabase() {
		currentDatabase = activ.openOrCreateDatabase("PlayLists", Activity.MODE_PRIVATE, null);
	}

	public void addPlayListTable() {
		currentDatabase
				.execSQL("CREATE TABLE IF NOT EXISTS PlayList (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR);");
	}

	// Add the PlayList name in the Playlist Table and create a new Table for
	// the playlist
	public void addPlayList(String playlist) {
		currentDatabase.execSQL("INSERT INTO PlayList VALUES(NULL, '" + playlist + "');");
		currentDatabase.execSQL(
				"CREATE TABLE IF NOT EXISTS " + playlist + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR);");
	}

	// delete the PlayList name in the Playlist Table and delete the Table for
	// the playlist
	public void deletePlayList(String playlist) {
		currentDatabase.execSQL("DELETE FROM PlayList WHERE name = '" + playlist + "';");
		currentDatabase.execSQL("DROP TABLE " + playlist + ";");
	}

	public void addSongToPlayList(String playlist, String songName) {
		currentDatabase.execSQL("INSERT INTO " + playlist + " VALUES(NULL, '" + songName + "');");
	}
	
	public void deleteSongFromPlayList(String playlist, String songName) {
		currentDatabase.execSQL("DELETE FROM " + playlist + " WHERE name = '" + songName + "';");
	}

	public ArrayList<String> showPlayLists() {

		ArrayList<String> playLists = new ArrayList<String>();

		String selectQuery = "SELECT * FROM PlayList;";
		Cursor cursor = currentDatabase.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				playLists.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}

		return playLists;
	}

	public ArrayList<String> showSongInPlaylist(String playlist) {

		ArrayList<String> songs = new ArrayList<String>();
		String selectQuery = "SELECT * FROM " + playlist + ";";

		Cursor cursor = currentDatabase.rawQuery(selectQuery, null);
		if (cursor == null) {
			return songs;
		}
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				songs.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}

		return songs;
	}

}
