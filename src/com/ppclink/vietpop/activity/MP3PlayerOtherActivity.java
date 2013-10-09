package com.ppclink.vietpop.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ppclink.vietpop.adv.AdvTask;
import com.ppclink.vietpop.adv.AdvViewGroup;
import com.ppclink.vietpop.data.Constant;
import com.ppclink.vietpop.data.DataInterface;
import com.ppclink.vietpop.lyric.LyricTask;
import com.ppclink.vietpop.lyric.LyricView;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.Rotate;
import com.ppclink.vietpop.widget.TranslateADV;

public class MP3PlayerOtherActivity extends Activity implements DataInterface {
	boolean state = false; // trạng thái của activity
	private ViewGroup frameLyric;
	private MediaPlayer media_player;
	private Context mContext;
	private ViewGroup container, galleryview;
	private ImageButton show_lyric, repeat, shuffle;
	private TextView now_playing, time_play, time_remain, text_artist,
			text_album, text_number;	
	private SeekBar seek_bar;
	private LyricView lyricview;
	private int count = 0;
	private String[] textDisplay = new String[13];
	private Handler handler1 = new Handler();
	private Handler handler2 = new Handler();
	private int textCount;
	private int timeTemp;
	private int indexTemp;
	private ArrayList<String> content;
	private ArrayList<Integer> time;
	private ArrayList<Integer> timeDistance;
	private LyricTask lyricAdapter;
	private LyricTask lyric_Adapter;
	String startPlay;
	// boolean downloadFinish;
	private int currentSong; // Lay ve position cua Song dang play
	int pos; // Dung trong lay picture
	boolean isPicture;
	ArrayList<String> urlSource;
	ArrayList<String> alPictures;
	ImageView search_lyric;
	ArrayList<String> list_Lyric;
	private static final int NO_LYRIC = 0;
	private static final int SHOW_LYRIC = 1;
	private ArrayList<String> listFile;
	private ImageButton play_song;
	private int isShuffle = 0, isRepeat = 0;
	Gallery gallery;
	private AdvViewGroup advView;
	private ImageButton tab_list, tab_previous, tab_play, tab_pause, tab_next;

	
	@Override
	protected void onStop() {
		super.onStop();
		advView.stopAdv();
	}
	
	private void setupUI() {
		tab_list = (ImageButton) findViewById(R.id.list);
		show_lyric = (ImageButton) findViewById(R.id.show_lyric);
		tab_next = (ImageButton) findViewById(R.id.next);
		tab_play = (ImageButton) findViewById(R.id.play);
		tab_previous = (ImageButton) findViewById(R.id.previous);
		tab_pause = (ImageButton) findViewById(R.id.pause);
		tab_pause.setVisibility(View.GONE);
		galleryview = (ViewGroup) findViewById(R.id.gallery_view);
		text_album = (TextView) findViewById(R.id.text_album);
		text_artist = (TextView) findViewById(R.id.text_artist);
		container = (ViewGroup) findViewById(R.id.container);
		now_playing = (TextView) findViewById(R.id.now_playing);
		time_play = (TextView) findViewById(R.id.time_play);
		time_remain = (TextView) findViewById(R.id.time_remain);
		seek_bar = (SeekBar) findViewById(R.id.seekBar1);
		lyricview = (LyricView) findViewById(R.id.lyricview);
		frameLyric = (ViewGroup) findViewById(R.id.frame_lyric);
		frameLyric.setVisibility(View.GONE);
		search_lyric = (ImageView) findViewById(R.id.search_lyric);

		tab_play.setVisibility(View.GONE);
		tab_pause.setVisibility(View.VISIBLE);
		play_song = (ImageButton) findViewById(R.id.play_song);

		repeat = (ImageButton) findViewById(R.id.repeat);
		shuffle = (ImageButton) findViewById(R.id.shuffle);
		setText_MP3Player();
		
		play_song.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				handler1.removeCallbacks(displayLyric);
				applyRotation(0, 0, 90);
				state = !state;
			}
		});

		repeat.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if (isRepeat == 1) {
					isRepeat = 0;
					repeat.setSelected(false);
					Toast.makeText(getApplicationContext(), "Repeat all off", 800).show();
				}
				else {
					isRepeat = 1;
					repeat.setSelected(true);
					Toast.makeText(getApplicationContext(), "Repeat all on", 800).show();
				}
			}
		});

		shuffle.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if (isShuffle == 0) {
					isShuffle = 1;
					shuffle.setSelected(true);
					Toast.makeText(getApplicationContext(), "Shuffle on", 1500).show();
				}
				else {
					isShuffle = 0;
					shuffle.setSelected(false);
					Toast.makeText(getApplicationContext(), "Shuffle off", 1500).show();
				}
			}
		});  

		tab_list.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab_list.setSelected(true);
				tab_next.setSelected(false);
				tab_play.setSelected(false);
				show_lyric.setSelected(false);
				tab_previous.setSelected(false);
				tab_pause.setSelected(false);
				showDialog(SHOW_LIST);

			}
		});

		tab_previous.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab_list.setSelected(false);
				tab_next.setSelected(false);
				show_lyric.setSelected(false);
				tab_previous.setSelected(true);
				tab_pause.setSelected(false);
				tab_pause.setVisibility(View.VISIBLE);
				tab_play.setVisibility(View.GONE);
				if (isShuffle == 0)
					if (currentSong > 0) {
						currentSong--;
						playSong();
					}
					else {
						playSong();
					}
				else {
					Random r = new Random();
					currentSong = r.nextInt(listFile.size());
					playSong();
				}
				gallery.setSelection(currentSong);
			}
		});

		tab_play.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab_list.setSelected(false);
				tab_next.setSelected(false);
				tab_play.setSelected(false);
				tab_play.setVisibility(View.GONE);
				tab_pause.setVisibility(View.VISIBLE);
				show_lyric.setSelected(false);
				tab_previous.setSelected(false);
				media_player.start();
				time_Handler1.removeCallbacks(displayTime1);
				time_Handler1.postDelayed(displayTime1, 0);
				time_Handler2.removeCallbacks(displayTime2);
				time_Handler2.postDelayed(displayTime2, 0);
				gallery.setSelection(currentSong);
			}
		});

		tab_pause.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab_list.setSelected(false);
				tab_next.setSelected(false);				
				tab_pause.setVisibility(View.GONE);
				tab_play.setVisibility(View.VISIBLE);
				show_lyric.setSelected(false);
				tab_previous.setSelected(false);
				tab_pause.setSelected(false);
				if (media_player.isPlaying()) {
					media_player.pause();			
				}				
				time_Handler1.removeCallbacks(displayTime1);
				time_Handler2.removeCallbacks(displayTime2);
			}
		});

		tab_next.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab_list.setSelected(false);
				tab_next.setSelected(true);
				show_lyric.setSelected(false);
				tab_previous.setSelected(false);
				tab_pause.setSelected(false);
				tab_pause.setVisibility(View.VISIBLE);
				tab_play.setVisibility(View.GONE);
				if (isShuffle == 0)
					if (currentSong < listFile.size() - 1) {
						currentSong++;
						playSong();
					}
					else {
						playSong();
					}
				else {
					Random r = new Random();
					currentSong = r.nextInt(listFile.size());
					playSong();
				}
				gallery.setSelection(currentSong);
			}
		});

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

	private void playSong() {			
			try {
				media_player.reset();
				media_player.setDataSource(Constant.PATH_DOWNLOAD
						+ listFile.get(currentSong)+".mp3");
	        	media_player.prepare();	
				media_player.start();	
	        	tab_pause.setVisibility(View.VISIBLE);
	        	tab_play.setVisibility(View.GONE);
	        	setText_MP3Player();  	
				setTime_MP3Player();	
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	private static final int SHOW_LIST = 10;

	private int duration;
	private Handler time_Handler1;
	private Handler time_Handler2;
	private int time1, time2;

	private void setText_MP3Player() {
		now_playing.setText(listFile.get(currentSong));
		text_artist.setText("Nhiều ca sĩ");
		text_album.setText("Các bài hát đã tải");
		text_number.setText(""+(currentSong+1)+"/"+listFile.size());
	}

	private void setTime_MP3Player() {
		duration = media_player.getDuration();
		seek_bar.setMax(media_player.getDuration());
		time1 = 0;
		time2 = duration / 1000;
		time_Handler1.removeCallbacks(displayTime1);
		time_Handler1.postDelayed(displayTime1, 0);
		time_Handler2.removeCallbacks(displayTime2);
		time_Handler2.postDelayed(displayTime2, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (DisplayMode.mode == 1) setContentView(R.layout.mp3_player_320x480);
		else if (DisplayMode.mode == 2) setContentView(R.layout.mp3_player_480x800);
		Bundle extras = getIntent().getExtras();
		startPlay = extras.getString("getPlaylist");
		currentSong = extras.getInt("startPlay");
		listFile = extras.getStringArrayList("listPlay");
		text_number = (TextView)findViewById(R.id.song_number);
		setupUI();
		advView = (AdvViewGroup) findViewById(R.id.advViewGroup1);
		if (!advView.isPlaying) {
			AdvTask advTask = new AdvTask();
			advTask.startAdv(advView);
		}
        advView.setVisibility(View.VISIBLE);
		applyRotationADV(1, 0, 50);
		show_lyric.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tab_list.setSelected(false);
				tab_next.setSelected(false);
				tab_play.setSelected(false);
				show_lyric.setSelected(true);
				tab_previous.setSelected(false);
				tab_pause.setSelected(false);

				if (!state) {
					lyricAdapter = new LyricTask();
					lyricAdapter.delegate = MP3PlayerOtherActivity.this;
					lyricAdapter.playLyric(listFile.get(currentSong));

					applyRotation(1, 0, 90);
					lyricview
							.setText(new String[] { " ", " ", " ", " ", " ",
									"Đang tải... ", " ", " ", " ", " ", " ",
									" ", " " });
				}
			}
		});

		// su kien cho nut search lyric
		search_lyric.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				lyric_Adapter = new LyricTask();
				lyric_Adapter.delegate = MP3PlayerOtherActivity.this;
				lyric_Adapter.searchLyric(listFile.get(currentSong));
			}
		});

		// util = new Utils();
		// urlSource = new ArrayList<String>();
		// alPictures = new ArrayList<String>();

		// for (int i = 0; i < list.size(); i++)
		// urlSource.add(list.get(i).getLink());
		// for (int i=0; i<list.size(); i++){
		// alPictures.add(list.get(i).getLinkPicture());
		// }
		//
		// DataReader dataReader = new DataReader();
		// dataReader = new DataReader();
		// dataReader.delegate = this;
		// dataReader.getPictures(alPictures);
		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(this));
		
		gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// Truyen position cho Lyric
				currentSong = position;
				// gallery.getChildAt(currentSong).setLayoutParams(new
				// Gallery.LayoutParams(150, 150));
				if (state){
					lyricAdapter = new LyricTask();
					lyricAdapter.delegate = MP3PlayerOtherActivity.this;
					lyricAdapter.playLyric(listFile.get(currentSong));	
				}
				playSong();
			}
		});

		media_player = new MediaPlayer();
		time_Handler1 = new Handler();
		time_Handler2 = new Handler();
		playSong();
		gallery.setSelection(currentSong);
		updateSeekbar.start();
		seek_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				media_player.seekTo(seekBar.getProgress());
				time1 = seekBar.getProgress() / 1000;
				time2 = (duration - seekBar.getProgress()) / 1000;
				time_Handler1.removeCallbacks(displayTime1);
				time_Handler1.postDelayed(displayTime1, 0);
				time_Handler2.removeCallbacks(displayTime2);
				time_Handler2.postDelayed(displayTime2, 0);

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});

		media_player
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					public void onCompletion(MediaPlayer mp) {
						try {
							if (currentSong == (listFile.size() - 1)) {
								if (isRepeat == 1)
									currentSong = -1;
								else {
									media_player.reset();
								}
							}
							if (isShuffle == 0)
								if (currentSong < listFile.size() - 1) {
									currentSong++;
									playSong();
								} else {
									playSong();
								}
							else {
								Random r = new Random();
								currentSong = r.nextInt(listFile.size());
								playSong();
							}
							media_player.reset();
							seek_bar.setProgress(0);
							seek_bar.setSecondaryProgress(0);
							media_player
									.setDataSource(Constant.PATH_DOWNLOAD
											+ listFile.get(currentSong)+".mp3");
							lyricAdapter = new LyricTask();
							lyricAdapter.delegate = MP3PlayerOtherActivity.this;
							lyricAdapter.playLyric(listFile.get(currentSong));

							setText_MP3Player();

							media_player.prepare();

							setTime_MP3Player();

							media_player.start();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});
	}

	Thread updateSeekbar = new Thread(new Runnable() {
		public void run() {

			try {
				int current = 0;
				int total = media_player.getDuration();
				seek_bar.setMax(total);
				seek_bar.setIndeterminate(false);
				while (media_player != null && current < total) {
					try {
						Thread.sleep(1000);
						current = media_player.getCurrentPosition();
						seek_bar.setProgress(current);
					} catch (Exception e) {

					}
				}
			} catch (Exception e) {

			}
		}
	});

	private Runnable displayTime1 = new Runnable() {
    	private int minute, second;    	
		public void run() {
			if (time1 <= duration / 1000) {
				second = time1;
				minute = second / 60;
				second = second % 60;
				String showSecond = null;
				String showMinute = null;
				if (second < 10){
					showSecond = "0" + String.valueOf(second);
				}
				else{
					showSecond = String.valueOf(second);
				}
				if (minute < 10) {					
					showMinute = "0" + String.valueOf(minute);
				}
				else{
					showMinute = String.valueOf(minute);
				}
				time_play.setText(showMinute + ":" + showSecond);
				time1++;
				time_Handler1.removeCallbacks(displayTime1);
				time_Handler1.postDelayed(displayTime1, 1000);
			}		
		}
	};

	private Runnable displayTime2 = new Runnable() {
    	private int minute, second;    	
		public void run() {
			if (time2 >= 0) {
				second = time2;
				minute = second / 60;
				second = second % 60;
				String showMinute = String.valueOf(minute);
				String showSecond = String.valueOf(second);
				if (second < 10){
					showSecond = "0"+String.valueOf(second);
				}
				else{
					showSecond = String.valueOf(second);
				}
				if (minute < 10) {
					showMinute = "-0" + String.valueOf(minute);
				} else {
					showMinute = "-" + String.valueOf(minute);
				}
				time_remain.setText(showMinute + ":" + showSecond);
				time2--;				
				time_Handler2.removeCallbacks(displayTime2);
				time_Handler2.postDelayed(displayTime2, 1000);
			}		
		}
	};

	// public void updateGallery(){
	// gallery.setAdapter(new ImageAdapter(this, alPictures));
	// }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (state) {
				handler1.removeCallbacks(displayLyric);
				applyRotation(0, 0, 90);
				state = !state;

			} else {
//				media_player.stop();
				media_player.release();
				// list.clear();
				this.finish();
				return super.onKeyDown(keyCode, event);
			}
		}
		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
    		AudioManager audio = (AudioManager)getSystemService(MP3PlayerOtherActivity.AUDIO_SERVICE);
    		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    		if (currentVolume < maxVolume) {
    			audio.setStreamVolume(AudioManager.STREAM_MUSIC, ++currentVolume, AudioManager.FLAG_PLAY_SOUND);
//    			Toast.makeText(getApplicationContext(), "" + currentVolume + " / " + maxVolume, 50).show();
    		}
    	}
    	if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
    		AudioManager audio = (AudioManager)getSystemService(MP3PlayerOtherActivity.AUDIO_SERVICE);
    		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    		if (currentVolume > 0) {
    			audio.setStreamVolume(AudioManager.STREAM_MUSIC, --currentVolume, AudioManager.FLAG_PLAY_SOUND);
//    			Toast.makeText(getApplicationContext(), "" + currentVolume + " / " + maxVolume, 50).show();
    		}
    	}
		return true;
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;

		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return listFile.size();
		}

		public Object getItem(int position) {
			return listFile.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setLayoutParams(new Gallery.LayoutParams(100, 100));
			i.setBackgroundResource(mGalleryItemBackground);

			i.setImageResource(R.drawable.icon_music_320x480);
			return i;
		}

	}

	// ham thuc hien khi parse xong lyric
	public int onComplete(Integer result) {
		switch (result) {
		case Constant.GET_PICTURE:

			break;
		case Constant.PLAYLYRIC:
			if (lyricAdapter.data != null) {
				content = lyricAdapter.data.getContent();
				time = lyricAdapter.data.getTime();
				timeDistance = lyricAdapter.data.getTimeDistance();
				setTextCount(media_player.getCurrentPosition());
				handler1.removeCallbacks(displayLyric);
				handler1.postDelayed(displayLyric, 0);
				state = !state;
			} else {
				lyricview.setText(new String[] { " ", " ", " ", " ", " ",
						"Không có lyric ", " ", " ", " ", " ", " ", " ", " " });
				state = !state;
			}
			break;
		case Constant.SEARCHLYRIC:
			list_Lyric = LyricTask.listSongLyric;
			if (list_Lyric == null) {
				showDialog(NO_LYRIC);
			} else {
				showDialog(SHOW_LYRIC);
			}
			break;
		case Constant.SELECTLYRIC:
			if (lyric_Adapter.downloadFileSuccess) {
				Toast.makeText(this, "Tải file thành công!", Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(this, "Tải file lỗi!", Toast.LENGTH_LONG).show();
			}
			break;
		}
		return 0;
	}

	// đối tượng thực hiện việc dịch nội dung lyric
	private Runnable shiftLyric = new Runnable() {

		public void run() {
			count += 2;
			lyricview.setCount(count);
			if (count <= 20) {
				handler2.removeCallbacks(shiftLyric);
				handler2.postDelayed(shiftLyric, 100);
			}
		}
	};

	// đối tượng hiển thì frame lyric
	private Runnable displayLyric = new Runnable() {

		public void run() {
			if (textCount != 0) {
				count = 0;
				handler2.removeCallbacks(shiftLyric);
				handler2.postDelayed(shiftLyric, 0);
			}

			switch (textCount) {
			case 0:
				for (int i = 0; i < 6; i++) {
					textDisplay[i] = " ";
				}
				for (int j = 6; j < 13; j++) {
					textDisplay[j] = content.get(j - 6);
				}
				lyricview.setText(textDisplay);
				break;
			case 1:
				for (int i = 0; i < 5; i++) {
					textDisplay[i] = " ";
				}
				for (int j2 = 5; j2 < 13; j2++) {
					textDisplay[j2] = content.get(j2 - 5);
				}
				lyricview.setText(textDisplay);
				break;
			case 2:
				for (int i = 0; i < 4; i++) {
					textDisplay[i] = " ";
				}
				for (int i = 4; i < 13; i++) {
					textDisplay[i] = content.get(i - 4);
				}
				lyricview.setText(textDisplay);
				break;
			case 3:
				textDisplay[0] = textDisplay[1] = textDisplay[2] = " ";
				for (int i = 3; i < 13; i++) {
					textDisplay[i] = content.get(i - 3);
				}
				lyricview.setText(textDisplay);
				break;
			case 4:
				textDisplay[0] = textDisplay[1] = " ";
				for (int i = 2; i < 13; i++) {
					textDisplay[i] = content.get(i - 2);
				}
				lyricview.setText(textDisplay);
				break;
			case 5:
				textDisplay[0] = " ";
				for (int i = 1; i < 13; i++) {
					textDisplay[i] = content.get(i - 1);
				}
				break;
			default:
				int j = 0;
				for (int i = textCount - 6; i < textCount + 7; i++) {
					if (i < content.size())
						textDisplay[j++] = content.get(i);
					else
						textDisplay[j++] = " ";
				}
				lyricview.setText(textDisplay);
				break;
			}
			if (textCount == (indexTemp + 1))
				timeDistance.set(indexTemp, timeTemp);
			textCount++;
			if ((textCount - 1) < timeDistance.size()) {
				handler1.postDelayed(displayLyric,
						timeDistance.get(textCount - 1));
			}

		}
	};

	// xac dinh vi tri dong text dang hien thi
	public void setTextCount(int timePlaying) {
		for (int i = 0; i < time.size() - 1; i++) {
			if ((time.get(i) <= timePlaying) && (time.get(i + 1) > timePlaying)) {
				textCount = i;
				indexTemp = i;
				timeTemp = timeDistance.get(i);
				timeDistance.set(i, time.get(i + 1) - timePlaying);
			}
		}
	}

	// ham thuc hien viec quay
	public void applyRotation(int position, float start, float end) {
		final float centerX = container.getWidth() / 2.0f;
		final float centerY = container.getHeight() / 2.0f;
		final Rotate rotation = new Rotate(start, end, centerX, centerY);
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
			Rotate rotation;
			if (mPosition > 0) {
				galleryview.setVisibility(View.GONE);
				text_album.setVisibility(View.GONE);
				text_artist.setVisibility(View.GONE);
				frameLyric.setVisibility(View.VISIBLE);
				play_song.setVisibility(View.VISIBLE);
				show_lyric.setVisibility(View.GONE);
				text_number.setVisibility(View.GONE);
				rotation = new Rotate(90, 0, centerX, centerY);
				applyRotationADV(0, 0, 0);
				advView.pauseAdv();
				advView.setVisibility(View.GONE);
			} else {
				frameLyric.setVisibility(View.GONE);
				galleryview.setVisibility(View.VISIBLE);
				text_album.setVisibility(View.VISIBLE);
				text_artist.setVisibility(View.VISIBLE);
				show_lyric.setVisibility(View.VISIBLE);
				play_song.setVisibility(View.GONE);
				text_number.setVisibility(View.VISIBLE);
				rotation = new Rotate(90, 0, centerX, centerY);
				applyRotationADV(1, 0, 0);
				
//				AdvTask advTask = new AdvTask();
//				advTask.startAdv(advView);
				advView.setVisibility(View.VISIBLE);
				advView.start();
			}
			rotation.setDuration(300);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());
			container.startAnimation(rotation);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case NO_LYRIC:
			AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
			builder3.setMessage("Không tìm thấy lyric");
			builder3.setPositiveButton("Thoát",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			builder3.create().show();
			break;
		case SHOW_LIST:
			final ArrayList<String> temp1 = new ArrayList<String>();
			for (int i = 0; i < listFile.size(); i++) {
				temp1.add((i + 1) + ". " + listFile.get(i));
			}
			AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
			AlertDialog alert4;
			builder4.setTitle("Chọn bài hát");
			final ArrayAdapter<String> test = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.select_dialog_item, temp1);
			builder4.setSingleChoiceItems(test, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							if (media_player.isPlaying())
								media_player.reset();
							
							try {
								currentSong = which;
								media_player.setDataSource(Constant.PATH_DOWNLOAD + listFile.get(which)+".mp3");
								media_player.prepare();
								media_player.start();
								setText_MP3Player();	
								setTime_MP3Player();	
								gallery.setSelection(currentSong);
							} catch (IllegalStateException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
			alert4 = builder4.create();
			alert4.show();
			break;
		case SHOW_LYRIC:
			final ArrayList<String> temp2 = new ArrayList<String>();
			for (int i = 0; i < list_Lyric.size(); i++) {
				temp2.add(list_Lyric.get(i));
			}
			AlertDialog.Builder builder5 = new AlertDialog.Builder(this);
			AlertDialog alert5;
			builder5.setTitle("Chọn Lyric");
			final ArrayAdapter<String> test2 = new ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.select_dialog_singlechoice, temp2);
			builder5.setSingleChoiceItems(test2, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							lyric_Adapter = new LyricTask();
							lyric_Adapter.delegate = MP3PlayerOtherActivity.this;
							lyric_Adapter.selectLyric(test2.getItem(which));
						}
					});
			alert5 = builder5.create();
			alert5.show();
			break;
		}
		return super.onCreateDialog(id);
	}
}