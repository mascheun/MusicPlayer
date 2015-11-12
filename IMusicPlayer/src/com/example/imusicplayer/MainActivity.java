package com.example.imusicplayer;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.imusicplayer.R;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import functions.SongsManager;

public class MainActivity extends Activity {
	
	private Button button1;
	private Button button2;
	private TextView tv1;
	private SongsManager sm = new SongsManager();
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		addListenerOnButton();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	public void addListenerOnButton() {

		button1 = (Button) findViewById(R.id.button1);

		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				songsList = sm.getPlayList();
//				sm.playSong(0, songsList);
//				button2 = (Button) findViewById(R.id.button2);
//				button2.setVisibility(Button.VISIBLE);

			}

		});
		
//		button2 = (Button) findViewById(R.id.button2);
//
//		button2.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				sm.playSong(0, songsList);
//			}
//
//		});
	}
	
}
