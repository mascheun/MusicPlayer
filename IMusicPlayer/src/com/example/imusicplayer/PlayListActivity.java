package com.example.imusicplayer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import database.DatabaseClass;
import functions.Item;
import functions.ListViewWithChkBoxAdapter;

public class PlayListActivity extends Activity {

	private ListView playLists;
	private DatabaseClass db;
	private SongListActivity sla;
	private RelativeLayout homePL;
	private RelativeLayout addPL;
	private LinearLayout deletePL;
	private RelativeLayout editPL;
	private Button addPlOk;
	private Button addPlCancel;
	private Button deletePlOk;
	private Button deletePlCancel;
	private EditText writePlName;
	private ListViewWithChkBoxAdapter lvAdapter;
	private ListView playListsForDeleting;

	private ArrayList<Item> itemList = new ArrayList<Item>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_list);

		db = new DatabaseClass(this);
		db.CreatePlayListDatabase();
		db.addPlayListTable();
		sla = new SongListActivity();

		playLists = (ListView) findViewById(R.id.playlist_drawer);
		homePL = (RelativeLayout) findViewById(R.id.HomePlayListView);
		addPL = (RelativeLayout) findViewById(R.id.AddPlayListView);
		deletePL = (LinearLayout) findViewById(R.id.DeletePlayListView);
		editPL = (RelativeLayout) findViewById(R.id.EditPlayListView);
		addPlOk = (Button) findViewById(R.id.buttonOkAdd);
		addPlCancel = (Button) findViewById(R.id.buttonCancelAdd);
		deletePlOk = (Button) findViewById(R.id.delete_pl);
		deletePlCancel = (Button) findViewById(R.id.cancel_delete);
		writePlName = (EditText) findViewById(R.id.editTextPlName);
		playListsForDeleting = (ListView) findViewById(R.id.delete_pl_view);

		// deletePlOk;
		// deletePlCancel;

		setONClickListener();
		showPlayList();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Gibt den ActionBar-Buttons Funktionen
		switch (item.getItemId()) {
		case R.id.add_playlist:
			editPL.setVisibility(RelativeLayout.INVISIBLE);
			deletePL.setVisibility(RelativeLayout.INVISIBLE);
			homePL.setVisibility(RelativeLayout.INVISIBLE);
			addPL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.delete_playlist:
			showPlayListToDelete();
			editPL.setVisibility(RelativeLayout.INVISIBLE);
			addPL.setVisibility(RelativeLayout.INVISIBLE);
			homePL.setVisibility(RelativeLayout.INVISIBLE);
			deletePL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		case R.id.edit_playlist:
			deletePL.setVisibility(RelativeLayout.INVISIBLE);
			addPL.setVisibility(RelativeLayout.INVISIBLE);
			homePL.setVisibility(RelativeLayout.INVISIBLE);
			editPL.setVisibility(RelativeLayout.VISIBLE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Show all possible PlayLists
	public void showPlayList() {
		playLists.setVisibility(ListView.VISIBLE);

		ArrayList<String> allPlayLists = new ArrayList<String>();

		allPlayLists = db.showPlayLists();

		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allPlayLists);
		playLists.setAdapter(adapter);
	}

	// Show all possible PlayLists for deleting
	public void showPlayListToDelete() {
		playListsForDeleting.setVisibility(ListView.VISIBLE);

		ArrayList<String> allPlayLists = new ArrayList<String>();

		allPlayLists = db.showPlayLists();
		itemList = new ArrayList<Item>();

		for (String plName : allPlayLists) {
			itemList.add(new Item(plName));
		}

		lvAdapter = new ListViewWithChkBoxAdapter(itemList, this);
		playListsForDeleting.setAdapter(lvAdapter);
	}

	// Set on Click Listener from PlayLists
	public void setONClickListener() {
		playLists.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String playlist = (String) parent.getAdapter().getItem(position);
				Intent nextScreen = new Intent(getApplicationContext(), SongListActivity.class);
				Bundle b = new Bundle();
				b.putInt(Constants.MODE, Constants.SONGSFROMPLAYLIST); //Your id
				b.putString(Constants.PLAYLISTKEY, playlist);
				nextScreen.putExtras(b); //Put your id to your next Intent
				startActivity(nextScreen);
				finish();
			}
		});

		addPlOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String plName = "";
				plName = writePlName.getText().toString();
				writePlName.setText("");
				db.addPlayList(plName); // TODO schauen ob PL mit diesem namen
										// schon existiert
				addPL.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
				showPlayList();
			}
		});

		addPlCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				writePlName.setText("");
				addPL.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
			}
		});

		deletePlOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for (Item item : itemList) {
					if (item.isSelected()) {
						db.deletePlayList(item.getName());
					}
				}
				deletePL.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
				showPlayList();
			}
		});

		deletePlCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				deletePL.setVisibility(RelativeLayout.INVISIBLE);
				homePL.setVisibility(RelativeLayout.VISIBLE);
			}
		});

	}

}
