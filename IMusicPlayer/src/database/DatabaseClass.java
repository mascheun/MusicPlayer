package database;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseClass {
	
	SQLiteDatabase newDatabase;
	
	public void CreateDatabase() {
		newDatabase = openOrCreateDatabase("Meine Datenbank", MODE_PRIVATE, null);
		newDatabase.execSQL("CREATE TABLE IF NOT EXISTS Text (texteingabedb VARCHAR);");
	}

}
