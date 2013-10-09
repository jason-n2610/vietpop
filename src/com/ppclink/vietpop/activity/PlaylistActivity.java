package com.ppclink.vietpop.activity;

import java.io.File;


import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ppclink.vietpop.adv.AdvTask;
import com.ppclink.vietpop.adv.AdvViewGroup;
import com.ppclink.vietpop.data.Constant;
import com.ppclink.vietpop.data.Constant.DownloadState;
import com.ppclink.vietpop.data.FileDownload;
import com.ppclink.vietpop.data.FileDownloadInterface;
import com.ppclink.vietpop.data.PlaylistManager;
import com.ppclink.vietpop.data.Song;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.Translate;
import com.ppclink.vietpop.widget.TranslateADV;

public class PlaylistActivity extends Activity implements FileDownloadInterface {
	

	private 	String 		strListPlaylist[];	// Luu du lieu list cac playlist
	private 	String 		strListDelete[];	// Luu mang cac playlist muon xoa 
	private 	ListView 	lvPlaylist;
	private 	ListView 	lvSongPlaylist;
	private 	ArrayList<Integer> 		alControlView;	// An hien thanh phan cua row
	private 	int 		stateView = 1;	// Luu trang thai cua ListView, 1 la Playlist, 2 la Song
	
	private SongAdapter songAdapter;
	// Cac bien de luu du lieu bai hat cua mot playlist
	static ArrayList<Song> alListSong;

	static int lengthList;
	static String strPlaylistName;
	int value;	// Dung bien dem thay doi khi click xoa item playlist
	int length;	// Do dai cua mang du lieu
	AlertDialog alert;	// Dung de thay doi button delete trong dialog delete playlist
	AlertDialog.Builder builder;
	ArrayList<FileDownload> downloaders;
	private ViewGroup container = null;
	private AdvViewGroup advView;
	
	@Override
	protected void onPause() {
		super.onPause();
		if (stateView == 2){
			if (lengthList != alListSong.size()){
				PlaylistManager.deleteAllSong(strPlaylistName);
				PlaylistManager.addListSong(strPlaylistName, alListSong);
			}	
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (stateView == 1){
			lvPlaylist.setAdapter(new PlaylistAdapter(PlaylistActivity.this, PlaylistManager.getPlaylist()));
		}
		else if (stateView == 2){
			alListSong = PlaylistManager.getSong(strPlaylistName);
			songAdapter = new SongAdapter(PlaylistActivity.this, alListSong);
			songAdapter.notifyDataSetChanged();
			lvSongPlaylist.setAdapter(songAdapter);
			alControlView = new ArrayList<Integer>();
			for (int i=0; i<alListSong.size(); i++){
				alControlView.add(View.GONE);
			}
		}
	}

	// Bat su kien khi an nut Back cua may
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (stateView == 1) {	// Neu trang thai dang o ListView cua Playlist
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				AlertDialog alert;
				
				builder.setMessage("Bạn muốn thoát khỏi chuong trình")
						.setPositiveButton("Có",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										PlaylistActivity.this.finish();
									}
								})
						.setIcon(R.drawable.icon)
						.setNegativeButton("Không",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				alert = builder.create();
				alert.show();
			}
			if (stateView == 2) {	// Neu trang thai dang o ListView cua Song 
            	applyRotation(0, 0, 90);
				stateView = 1;
				applyRotationADV(0, 0, 50);

				if (lengthList != alListSong.size()){
					PlaylistManager.deleteAllSong(strPlaylistName);
					PlaylistManager.addListSong(strPlaylistName, alListSong);
				}
				lvPlaylist.setAdapter(new PlaylistAdapter(PlaylistActivity.this, PlaylistManager.getPlaylist()));
				return true;
			}
		}
		return false;
	}

	// Khoi tao 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (DisplayMode.mode == 1) setContentView(R.layout.playlist_320x480);
		else if (DisplayMode.mode == 2) setContentView(R.layout.playlist_480x800);
		
		lvPlaylist = (ListView) findViewById(R.id.list_playlist);
		lvSongPlaylist = (ListView) findViewById(R.id.list_song_playlist);
        container = (ViewGroup)findViewById(R.id.container);
        container.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
        advView = (AdvViewGroup) findViewById(R.id.advViewGroup1);
		lvPlaylist.setScrollingCacheEnabled(true);
		lvPlaylist.setAlwaysDrawnWithCacheEnabled(true);
				
		try {
			PlaylistManager.addPlaylist("Now Playing");
		}
		catch (Exception e) {
		}
		
		downloaders = new ArrayList<FileDownload>();
		
		// Do du lieu playlist vao listview
		lvPlaylist.setAdapter(new PlaylistAdapter(this, PlaylistManager.getPlaylist()));
	}

	
	public static void updatePlaylist() {
		
	}
	
	
	// Khoi tao Menu khi dang o playlist nao
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (stateView == 1) {	// Khi dang o view playlist, thiet lap menu phu hop
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.playlist_menu1_320x480, menu);
			return true;
		} else if (stateView == 2) {	// Khi dang o view song, thiet lap menu phu hop
			menu.clear();
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.playlist_menu2_320x480, menu);
			return true;
		}
		return false;
	}

	// Khi click vao item trong Menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addPlaylist: // Khi nguoi dung chon menu item addplaylist
			// Tao dialog cho nguoi dung nhap ten playlist
			final EditText etNewPlaylist = new EditText(this);
			builder = new AlertDialog.Builder(this).setTitle("Thêm playlist");
			builder.setView(etNewPlaylist);
			builder.setMessage("Tên playlist:");
			builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							final String strPlaylistName = etNewPlaylist.getText().toString().trim();
							if (strPlaylistName.equals("")){
								Toast.makeText(PlaylistActivity.this, "Tên playlist không được để trống", 1000).show();
							}
							else{
								boolean result = PlaylistManager.addPlaylist(strPlaylistName);
								if (result) { // playlist chua ton tai, tem moi
									lvPlaylist.setAdapter(new PlaylistAdapter( PlaylistActivity.this, PlaylistManager.getPlaylist()));
								} 
								else { // Playlist ton tai, hien dialog thong bao muon ghi de ko
									dialog.cancel();
									new AlertDialog.Builder(PlaylistActivity.this)
									.setMessage("Playlist đã tồn tại, bạn có muốn ghi dè nó ko?")
									.setTitle("Warning!")
									.setPositiveButton("Ok",
											new DialogInterface.OnClickListener() {
	
												public void onClick(DialogInterface dialog, int which) {	// Ghi de playlist
													PlaylistManager.deleteAllSong(strPlaylistName);
													lvPlaylist.setAdapter(new PlaylistAdapter(PlaylistActivity.this,
																	PlaylistManager.getPlaylist()));
												}
											})
									.setNegativeButton("Cancel",
											new DialogInterface.OnClickListener() {
	
												public void onClick(DialogInterface dialog,int which) {
													dialog.cancel();
												}
											})
									.create().show();
								}
							}
						}
					});
			builder.setNegativeButton("Hủy",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			alert = builder.create();
			alert.show();
			break;

		case R.id.deletePlaylist: // Khi nguoi dung chon menu item deleteplaylist
			value = 0;
			length = PlaylistManager.getPlaylist().length;
			strListPlaylist = new String[length];
			strListDelete = new String[length];	// 
			for (int i = 0; i < length; i++) {
				strListPlaylist[i] = PlaylistManager.getPlaylist()[i];
			}	// Do du lieu vao list
			builder = new AlertDialog.Builder(this);
			builder.setTitle("Delete Playlist");
			builder.setMultiChoiceItems(strListPlaylist, null,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {
									if (isChecked) {	// Neu chon playlist, them no vao strListDelete
										strListDelete[whichButton] = strListPlaylist[whichButton];
										value += 1;
										Button button = alert.getButton(DialogInterface.BUTTON_POSITIVE);
										button.setText("Delete (" + value +")");
										button.setEnabled(true);
									} else {
										value -= 1;
										strListDelete[whichButton] = null;
										Button button = alert.getButton(DialogInterface.BUTTON_POSITIVE);
										button.setText("Delete (" + value +")");
										if (value == 0){
											button.setEnabled(false);
										}
									}
								}
							})
			.setPositiveButton("Delete",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									for (int i = 0; i < length; i++) {
										if (strListDelete[i] != null) {
											PlaylistManager.deletePlaylist(strListDelete[i]);
											lvPlaylist.setAdapter(new PlaylistAdapter(PlaylistActivity.this, PlaylistManager.getPlaylist()));
										}
									}
								}
							})
			.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();									
								}
							});
			alert = builder.create();
			alert.show();
			break;
		case R.id.deleteAll:
			alListSong.clear();
			songAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// Tao adapter do du lieu vao list playlist vao listview
	class PlaylistAdapter extends BaseAdapter {
		private String[] playlist;
		private LayoutInflater mInflater;
        private int mIcon1;
        private int mIcon2;
        private int mBackground1;
        private int mBackground2;     
        

		public PlaylistAdapter(Context context, String[] playlist) {
			this.playlist = playlist;
			mInflater = LayoutInflater.from(context);
			mIcon1 = R.drawable.icon_left_320x480;
			mIcon2 = R.drawable.icon_right_access_320x480;
			mBackground1 = R.drawable.list1_320x480;
			mBackground2 = R.drawable.list2_320x480;
		}

		public String getItem(int position) {
			return playlist[position];
		}

		public long getItemId(int position) {
			return position;
		}
		
		class PlaylistHolder {
    		TextView tvPlaylist;
            ImageButton icon1;
            ImageButton icon2;
    	}

		public View getView(int position, View convertView, ViewGroup parent) {
			final String entry = playlist[position];
			PlaylistHolder holder;    		
    		if (convertView == null) {
    
 				if (DisplayMode.mode == 1) convertView = mInflater.inflate(R.layout.row_browser_320x480, null);
 				else if (DisplayMode.mode == 2) convertView = mInflater.inflate(R.layout.row_browser_480x800, null);			 
				holder = new PlaylistHolder();
				holder.tvPlaylist = (TextView) convertView.findViewById(R.id.text_topic);
				holder.icon1 = (ImageButton) convertView.findViewById(R.id.icon1);
				holder.icon2 = (ImageButton)convertView.findViewById(R.id.icon2);
				holder.icon1.setImageResource(mIcon1);
				holder.icon2.setImageResource(mIcon2);  
				convertView.setTag(holder);                
    		}  else {
                holder = (PlaylistHolder) convertView.getTag();
            }    	   		
    		convertView.setBackgroundResource((position & 1) == 1 ? mBackground1 : mBackground2);    		
			if (entry != null) {
				holder.tvPlaylist.setText(entry);
				holder.tvPlaylist.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// Khi nguoi dung click vao 1 playlist
						// Lay bai hat trong list luu vao alListSong dung de lam du lieu do vao listview
						// alListSong se luu nhung thay doi tren list bai hat cua playlist
						// Khi nguoi dung thoat activity se tu dong luu vao file
						alListSong = new ArrayList<Song>();
						alListSong = PlaylistManager.getSong(entry);
						lengthList = PlaylistManager.getSong(entry).size();
						strPlaylistName = entry;
						if (!advView.isPlaying) {
							AdvTask advTask = new AdvTask();
							advTask.startAdv(advView);
						}
		
						alControlView = new ArrayList<Integer>();
						for (int i=0; i<lengthList; i++){
							alControlView.add(View.GONE);
						}
						stateView = 2;
						applyRotationADV(1, 0, 50);
						applyRotation(1, 0, 90);
						songAdapter = new SongAdapter(PlaylistActivity.this, alListSong);	
						lvSongPlaylist.setAdapter(songAdapter);

					}
				});
				holder.icon2.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						stateView = 2;
						alListSong = new ArrayList<Song>();
						alListSong = PlaylistManager.getSong(entry);
						lengthList = PlaylistManager.getSong(entry).size();
						strPlaylistName = entry;
						if (!advView.isPlaying) {
							AdvTask advTask = new AdvTask();
							advTask.startAdv(advView);
						}
						stateView = 2;
						applyRotationADV(1, 0, 50);
						applyRotation(1, 0, 90);
						songAdapter = new SongAdapter(PlaylistActivity.this, alListSong);	
						lvSongPlaylist.setAdapter(songAdapter);
					}
				});
			}
			return convertView;
		}

		public int getCount() {	
			return playlist.length;
		}

	}
	
	public void applyRotationADV(int position, float start, float end) {

		final float centerX = advView.getWidth() / 3.0f;
		final float centerY = advView.getHeight() / 3.0f;
		final TranslateADV rotation = new TranslateADV(start, end, centerX,
				centerY);
		rotation.setDuration(1000);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextViewADV(position));
		advView.startAnimation(rotation);
	}

	private final class DisplayNextViewADV implements
			Animation.AnimationListener {
		private final int mPosition;

		private DisplayNextViewADV(int position) {
			mPosition = position;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			advView.post(new SwapViewsADV(mPosition));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	private final class SwapViewsADV implements Runnable {
		private final int mPosition;

		public SwapViewsADV(int position) {
			mPosition = position;
		}

		public void run() {
			final float centerX = advView.getWidth() / 3.0f;
			final float centerY = advView.getHeight() / 3.0f;
			TranslateADV rotation;
			if (mPosition > 0) {
				advView.setVisibility(View.VISIBLE);
				advView.requestFocus();
				rotation = new TranslateADV(50, 0, centerX, centerY);
			} else {
				advView.setVisibility(View.GONE);
				rotation = new TranslateADV(50, 0, centerX, centerY);
			}
			rotation.setDuration(1000);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			advView.startAnimation(rotation);
		}
	}
	
	private class SongAdapter extends BaseAdapter {
		
		private ArrayList<Song> listSong;
		private LayoutInflater mInflater;
        private int mIcon1;
        private int mIcon2;
        private int mIcon3;
        private int mBackground1;
        private int mBackground2;         
              
		public SongAdapter(Context context, ArrayList<Song> listSong) {
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.listSong = listSong;
			mInflater = LayoutInflater.from(context);
			mIcon1 = R.drawable.icon_left_320x480;
			mIcon3 = R.drawable.icon_right_cancel_320x480;
			mBackground1 = R.drawable.list1_320x480;
			mBackground2 = R.drawable.list2_320x480;
			mIcon2 = R.drawable.icon_right_delete_320x480;
		}

		public int getCount() {
			return listSong.size();
		}

		public Song getItem(int position) {
			return listSong.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {			
			Song entry = listSong.get(position);
			final int currentPosition = position;
			final String strSongName = entry.getName();
			final String urlDownload = entry.getLink();
			final SongHolder holder; 
			if (convertView == null) {				
		
 				if (DisplayMode.mode == 1) convertView = mInflater.inflate(R.layout.row_song_320x480, null);
 				else if (DisplayMode.mode == 2) convertView = mInflater.inflate(R.layout.row_song_480x800, null);  			 
			holder = new SongHolder();
			holder.tvSong = (TextView) convertView.findViewById(R.id.text_song);
			holder.tvArtist = (TextView)convertView.findViewById(R.id.text_artist);
			holder.tvDownloadInfo = (TextView)convertView.findViewById(R.id.tvDownload);
			holder.icon1 = (ImageButton) convertView.findViewById(R.id.icon1);
			holder.ibRemoveItem = (ImageButton)convertView.findViewById(R.id.icon2);
			holder.ibCancelDownload = (ImageButton)convertView.findViewById(R.id.ibCancelDownload);
			holder.pbDownload = (ProgressBar)convertView.findViewById(R.id.pbDownload);
			holder.rowDownload = (TableRow)convertView.findViewById(R.id.row_download);
			holder.icon1.setImageResource(mIcon1);
			holder.ibCancelDownload.setImageResource(mIcon3);
			holder.ibRemoveItem.setImageResource(mIcon2);   
			holder.rowDownload.setVisibility(View.GONE);
			holder.pbDownload.setTag(position);
			convertView.setTag(holder);                  
			}  
			else {   			
				holder = (SongHolder)convertView.getTag();
				holder.pbDownload.setTag(position);
			}    
			convertView.setBackgroundResource((position & 1) == 1 ? mBackground1 : mBackground2);                    		
			if (entry != null) {
				holder.tvArtist.setText(entry.getNameArtist());
				holder.tvSong.setText(entry.getName());
				holder.rowDownload.setVisibility(alControlView.get(position));
				holder.pbDownload.setVisibility(alControlView.get(position));
				holder.ibCancelDownload.setVisibility(alControlView.get(position));
				holder.tvArtist.setVisibility(alControlView.get(position)+8);
				holder.ibRemoveItem.setVisibility(alControlView.get(position)+8);
				// long click vao bai hat, xu ly hoac download hoac delete neu ton tai
				holder.tvSong.setOnLongClickListener(new OnLongClickListener() {
					
					public boolean onLongClick(View v) {
						final File tempFile = new File(Constant.PATH_DOWNLOAD+"/"+alListSong.get(currentPosition).getName()+".mp3");
						if(tempFile.exists())	// Bai hat da duoc tai
						{
							// Xoa bai hat khoi may
							new AlertDialog.Builder(PlaylistActivity.this)
							.setTitle("Delete")
							.setMessage("Bài hát đã được tải, bạn có muốn xóa không?")
							.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									tempFile.delete();
								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							})
							.create().show();
						}
						else {	// Bai hat chua ton tai
							boolean isDownload = false;
							for (int i=0; i<downloaders.size(); i++){
								if (downloaders.get(i).getState() == DownloadState.DOWNLOAD_INPROGRESS){
									isDownload = true;
									break;
								}
							}					
							if (isDownload){
								FileDownload downloader = new FileDownload(strSongName, urlDownload, Constant.PATH_DOWNLOAD);						
								downloader.delegate = PlaylistActivity.this;
								downloader.setTag(currentPosition);
								downloader.init(holder.pbDownload, holder.tvDownloadInfo, holder.ibCancelDownload, holder.ibRemoveItem, holder.tvArtist);
								downloaders.add(downloader);
								alControlView.set(position,View.VISIBLE);
								holder.rowDownload.setVisibility(alControlView.get(position));
								holder.pbDownload.setVisibility(alControlView.get(position));
								holder.ibCancelDownload.setVisibility(alControlView.get(position));
								holder.tvArtist.setVisibility(alControlView.get(position)+8);
								holder.ibRemoveItem.setVisibility(alControlView.get(position)+8);
								holder.tvDownloadInfo.setVisibility(alControlView.get(position));
								holder.tvDownloadInfo.setText("Wait...");
							}
							else{	// Download bai hat
								File dirDownload = new File(Constant.PATH_DOWNLOAD);
								if (!(dirDownload.exists() && dirDownload.isDirectory()) ){
									
									dirDownload.mkdirs();
								}
								FileDownload downloader = new FileDownload(strSongName, urlDownload, Constant.PATH_DOWNLOAD);						
								downloader.init(holder.pbDownload, holder.tvDownloadInfo, holder.ibCancelDownload, holder.ibRemoveItem, holder.tvArtist);
								downloader.delegate = PlaylistActivity.this;
								downloader.setTag(currentPosition);
								downloaders.add(downloader);
								alControlView.set(currentPosition, View.VISIBLE);
								holder.rowDownload.setVisibility(alControlView.get(position));
								holder.pbDownload.setVisibility(alControlView.get(position));
								holder.ibCancelDownload.setVisibility(alControlView.get(position));
								holder.tvArtist.setVisibility(alControlView.get(position)+8);
								holder.ibRemoveItem.setVisibility(alControlView.get(position)+8);
								downloader.start();
							}
						}
						return true;						
					}
				});
				
				// click vao 1 bai hat se nhay sang view nghe nhac, nghe toan bo playlist
				// ghi du lieu vao file
				holder.tvSong.setOnClickListener(new OnClickListener() {	// Khi click vao 1 bai hat

					public void onClick(View v) {
						if (alListSong.size() != lengthList){
	    					Intent intent = new Intent("android.intent.action.MP3Player");
	    					intent.putExtra("getPlaylist", strPlaylistName);							
						}
    					Intent intent = new Intent("android.intent.action.MP3Player");
    					intent.putExtra("getPlaylist", strPlaylistName);
    					intent.putExtra("startPlay", currentPosition);
    					startActivity(intent);
					}
				});
				holder.tvArtist.setOnLongClickListener(new OnLongClickListener() {
					
					public boolean onLongClick(View v) {
						final File tempFile = new File(Constant.PATH_DOWNLOAD+"/"+alListSong.get(currentPosition).getName()+".mp3");
						if(tempFile.exists())	// Bai hat da duoc tai
						{
							// Xoa bai hat khoi may
							new AlertDialog.Builder(PlaylistActivity.this)
							.setTitle("Delete")
							.setMessage("Bài hát đã được tải, bạn có muốn xóa không?")
							.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									tempFile.delete();
								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							})
							.create().show();
						}
						else {	// Bai hat chua ton tai
							boolean isDownload = false;
							for (int i=0; i<downloaders.size(); i++){
								if (downloaders.get(i).getState() == DownloadState.DOWNLOAD_INPROGRESS){
									isDownload = true;
									break;
								}
							}					
							if (isDownload){
								FileDownload downloader = new FileDownload(strSongName, urlDownload, Constant.PATH_DOWNLOAD);						
								downloader.delegate = PlaylistActivity.this;
								downloader.setTag(currentPosition);
								downloader.init(holder.pbDownload, holder.tvDownloadInfo, holder.ibCancelDownload, holder.ibRemoveItem, holder.tvArtist);
								downloaders.add(downloader);
								alControlView.set(currentPosition, View.VISIBLE);
								holder.rowDownload.setVisibility(alControlView.get(position));
								holder.pbDownload.setVisibility(alControlView.get(position));
								holder.ibCancelDownload.setVisibility(alControlView.get(position));
								holder.tvArtist.setVisibility(alControlView.get(position)+8);
								holder.ibRemoveItem.setVisibility(alControlView.get(position)+8);
								holder.tvDownloadInfo.setVisibility(alControlView.get(position));
								holder.tvDownloadInfo.setText("wait...");							
							}
							else{	// Download bai hat
								File dirDownload = new File(Constant.PATH_DOWNLOAD);
								if (!(dirDownload.exists() && dirDownload.isDirectory()) ){
									
									dirDownload.mkdirs();
								}
								FileDownload downloader = new FileDownload(strSongName, urlDownload, Constant.PATH_DOWNLOAD);						
								downloader.init(holder.pbDownload, holder.tvDownloadInfo, holder.ibCancelDownload, holder.ibRemoveItem, holder.tvArtist);
								downloader.delegate = PlaylistActivity.this;
								downloader.setTag(currentPosition);
								downloaders.add(downloader);
								alControlView.set(currentPosition, View.VISIBLE);
								holder.rowDownload.setVisibility(alControlView.get(position));
								holder.pbDownload.setVisibility(alControlView.get(position));
								holder.ibCancelDownload.setVisibility(alControlView.get(position));
								holder.tvArtist.setVisibility(alControlView.get(position)+8);
								holder.ibRemoveItem.setVisibility(alControlView.get(position)+8);
								downloader.start();
							}
						}
						return true;
					}
				});
				
				// click vao 1 bai hat se nhay sang view nghe nhac, nghe toan bo playlist
				// ghi du lieu vao file
				holder.tvArtist.setOnClickListener(new OnClickListener() {	// Khi click vao 1 bai hat

					public void onClick(View v) {
							if (alListSong.size() != lengthList){
		    					Intent intent = new Intent("android.intent.action.MP3Player");
		    					intent.putExtra("getPlaylist", strPlaylistName);							
							}
	    					Intent intent = new Intent("android.intent.action.MP3Player");
	    					intent.putExtra("getPlaylist", strPlaylistName);
	    					intent.putExtra("startPlay", currentPosition);
	    					startActivity(intent);
					}
				});
				
				// xu ly khi dang download nguoi dung muon cancel
				holder.ibCancelDownload.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						int cancel=-1;
						for (int i=0; i<downloaders.size(); i++){
							if (downloaders.get(i).getTag() == currentPosition){
								cancel = i;
								break;
							}
						}
						if (cancel != -1){
							if (downloaders.get(cancel).getState() == DownloadState.DOWNLOAD_INPROGRESS){
								downloaders.get(cancel).cancelDownload(true);
								downloaders.remove(cancel);
							}
							else{
								alControlView.set(currentPosition, View.VISIBLE);
								downloaders.remove(cancel);
								holder.rowDownload.setVisibility(alControlView.get(position)+8);
								holder.pbDownload.setVisibility(alControlView.get(position)+8);
								holder.ibCancelDownload.setVisibility(alControlView.get(position)+8);
								holder.tvArtist.setVisibility(alControlView.get(position));
								holder.ibRemoveItem.setVisibility(alControlView.get(position));
							}
						}
					}
				});
				
				// Xu ly khi click vao button delete item khoi playlist
				holder.ibRemoveItem.setOnClickListener(new OnClickListener() {	// Khi nguoi dung click vao icon delete

					// @Override
					public void onClick(View v) {	
						alListSong.remove(currentPosition);
						alControlView.remove(currentPosition);
						songAdapter.notifyDataSetChanged();
					}
				});
			}
			return convertView;
		}
		class SongHolder {
    		TextView tvSong;
    		TextView tvArtist; 
    		TextView tvDownloadInfo;
            ImageButton icon1;
            ImageButton ibCancelDownload;
            ImageButton ibRemoveItem;
            ProgressBar pbDownload;
            TableRow rowDownload;
          
    	}

	}
	
	// Ham chuyen doi giao dien
    public void applyRotation(int position, float start, float end) {
        final float centerX = container.getWidth() / 2.0f;
        final float centerY = container.getHeight() / 2.0f;
        final Translate rotation =
                new Translate(start, end, centerX, centerY);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));
        container.startAnimation(rotation);
    }
    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;
        
        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            container.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {
        private final int mPosition;

        public SwapViews(int position) {
            mPosition = position;
        }

        public void run() {
            final float centerX = container.getWidth() / 2.0f;
            final float centerY = container.getHeight() / 2.0f;
            Translate rotation;            
            if (mPosition > 0) {
                lvPlaylist.setVisibility(View.GONE);
                lvSongPlaylist.setVisibility(View.VISIBLE);
                lvSongPlaylist.requestFocus();
                rotation = new Translate(90, 0, centerX, centerY);
            } else {
            	lvSongPlaylist.setVisibility(View.GONE);
            	lvPlaylist.setVisibility(View.VISIBLE);
            	lvPlaylist.requestFocus();
                rotation = new Translate(90, 0, centerX, centerY);
            }
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            container.startAnimation(rotation);
        }
    }

	public int onFinishDownload(FileDownload downloader) {
		if (downloader.getState() == DownloadState.DOWNLOAD_CANCELED){
			alControlView.set(downloader.getTag(), View.GONE);
			// xoa khoi danh sach dang download
			for (int i=0; i<downloaders.size(); i++){
				if (downloaders.get(i).getTag() == downloader.getTag()){
					downloaders.remove(i);
					break;
				}
			}
			if (downloaders.size() != 0){
				downloaders.get(0).start();
			}
		}
		else if (downloader.getState() == DownloadState.DOWNLOAD_COMPLETE){
			// xoa khoi danh sach dang download
			for (int i=0; i<downloaders.size(); i++){
				if (downloaders.get(i).getTag() == downloader.getTag()){
					downloaders.remove(i);
					break;
				}
			}
			alControlView.set(downloader.getTag(), View.GONE);
			if (downloaders.size() != 0){
				downloaders.get(0).start();
			}
		}
		return 0;
	}
}
