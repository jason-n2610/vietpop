package com.ppclink.vietpop.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.ppclink.vietpop.adv.AdvTask;
import com.ppclink.vietpop.adv.AdvViewGroup;
import com.ppclink.vietpop.data.BaseData;
import com.ppclink.vietpop.data.Constant;
import com.ppclink.vietpop.data.Constant.DownloadState;
import com.ppclink.vietpop.data.DataInterface;
import com.ppclink.vietpop.data.DataReader;
import com.ppclink.vietpop.data.FileDownload;
import com.ppclink.vietpop.data.FileDownloadInterface;
import com.ppclink.vietpop.data.PlaylistManager;
import com.ppclink.vietpop.data.Song;
import com.ppclink.vietpop.lyric.LyricTask;

import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.Translate;
import com.ppclink.vietpop.widget.TranslateADV;

public class CategoryActivity extends Activity implements DataInterface,
		FileDownloadInterface {
	private ListView list_category;
	private ListView list_song;
	private ViewGroup container;
	private Dialog dialog = null;
	private static final int EXIT = 0;
	private static final int ERROR = 1;
	private static final int SONG_OPTION = 2;
	private CharSequence temp;
	private int state = 0;
	private DataReader dataReader = null;
	private int pageNumber = 1;
	ArrayList<FileDownload> downloaders;
	private ProgressDialog progressDialog;
	private AdvViewGroup advView;
	private static final int CREATE_PLAYLIST_ONE_SONG = 8;
	private static final int CREATE_PLAYLIST_ALL_SONG = 11;
	private static final int ADD_ALL_SONG = 10;
	private static final int ADD_ONE_SONG = 5;
	private Song songAdd;
	private ArrayList<Song> song;
	private LyricTask lyric_Task;
	public ArrayList<String> list_Lyric = null;
	private static final int NO_LYRIC = 3;
	private static final int SHOW_LYRIC = 4;
	Handler advHandler = new Handler();
	private ArrayList<Integer> alControlView; // An hien cac thanh phan cua row
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);       
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		if (DisplayMode.mode == 1)
			setContentView(R.layout.category_320x480);
		else if (DisplayMode.mode == 2)
			setContentView(R.layout.category_480x800);

		list_category = (ListView) findViewById(R.id.list_category);
		list_song = (ListView) findViewById(R.id.list_song);
		list_song.setVisibility(View.GONE);
		list_category.setVisibility(View.VISIBLE);

		container = (ViewGroup) findViewById(R.id.container);
		callDataTask(Constant.SONG_CATEGORY, null);
		progressDialog = new ProgressDialog(CategoryActivity.this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Đang tải thể loại ...");
		progressDialog.show();
		downloaders = new ArrayList<FileDownload>();


		System.out.println("finished");
		advView = (AdvViewGroup) findViewById(R.id.advViewGroup1);

	}
	


	@Override
	protected void onResume() {
		super.onResume();
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

	@Override
	public Dialog onCreateDialog(int id) {
		switch (id) {
case ADD_ALL_SONG:
			
			final ArrayList<String> temp3 = new ArrayList<String>();
      		for (int i = 0; i < PlaylistManager.getPlaylist().length; i++) {
      			temp3.add(PlaylistManager.getPlaylist()[i]);
      		}  	
      		temp3.add("Tạo playlist mới");
			AlertDialog.Builder builder10 = new AlertDialog.Builder(this);
			AlertDialog alert10;
 
    		 builder10.setTitle("Chọn playlist");
    		 final ArrayAdapter<String> test3 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, temp3);
    		 builder10.setSingleChoiceItems(test3, -1, new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if (which < temp3.size() - 1){
						String strPlaylistName = temp3.get(which);
						ArrayList<Song> listSongAdd = new ArrayList<Song>();
    					for (int i=0; i<song.size(); i++){	// Do du lieu vao listSongAdd la cac bai hat muon them
    						listSongAdd.add(song.get(i));
    					}
						ArrayList<Song> listSong = PlaylistManager.getSong(strPlaylistName);
						ArrayList<Integer> listDelete = new ArrayList<Integer>();	// chua cac chi so cua list muon xoa
						for (int i=0; i<listSong.size(); i++){
							for (int j=0; j<listSongAdd.size(); j++){
								if (listSong.get(i).getLink().equals(listSongAdd.get(j).getLink())){
									listDelete.add(j);	// neu trung ten thi remove    									
								}
							}
						}
						for (int i=(listDelete.size()-1); i>-1; i--){
							int value = listDelete.get(i);
							listSongAdd.remove(value);
						}    						
						PlaylistManager.addListSong(strPlaylistName, listSongAdd);
						if (listDelete.size() != 0){
							Toast.makeText(getApplicationContext(), "Thêm thành công. \nCó " + listDelete.size()+" bài đã tồn tại trong playlist.", 2000).show();
	    				}
						else{
							Toast.makeText(getApplicationContext(), "Thêm thành công.", 1500).show();
						}
						dialog.cancel();
					}
					if (which == temp3.size() - 1) {
						dialog.cancel();
						showDialog(CREATE_PLAYLIST_ALL_SONG);
					}
				}
			});
    		 alert10 = builder10.create();
    		 alert10.show();
      		      		 
      		 
      		 break;
      		 
      		 

      	case CREATE_PLAYLIST_ALL_SONG:           
      		AlertDialog.Builder alert11 = new AlertDialog.Builder(this);
    		final EditText input11 = new EditText(this);
    		alert11.setView(input11);
    		alert11.setTitle("Nhập tên playlist");
    		alert11.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) { 
    				String strPlaylistName = input11.getText().toString();
    				if(strPlaylistName.equals("")){
    					Toast.makeText(getApplicationContext(), "Tên playlist không được để trống", 1000).show();
    				}
    				else{
    					ArrayList<Song> listSongAdd = new ArrayList<Song>();
    					for (int i=0; i<song.size(); i++){
    						listSongAdd.add(song.get(i));
    					}
    					boolean addPlay = PlaylistManager.addPlaylist(strPlaylistName);
    					if (addPlay){	// playlist chua ton tai, them moi
    						PlaylistManager.addListSong(strPlaylistName, listSongAdd);
        					Toast.makeText(getApplicationContext(), "Thêm thành công", 1500).show();
        				}
    					else{	// playlist da ton tai, kiem tra cac bai hat trung ten
    						ArrayList<Song> listSong = PlaylistManager.getSong(strPlaylistName);
    						ArrayList<Integer> listDelete = new ArrayList<Integer>();
    						for (int i=0; i<listSong.size(); i++){
    							for (int j=0; j<listSongAdd.size(); j++){
    								if (listSong.get(i).getLink().equals(listSongAdd.get(j).getLink())){
    									listDelete.add(j);	// neu trung ten thi remove    									
    								}
    							}
    						}
    						for (int i=(listDelete.size()-1); i>-1; i--){
    							int value = listDelete.get(i);
    							listSongAdd.remove(value);
    						}    						
    						PlaylistManager.addListSong(strPlaylistName, listSongAdd);
    						if (listDelete.size() != 0){
    							Toast.makeText(getApplicationContext(), "Thêm thành công. \nCó" + listDelete.size()+" bài đã tồn tại trong playlist", 1500).show();
    	    				}
    						else{
    							Toast.makeText(getApplicationContext(), "Thêm thành công.", 1500).show();
    						}
        				}
        			}
    			}    			
    		});
    		alert11.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
    		alert11.show();
      		 break;	 

		case SONG_OPTION:
			final CharSequence[] items = { 
					"Thêm vào danh sách", "Thêm tất cả vào danh sách",
					"Tìm Lyric bài này" };
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			AlertDialog alert2;
			builder2.setTitle(temp);
			builder2.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch (item) {
					case 0:
						dialog.cancel();
						showDialog(ADD_ONE_SONG);

						break;
					case 1:

						dialog.cancel();
						showDialog(ADD_ALL_SONG);
						break;
					case 2:
						// chien
						progressDialog = new ProgressDialog(
								CategoryActivity.this);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("Đang tìm lyric...");
						progressDialog.show();
						lyric_Task = new LyricTask();
						lyric_Task.delegate = CategoryActivity.this;
						lyric_Task.searchLyric(temp.toString());
						dialog.cancel();
						break;
					default:
						break;
					}
				}
			});
			alert2 = builder2.create();
			alert2.show();
			break;

		case NO_LYRIC:
			AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
			AlertDialog alert3;
			builder3.setMessage("Không tìm thấy lyric")

			.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			alert3 = builder3.create();
			alert3.show();
			break;
		case SHOW_LYRIC:
			final ArrayList<String> temp1 = new ArrayList<String>();
			for (int i = 0; i < list_Lyric.size(); i++) {
				temp1.add(list_Lyric.get(i));
			}
			AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
			AlertDialog alert4;

			builder4.setTitle("Chọn lyric");
			final ArrayAdapter<String> test = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.select_dialog_item, temp1);
			builder4.setSingleChoiceItems(test, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							progressDialog = new ProgressDialog(
									CategoryActivity.this);
							progressDialog
									.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							progressDialog.setMessage("Đang tải lyric...");
							progressDialog.show();
							lyric_Task = new LyricTask();
							lyric_Task.delegate = CategoryActivity.this;
							lyric_Task.selectLyric(test.getItem(which));
							dialog.cancel();
						}
					});
			alert4 = builder4.create();
			alert4.show();
			break;
		case ADD_ONE_SONG:
			
			final ArrayList<String> temp2 = new ArrayList<String>();
      		for (int i = 0; i < PlaylistManager.getPlaylist().length; i++) {
      			temp2.add(PlaylistManager.getPlaylist()[i]);
      		}  	
      		temp2.add("Tạo playlist mới");
			AlertDialog.Builder builder8 = new AlertDialog.Builder(this);
			AlertDialog alert8;
    		 builder8.setTitle("Chọn playlist");
    		 final ArrayAdapter<String> test2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, temp2);
    		 builder8.setSingleChoiceItems(test2, -1, new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if (which < temp2.size() - 1)
						try {
							boolean testAddSong = false;
							ArrayList<Song> listSong = PlaylistManager.getSong(temp2.get(which));
							for (int i=0; i<listSong.size(); i++){
								if (listSong.get(i).getLink().equals(songAdd.getLink())){
									testAddSong = true;
									break;
								}
							}
							if (testAddSong){
								Toast.makeText(getApplicationContext(), "Bài hát đã tồn tại trong playlist!", 2000).show();
							}
							else{
								PlaylistManager.addOneSong(temp2.get(which), songAdd);
								Toast.makeText(getApplicationContext(), "Thêm bài hát thành công", 2000).show();
							}							
						}
						catch (Exception e) {
							Toast.makeText(getApplicationContext(), "Thêm bài hát thất bại", 2000).show();
						}	
					if (which == temp2.size() - 1) {
						dialog.cancel();
						showDialog(CREATE_PLAYLIST_ONE_SONG);
					}
					dialog.cancel();
				}
			});
    		 alert8 = builder8.create();
    		 alert8.show();
			break;

		case CREATE_PLAYLIST_ONE_SONG:           
      		AlertDialog.Builder alert9 = new AlertDialog.Builder(this);
    		final EditText input = new EditText(this);
    		alert9.setView(input);
    		alert9.setTitle("Nhập tên playlist");
    		alert9.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int whichButton) {  
    				String strPlaylistName = input.getText().toString();
    				if (strPlaylistName.equals("")){
    					Toast.makeText(getApplicationContext(), "Tên playlist không được trống", 1000).show();
    					dialog.cancel();
    				}
    				else{
    					boolean check = PlaylistManager.addPlaylist(input.getText().toString());
        				if (check){	// playlist chua ton tai
            				PlaylistManager.addOneSong(strPlaylistName, songAdd);
        					Toast.makeText(getApplicationContext(), "Thêm bài hát thành công", 1000).show();    					
        				}
        				else{	// playlist da ton tai
        					boolean addOk = false;
        					ArrayList<Song> listSong = PlaylistManager.getSong(strPlaylistName);
        					for(int i=0; i<listSong.size(); i++){
        						if (listSong.get(i).getLink().equals(songAdd.getLink())){
        							addOk = true;
        							break;
        						}
        					}
        					if (addOk){	// bai hat da co trong playlist
        						Toast.makeText(getApplicationContext(), "Bài hát đã tồn tại trong playlist", 1000).show();
        					}
        					else{	// bai hat chua co trong playlist
        						PlaylistManager.addOneSong(strPlaylistName, songAdd);
        						Toast.makeText(getApplicationContext(), "Thêm bài hát thành công", 1000).show();
        					}
        				}
    				}
    			}
    		});
    		alert9.show();
      		 break;
    	
      	 
		case EXIT:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			AlertDialog alert;
			builder.setMessage("Bạn muốn thoát khỏi chương trình")
					.setPositiveButton("Có",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									CategoryActivity.this.finish();
								}
							})

					.setNegativeButton("Không",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			alert = builder.create();
			alert.show();
			break;
		case ERROR:
			AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
			AlertDialog alert1;
			builder1.setMessage("Lỗi đường truyền")

			.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
					CategoryActivity.this.finish();
				}
			});
			alert1 = builder1.create();
			alert1.show();

			break;

		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (state == 1) {
				applyRotation(0, 0, 90);
				applyRotationADV(0, 0, 50);
				state = 0;
				return true;
			}
			if (state == 0) {
				showDialog(EXIT);
			}
		}
		return false;
	}

	class CategoryAdapter extends BaseAdapter {
		private List<BaseData> listCategory;
		private LayoutInflater mInflater;
		private int mIcon1;
		private int mIcon2;
		private int mBackground1;
		private int mBackground2;

		public CategoryAdapter(Context context, List<BaseData> listCategory) {
			this.listCategory = listCategory;
			mInflater = LayoutInflater.from(context);
			mIcon1 = R.drawable.icon_left_320x480;
			mIcon2 = R.drawable.icon_right_access_320x480;
			mBackground1 = R.drawable.list1_320x480;
			mBackground2 = R.drawable.list2_320x480;
		}

		public int getCount() {
			return listCategory.size();
		}

		public Object getItem(int position) {
			return listCategory.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		class CategoryHolder {
			TextView textTopic;
			ImageButton icon1;
			ImageButton icon2;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final BaseData entry = listCategory.get(position);
			CategoryHolder holder;
			if (convertView == null) {

				Display dp = getWindowManager().getDefaultDisplay();
				int x = dp.getWidth();
				int y = dp.getHeight();
				if (x == 320 && y == 480)
					convertView = mInflater.inflate(
							R.layout.row_browser_320x480, null);
				else if (x == 480 && y == 800)
					convertView = mInflater.inflate(
							R.layout.row_browser_480x800, null);

				holder = new CategoryHolder();
				holder.textTopic = (TextView) convertView
						.findViewById(R.id.text_topic);
				holder.icon1 = (ImageButton) convertView
						.findViewById(R.id.icon1);
				holder.icon2 = (ImageButton) convertView
						.findViewById(R.id.icon2);
				holder.icon1.setImageResource(mIcon1);
				holder.icon2.setImageResource(mIcon2);
				convertView.setTag(holder);
			} else {
				holder = (CategoryHolder) convertView.getTag();
			}
			convertView
					.setBackgroundResource((position & 1) == 1 ? mBackground1
							: mBackground2);

			holder.textTopic.setText(entry.getName());
			holder.textTopic.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					if (state == 0) {
						callDataTask(Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY,
								entry.getLink());
						progressDialog = new ProgressDialog(
								CategoryActivity.this);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("Đang tải bài hát...");
						progressDialog.show();
						if (!advView.isPlaying) {
							AdvTask advTask = new AdvTask();
							advTask.startAdv(advView);
						}
						state = 1;
					}
				}
			});
			holder.icon2.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (state == 0) {
						callDataTask(Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY,
								entry.getLink());
						progressDialog = new ProgressDialog(
								CategoryActivity.this);
						progressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("Đang tải bài hát...");
						progressDialog.show();
						state = 1;
					}
				}
			});

			return convertView;
		}
	}

	class SongAdapter extends BaseAdapter {
		private List<Song> listSong;
		private LayoutInflater mInflater;
		private int mIcon1;
		private int mIcon2;
		private int mIcon3;
		private int mBackground1;
		private int mBackground2;

		class SongHolder {
			TextView textSong;
			TextView textArtist;
			TextView tvDownloadInfo;
			ImageButton icon1;
			ImageButton ibCancel;
			ImageButton ibDownload;
			ProgressBar pbDownload;
			TableRow rowDownload;
		}

		public SongAdapter(Context context, List<Song> listSong) {
			this.listSong = listSong;
			mInflater = LayoutInflater.from(context);
			mIcon1 = R.drawable.icon_left_320x480;
			mIcon2 = R.drawable.icon_right_download_320x480;
			mIcon3 = R.drawable.icon_right_cancel_320x480;
			mBackground1 = R.drawable.list1_320x480;
			mBackground2 = R.drawable.list2_320x480;
		}

		public int getCount() {
			return listSong.size();
		}

		public Object getItem(int position) {
			return listSong.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Song entry = listSong.get(position);
			final SongHolder holder;
			if (convertView == null) {

				if (DisplayMode.mode == 1)
					convertView = mInflater.inflate(R.layout.row_song_320x480,
							null);
				else if (DisplayMode.mode == 2)
					convertView = mInflater.inflate(R.layout.row_song_480x800,
							null);
				holder = new SongHolder();
				holder.textSong = (TextView) convertView
						.findViewById(R.id.text_song);
				holder.textArtist = (TextView) convertView
						.findViewById(R.id.text_artist);
				holder.tvDownloadInfo = (TextView) convertView
						.findViewById(R.id.tvDownload);
				holder.icon1 = (ImageButton) convertView
						.findViewById(R.id.icon1);
				holder.ibDownload = (ImageButton) convertView
						.findViewById(R.id.icon2);
				holder.ibCancel = (ImageButton) convertView
						.findViewById(R.id.ibCancelDownload);
				holder.pbDownload = (ProgressBar) convertView
						.findViewById(R.id.pbDownload);
				holder.rowDownload = (TableRow) convertView
						.findViewById(R.id.row_download);
				holder.icon1.setImageResource(mIcon1);
				holder.ibCancel.setImageResource(mIcon3);
				holder.ibDownload.setImageResource(mIcon2);
				holder.rowDownload.setVisibility(View.GONE);
				convertView.setTag(holder);
			} else {
				holder = (SongHolder) convertView.getTag();
			}
			convertView
					.setBackgroundResource((position & 1) == 1 ? mBackground1
							: mBackground2);
			holder.textSong.setText(entry.getName());
			holder.rowDownload.setVisibility(alControlView.get(position));
			holder.pbDownload.setVisibility(alControlView.get(position));
			holder.ibCancel.setVisibility(alControlView.get(position));
			holder.textArtist.setVisibility(alControlView.get(position) + 8);
			holder.ibDownload.setVisibility(alControlView.get(position) + 8);
			holder.textSong.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					PlaylistManager.updateNowplaying(song);
					Intent intent = new Intent(
							"android.intent.action.MP3Player");
					intent.putExtra("getPlaylist", "Now Playing");
					intent.putExtra("startPlay", position);
					startActivity(intent);
				}
			});

			holder.textSong.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					temp = entry.getName();
					songAdd = entry;
					showDialog(SONG_OPTION);
					return true;
				}
			});

			holder.textArtist.setText(entry.getNameArtist());
			holder.textArtist.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					PlaylistManager.updateNowplaying(song);
					Intent intent = new Intent(
							"android.intent.action.MP3Player");
					intent.putExtra("getPlaylist", "Now Playing");
					intent.putExtra("startPlay", position);
					startActivity(intent);
				}
			});

			holder.textArtist.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					temp = entry.getName();
					songAdd = entry;
					showDialog(SONG_OPTION);
					return true;
				}
			});

			holder.ibCancel.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					int cancel = -1;
					for (int i = 0; i < downloaders.size(); i++) {
						if (downloaders.get(i).getTag() == position) {
							cancel = i;
							break;
						}
					}
					if (cancel != -1) {
						if (downloaders.get(cancel).getState() == DownloadState.DOWNLOAD_INPROGRESS) {
							downloaders.get(cancel).cancelDownload(true);
							downloaders.remove(cancel);
						} else {
							alControlView.set(position, View.VISIBLE);
							downloaders.remove(cancel);
							holder.rowDownload.setVisibility(alControlView
									.get(position) + 8);
							holder.pbDownload.setVisibility(alControlView
									.get(position) + 8);
							holder.ibCancel.setVisibility(alControlView
									.get(position) + 8);
							holder.textArtist.setVisibility(alControlView
									.get(position));
							holder.ibDownload.setVisibility(alControlView
									.get(position));
						}
						holder.ibDownload.setImageResource(mIcon2);
					}
				}
			});

			list_song.setOnScrollListener(new OnScrollListener() {
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {

				}

				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {

				}
			});

			holder.ibDownload.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final File fileDownload = new File(Constant.PATH_DOWNLOAD,
							entry.getName() + ".mp3");
					if (fileDownload.exists()) {
						// Xoa bai hat khoi may
						new AlertDialog.Builder(CategoryActivity.this)
								.setTitle("Delete")
								.setMessage(
										"Bài hát đã được tải, bạn có muốn xóa không?")
								.setPositiveButton("Delete",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												fileDownload.delete();
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
											}
										}).create().show();
					} else {
						boolean isDownload = false;
						for (int i = 0; i < downloaders.size(); i++) {
							if (downloaders.get(i).getState() == DownloadState.DOWNLOAD_INPROGRESS) {
								isDownload = true;
								break;
							}
						}
						if (isDownload) {
							FileDownload downloader = new FileDownload(entry
									.getName(), entry.getLink(),
									Constant.PATH_DOWNLOAD);
							downloader.delegate = CategoryActivity.this;
							downloader.setTag(position);
							downloader.init(holder.pbDownload,
									holder.tvDownloadInfo, holder.ibCancel,
									holder.ibDownload, holder.textArtist);
							downloaders.add(downloader);
							alControlView.set(position, View.VISIBLE);
							holder.rowDownload.setVisibility(alControlView
									.get(position));
							holder.pbDownload.setVisibility(alControlView
									.get(position));
							holder.ibCancel.setVisibility(alControlView
									.get(position));
							holder.textArtist.setVisibility(alControlView
									.get(position) + 8);
							holder.ibDownload.setVisibility(alControlView
									.get(position) + 8);
							holder.tvDownloadInfo.setVisibility(alControlView
									.get(position));
							holder.tvDownloadInfo.setText("Wait...");
						} else { // Download bai hat
							File dirDownload = new File(Constant.PATH_DOWNLOAD);
							if (!(dirDownload.exists() && dirDownload
									.isDirectory())) {

								dirDownload.mkdirs();
							}
							FileDownload downloader = new FileDownload(entry
									.getName(), entry.getLink(),
									Constant.PATH_DOWNLOAD);
							downloader.init(holder.pbDownload,
									holder.tvDownloadInfo, holder.ibCancel,
									holder.ibDownload, holder.textArtist);
							downloader.delegate = CategoryActivity.this;
							downloader.setTag(position);
							downloaders.add(downloader);
							alControlView.set(position, View.VISIBLE);
							holder.rowDownload.setVisibility(alControlView
									.get(position));
							holder.pbDownload.setVisibility(alControlView
									.get(position));
							holder.ibCancel.setVisibility(alControlView
									.get(position));
							holder.textArtist.setVisibility(alControlView
									.get(position) + 8);
							holder.ibDownload.setVisibility(alControlView
									.get(position) + 8);
							downloader.start();
						}
					}
				}

			});

			return convertView;
		}
	}
	
	

	public void applyRotation(int position, float start, float end) {
		final float centerX = container.getWidth() / 3.0f;
		final float centerY = container.getHeight() / 3.0f;
		final Translate rotation = new Translate(start, end, centerX, centerY);
		rotation.setDuration(300);
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
			final float centerX = container.getWidth() / 3.0f;
			final float centerY = container.getHeight() / 3.0f;
			Translate rotation;
			if (mPosition > 0) {
				list_category.setVisibility(View.GONE);
				list_song.setVisibility(View.VISIBLE);
				list_song.requestFocus();
				rotation = new Translate(90, 0, centerX, centerY);
			} else {
				list_song.setVisibility(View.GONE);
				list_category.setVisibility(View.VISIBLE);
				list_category.requestFocus();
				rotation = new Translate(90, 0, centerX, centerY);
			}
			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			container.startAnimation(rotation);
		}
	}

	public void callDataTask(int id, String url) {
		dataReader = new DataReader();
		dataReader.delegate = this;
		if (id == Constant.SONG_CATEGORY) {
			dataReader.getAllSongCategory();
		} else if (id == Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY) {
			dataReader.getFistSongAndPageByCategory(url);
		} else if (id == Constant.SONG_BY_SONG_CATEGORY) {
			dataReader.getAllSongBySongCategory(url, pageNumber);
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	

	public int onComplete(Integer result) {
		progressDialog.cancel();
		if (result == Constant.SONG_CATEGORY) {
			List<BaseData> temp = null;

			temp = dataReader.data;
			if (temp == null) {
				showDialog(ERROR);
			} else {
				list_category.setAdapter(new CategoryAdapter(this, temp));
				return 0;
			}
		}
		if (result == Constant.SONG_BY_SONG_CATEGORY) {
			List<BaseData> temp = null;

			temp = dataReader.data;
			if (temp == null) {
				showDialog(ERROR);
			} else {
				song = new ArrayList<Song>();
				for (int i = 0; i < temp.size(); i++) {
					song.add((Song) temp.get(i));
				}
				alControlView = new ArrayList<Integer>();
				for (int i = 0; i < temp.size(); i++) {
					alControlView.add(View.GONE);
				}
				list_song.setAdapter(new SongAdapter(this, song));

			}
		}
		if (result == Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY) {
			List<BaseData> temp = null;
			temp = dataReader.data;
			if (temp == null) {
				showDialog(ERROR);
			} else {
				song = new ArrayList<Song>();
				for (int i = 0; i < temp.size(); i++) {
					song.add((Song) temp.get(i));
				}

				alControlView = new ArrayList<Integer>();
				for (int i = 0; i < temp.size(); i++) {
					alControlView.add(View.GONE);
				}
				list_song.setAdapter(new SongAdapter(this, song));

				applyRotation(1, 0, 90);

				advView.setVisibility(View.VISIBLE);
				applyRotationADV(1, 0, 50);

				return 0;
			}
		}

		if (result == Constant.SEARCHLYRIC) {
			list_Lyric = LyricTask.listSongLyric;
			if (list_Lyric == null)
				showDialog(NO_LYRIC);
			else {
				showDialog(SHOW_LYRIC);
			}
		}
		if (result == Constant.SELECTLYRIC) {
			if (lyric_Task.downloadFileSuccess) {
				Toast.makeText(this, "Tải lyric thành công!", Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, "Tải lyric không thành công!",
						Toast.LENGTH_LONG).show();
			}
		}

		return 0;
	}

	public int onFinishDownload(FileDownload downloader) {
		if (downloader.getState() == DownloadState.DOWNLOAD_CANCELED) {
			alControlView.set(downloader.getTag(), View.GONE);
			// xoa khoi danh sach dang download
			for (int i = 0; i < downloaders.size(); i++) {
				if (downloaders.get(i).getTag() == downloader.getTag()) {
					downloaders.remove(i);
					break;
				}
			}
			if (downloaders.size() != 0) {
				downloaders.get(0).start();
			}
		} else if (downloader.getState() == DownloadState.DOWNLOAD_COMPLETE) {
			// xoa khoi danh sach dang download
			for (int i = 0; i < downloaders.size(); i++) {
				if (downloaders.get(i).getTag() == downloader.getTag()) {
					downloaders.remove(i);
					break;
				}
			}
			alControlView.set(downloader.getTag(), View.GONE);
			if (downloaders.size() != 0) {
				downloaders.get(0).start();
			}
		}
		return 0;
	}

	@Override
	protected void onStop() {
		
		super.onStop();
		advView.stopAdv();
	}
}
