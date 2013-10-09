package com.ppclink.vietpop.activity;

import com.ppclink.vietpop.widget.DisplayMode;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

public class VietPOPActivity extends TabActivity implements
		TabHost.TabContentFactory {

	private TextView title = null;
	private ImageButton hot_music;
	private ImageButton category;
	private ImageButton radio;
	private ImageButton playlist;
	private ImageButton other;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		Display dp = getWindowManager().getDefaultDisplay();
		int x = dp.getWidth();
		int y = dp.getHeight();
		if ((x == 320 && y == 480) || (x == 240 && y == 320)) {
			DisplayMode.mode = 1;
			setContentView(R.layout.main_320x480);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.custom_window_title_320x480);

		} else if (x == 480 && y == 800) {
			DisplayMode.mode = 2;
			setContentView(R.layout.main_480x800);
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
					R.layout.custom_window_title_480x800);

		} 
		title = (TextView) findViewById(R.id.title);
		setupUI();
		TabHost tabHost = getTabHost();

		tabHost.addTab(tabHost.newTabSpec("hot_music")
				.setIndicator("hot_music")
				.setContent(new Intent(this, HotMusicActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("category").setIndicator("category")
				.setContent(new Intent(this, CategoryActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("radio").setIndicator("radio")
				.setContent(new Intent(this, RadioActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("play_list")
				.setIndicator("play_list")
				.setContent(new Intent(this, PlaylistActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("other").setIndicator("other")
				.setContent(new Intent(this, OtherActivity.class)));
		tabHost.setCurrentTab(0);
		title.setText("Nhạc Hot");
		hot_music.setSelected(true);
	}

	private void setupUI() {
		hot_music = (ImageButton) findViewById(R.id.hot_music);
		category = (ImageButton) findViewById(R.id.category);
		radio = (ImageButton) findViewById(R.id.radio);
		playlist = (ImageButton) findViewById(R.id.playlist);
		other = (ImageButton) findViewById(R.id.other);
		hot_music.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				title.setText("Nhạc Hot");
				hot_music.setSelected(true);
				category.setSelected(false);
				radio.setSelected(false);
				playlist.setSelected(false);
				other.setSelected(false);
				getTabHost().setCurrentTab(0);
			}
		});
		category.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				title.setText("Thể Loại");
				hot_music.setSelected(false);
				category.setSelected(true);
				radio.setSelected(false);
				playlist.setSelected(false);
				other.setSelected(false);
				getTabHost().setCurrentTab(1);
			}
		});
		radio.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				title.setText("Radio");
				hot_music.setSelected(false);
				category.setSelected(false);
				radio.setSelected(true);
				playlist.setSelected(false);
				other.setSelected(false);
				getTabHost().setCurrentTab(2);
			}
		});
		playlist.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				title.setText("Playlist");
				hot_music.setSelected(false);
				category.setSelected(false);
				radio.setSelected(false);
				playlist.setSelected(true);
				other.setSelected(false);
				getTabHost().setCurrentTab(3);
			}
		});
		other.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				title.setText("Khác");
				hot_music.setSelected(false);
				category.setSelected(false);
				radio.setSelected(false);
				playlist.setSelected(false);
				other.setSelected(true);
				getTabHost().setCurrentTab(4);
			}
		});
	}

	public View createTabContent(String tag) {
		LayoutInflater inflater = getLayoutInflater();
		View convertView = null;
		if (tag.equals("hot_music")) {
			convertView = inflater.inflate(R.layout.hot_music_320x480, null,
					false);
		} else if (tag.equals("category")) {
			convertView = inflater.inflate(R.layout.category_320x480, null,
					false);
		} else if (tag.equals("radio")) {
			convertView = inflater.inflate(R.layout.radio_320x480, null, false);
		} else if (tag.equals("play_list")) {
			convertView = inflater.inflate(R.layout.playlist_320x480, null,
					false);
		} else if (tag.equals("other")) {
			convertView = inflater.inflate(R.layout.other_320x480, null, false);
		}
		return convertView;
	}
	// @Override
	// protected void onDestroy() {
	// File dirImage = new
	// File("/data/data/com.ppclink.vietpop.activity/adv/images");
	// if(dirImage.isDirectory()){
	// File[] temp = dirImage.listFiles();
	// for (int i = 0; i < temp.length; i++) {
	// temp[i].delete();
	// }
	// }
	// super.onDestroy();
	// }
}