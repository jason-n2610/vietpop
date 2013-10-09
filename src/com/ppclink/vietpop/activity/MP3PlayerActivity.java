package com.ppclink.vietpop.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
import com.ppclink.vietpop.data.DataReader;
import com.ppclink.vietpop.data.PlaylistManager;
import com.ppclink.vietpop.data.Song;
import com.ppclink.vietpop.data.Utils;
import com.ppclink.vietpop.lyric.LyricTask;
import com.ppclink.vietpop.lyric.LyricView;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.Rotate;
import com.ppclink.vietpop.widget.TranslateADV;

public class MP3PlayerActivity extends Activity implements DataInterface{
	boolean hasLyric = false;
	boolean state = false;     //trạng thái của activity
	private ViewGroup frameLyric;
	private MediaPlayer media_player;
	private Context mContext;
	private ViewGroup container, galleryview;
	private ImageButton show_lyric, repeat, shuffle;	
	private TextView now_playing, time_play, time_remain, text_artist, text_album;
	private SeekBar seek_bar;
	private LyricView lyricview;
	private int count = 0;
	private String[] textDisplay= new String[13];
	private Handler displayLyricHandler = new Handler();
	private Handler shiftLyricHandler = new Handler();
	private int textCount;
	private int timeTemp;
	private int indexTemp;
	private ArrayList<String> content ;
	private ArrayList<Integer> time;
	private ArrayList<Integer> timeDistance;
	private LyricTask lyricTask;
	private ArrayList<Song> list;
	private ImageAdapter imageAdapter;
	String startPlay;
	int startPos;
	private int currentSong;	// Lay ve position cua Song dang play
	int pos;	// Dung trong lay picture
	boolean isPicture;
	ArrayList<String> urlSource;
	ArrayList<String> alPictures;
	ImageView search_lyric;
	ArrayList<String> list_Lyric;
    private static final int NO_LYRIC = 0;
	private static final int SHOW_LYRIC = 1;
	private int isShuffle = 0, isRepeat = 0;
	private ImageButton play_song;
	private ProgressDialog progressDialog;
	private AdvViewGroup advView;
	private static final int SHOW_LIST = 10;	
	private int duration;
	private Handler time_Handler1;
	private Handler time_Handler2;
	private int time1, time2;
	TextView song_number;
	private Gallery gallery;
	private ImageButton tab_list, tab_previous, tab_play, tab_pause, tab_next;
	private void setupUI() {
		tab_list = (ImageButton)findViewById(R.id.list);
		show_lyric = (ImageButton) findViewById(R.id.show_lyric);
		tab_next = (ImageButton)findViewById(R.id.next);
		tab_play = (ImageButton)findViewById(R.id.play);
		tab_previous = (ImageButton)findViewById(R.id.previous);
		tab_pause = (ImageButton)findViewById(R.id.pause);
		tab_pause.setVisibility(View.GONE);
		galleryview = (ViewGroup) findViewById(R.id.gallery_view);
        text_album = (TextView)findViewById(R.id.text_album);
        text_artist = (TextView)findViewById(R.id.text_artist);
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
        play_song = (ImageButton)findViewById(R.id.play_song);
        repeat = (ImageButton)findViewById(R.id.repeat);
        shuffle = (ImageButton)findViewById(R.id.shuffle);
        song_number = (TextView) findViewById(R.id.song_number);
        advView = (AdvViewGroup) findViewById(R.id.advViewGroup1);
       // advView.setVisibility(View.GONE);
        setText_MP3Player();
        if (!advView.isPlaying) {
			AdvTask advTask = new AdvTask();
			advTask.startAdv(advView);
		}
        //advView.setVisibility(View.VISIBLE);
		//applyRotationADV(1, 0, 50);
        
        repeat.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if (isRepeat == 1) {
					isRepeat = 0;
					repeat.setSelected(false);
					Toast.makeText(MP3PlayerActivity.this, "Repeat all off", 800).show();
				}
				else {
					isRepeat = 1;
					repeat.setSelected(true);
					Toast.makeText(MP3PlayerActivity.this, "Repeat all on", 800).show();
				}
			}
		});
        
        shuffle.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				if (isShuffle == 0) {
					isShuffle = 1;
					shuffle.setSelected(true);
					Toast.makeText(MP3PlayerActivity.this, "Shuffle on", 1500).show();
				}
				else {
					isShuffle = 0;
					shuffle.setSelected(false);
					Toast.makeText(MP3PlayerActivity.this, "Shuffle off", 1500).show();
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
				media_player.reset();
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
					currentSong = r.nextInt(list.size());
					playSong();
				}
				
				//chien code : 
				if (state) {
					lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
					displayLyricHandler.removeCallbacks(displayLyric);
					shiftLyricHandler.removeCallbacks(shiftLyric);
					lyricTask = new LyricTask();
					lyricTask.delegate = MP3PlayerActivity.this;
					lyricTask.playLyric(list.get(currentSong).getName());
					
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
				//chien code
				if(state&&hasLyric){  // neu dang o man hinh hien thi lyric
					//hien thi lai lyric
					setTextCount(media_player.getCurrentPosition());
					displayLyricHandler.postDelayed(displayLyric, 0);
				}

				gallery.setSelection(currentSong);
			}
		});
		
		tab_pause.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(state){ //neu dang o man hinh hien thi lyric
					shiftLyricHandler.removeCallbacks(shiftLyric);
					displayLyricHandler.removeCallbacks(displayLyric);
				}
				tab_list.setSelected(false);
				tab_next.setSelected(false);				
				tab_pause.setVisibility(View.GONE);
				tab_play.setVisibility(View.VISIBLE);
				show_lyric.setSelected(false);
				tab_previous.setSelected(false);
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
				media_player.reset();	
				if (isShuffle == 0)
					if (currentSong < list.size() - 1) {
						currentSong++;
						playSong();
					}
					else {
						playSong();
					}
				else {
					Random r = new Random();
					currentSong = r.nextInt(list.size());
					playSong();
				}
				//chien code : 
				if (state) {
					lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
					displayLyricHandler.removeCallbacks(displayLyric);
					shiftLyricHandler.removeCallbacks(shiftLyric);
					lyricTask = new LyricTask();
					lyricTask.delegate = MP3PlayerActivity.this;
					lyricTask.playLyric(list.get(currentSong).getName());
					
				}

				gallery.setSelection(currentSong);
			}
		});
		
		play_song.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				displayLyricHandler.removeCallbacks(displayLyric);
				applyRotation(0, 0, 90);
				state = !state;
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		advView.stopAdv();
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
        	media_player.setDataSource(Utils.convertUrl(urlSource.get(currentSong)));
        	tab_pause.setVisibility(View.VISIBLE);
        	tab_play.setVisibility(View.GONE);
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
	
	private void setText_MP3Player() {
		now_playing.setText(list.get(currentSong).getName());
		text_artist.setText(list.get(currentSong).getNameArtist());
		song_number.setText(""+(currentSong+1)+"/"+list.size());
	}	
	
	private void setTime_MP3Player() {
		duration = media_player.getDuration() ;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (DisplayMode.mode == 1) setContentView(R.layout.mp3_player_320x480);
        else if (DisplayMode.mode == 2) {
        	setContentView(R.layout.mp3_player_480x800);
        	
        }
		Bundle extras = getIntent().getExtras();
		startPlay = extras.getString("getPlaylist");
		startPos = extras.getInt("startPlay");	
                     
        list = new ArrayList<Song>();
        list = PlaylistManager.getSong(startPlay);
        
        setupUI();
        currentSong = startPos;
        
        show_lyric.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				
				tab_list.setSelected(false);
				tab_next.setSelected(false);
				tab_play.setSelected(false);
				tab_previous.setSelected(false);
				tab_pause.setSelected(false);
				
				lyricTask = new LyricTask();
				lyricTask.delegate = MP3PlayerActivity.this;
				lyricTask.playLyric(list.get(currentSong).getName());
					
				applyRotation(1, 0, 90);
				lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
				
				state = !state;
			}
		});	
        
        //set su kien cho nut back lai man hinh choi nhac
        play_song.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				displayLyricHandler.removeCallbacks(displayLyric);
				applyRotation(0, 0, 90);
				state = !state;
			}
		});
        
      //su kien cho nut search lyric
        search_lyric.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(media_player.isPlaying()){
					media_player.stop();
					media_player.release();
					displayLyricHandler.removeCallbacks(displayLyric);
					shiftLyricHandler.removeCallbacks(shiftLyric);
					time_Handler1.removeCallbacks(displayTime1);
					time_Handler2.removeCallbacks(displayTime2);
				}
				search_lyric.setPressed(true);
				progressDialog = new ProgressDialog(MP3PlayerActivity.this);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("Đang tìm lyric...");
				progressDialog.show();   
				lyricTask = new LyricTask();
				lyricTask.delegate = MP3PlayerActivity.this;
				lyricTask.searchLyric(list.get(currentSong).getName());
			}
		});
           
        urlSource = new ArrayList<String>();
        alPictures = new ArrayList<String>();
 
        for (int i = 0; i < list.size(); i++) urlSource.add(list.get(i).getLink());
        for (int i=0; i<list.size(); i++){
        	alPictures.add(list.get(i).getLinkPicture());
        }
        media_player = new MediaPlayer();
        time_Handler1 = new Handler();
        time_Handler2 = new Handler();
        updateSeekbar.start();
        DataReader dataReader = new DataReader();
        dataReader = new DataReader();
        dataReader.delegate = this;
        dataReader.getPictures(alPictures);	
        
        gallery = (Gallery)findViewById(R.id.gallery);
        imageAdapter = new ImageAdapter(this);
        
        gallery.setAdapter(imageAdapter);   
        gallery.setSelection(startPos);
        gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
	        	// Truyen position cho Lyric
	        	currentSong = position ;
//				gallery.getChildAt(position).setLayoutParams(new Gallery.LayoutParams(150, 150));
		        try {		        	
		        	playSong();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				}
			}        	
		});   
        
        seek_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				//chien code: tam dung hien thi lyric
				displayLyricHandler.removeCallbacks(displayLyric);
				shiftLyricHandler.removeCallbacks(shiftLyric);
				
				media_player.seekTo(seekBar.getProgress());
				time1 = seekBar.getProgress() / 1000;
		        time2 = (duration - seekBar.getProgress()) / 1000;
				time_Handler1.removeCallbacks(displayTime1);
				time_Handler1.postDelayed(displayTime1, 0);
				time_Handler2.removeCallbacks(displayTime2);
				time_Handler2.postDelayed(displayTime2, 0);
				
				//chien code 
				if(state&&hasLyric){  // neu dang o man hinh hien thi lyric
					//hien thi lai lyric
					setTextCount(media_player.getCurrentPosition());
					displayLyricHandler.postDelayed(displayLyric, 0);
				}
			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
		});
        
        
        
        media_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			public void onCompletion(MediaPlayer mp) {
				try {        	
		        	if (currentSong == (urlSource.size()-1)){
		        		if (isRepeat == 1) currentSong = -1;
		        		else {
							media_player.reset();
						}
		        	}
		        	
		        	if (isShuffle == 0)
						if (currentSong < list.size() - 1) {
							currentSong++;
							playSong();
						}
						else {
							playSong();
						}
					else {
						Random r = new Random();
						currentSong = r.nextInt(list.size());
						playSong();
					}
		        	media_player.reset();
		        	seek_bar.setProgress(0);
		        	seek_bar.setSecondaryProgress(0);
		        	media_player.setDataSource(Utils.convertUrl(urlSource.get(currentSong)));
		        	
		        	//chien code: hien thi lyric luc chuyen sang bai khac
		        	if (state) {
		        		lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
						displayLyricHandler.removeCallbacks(displayLyric);
						shiftLyricHandler.removeCallbacks(shiftLyric);
						lyricTask = new LyricTask();
						lyricTask.delegate = MP3PlayerActivity.this;
						lyricTask.playLyric(list.get(currentSong).getName());
					}
		        	

		        	setText_MP3Player();
		        	
					media_player.prepare();
					
					setTime_MP3Player();
					
					media_player.start();
					gallery.setSelection(currentSong);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});        
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }
    
  
    Thread updateSeekbar = new Thread(new Runnable() {		
		public void run() {
			try {
				playSong();
				int current = 0;
				int total = media_player.getDuration();
				seek_bar.setMax(total);
				seek_bar.setIndeterminate(false);
				while (media_player !=null && current < total){
					 try {                                
						 Thread.sleep(1000);         
						 current = media_player.getCurrentPosition();  
						 seek_bar.setProgress(current);  
					 }
					 catch (Exception e) {

					}
				}
			}
			catch (Exception e) {
			
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
    
    

	public void updateGallery(){
		imageAdapter.notifyDataSetChanged();
//		gallery.setAdapter(new ImageAdapter(this, alPictures));
//		gallery.setSelection(currentSong);
	}
        
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {   
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if (state) {
    			displayLyricHandler.removeCallbacks(displayLyric);
				applyRotation(0, 0, 90);
				state = !state;
			}
    		else{
    			media_player.stop();
				media_player.release();
				list.clear();
				this.finish();
        		return super.onKeyDown(keyCode, event);
    		}
    	}
    	
    	if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
    		AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    		if (currentVolume < maxVolume) {
    			audio.setStreamVolume(AudioManager.STREAM_MUSIC, ++currentVolume, AudioManager.FLAG_PLAY_SOUND);
//    			Toast.makeText(getApplicationContext(), "" + currentVolume + " / " + maxVolume, 50).show();
    		}
    	}
    	if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
    		AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
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
            return alPictures.size();
        }

        public Object getItem(int position) {
            return alPictures.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			String namePicture = alPictures.get(position);

			if (!namePicture.equals("default")){
				int startName = namePicture.lastIndexOf("/");
				String path = Constant.PATH_CACHE_PICTURE+namePicture.substring(startName+1, namePicture.length());
				File file = new File(path);
				if (file.exists()) {
						i.setImageDrawable(Drawable.createFromPath(path));
				}
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				if (DisplayMode.mode == 1) i.setLayoutParams(new Gallery.LayoutParams(100, 100));
				else if (DisplayMode.mode == 2) i.setLayoutParams(new Gallery.LayoutParams(200, 200));
				i.setBackgroundResource(mGalleryItemBackground);
			} 
			else {				
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				i.setLayoutParams(new Gallery.LayoutParams(100, 100));
				i.setBackgroundResource(mGalleryItemBackground);
				i.setImageDrawable(getResources().getDrawable(R.drawable.icon_music_320x480));
			}
			return i;
		}
        
    }

    //ham thuc hien khi parse xong lyric
   	public int onComplete(Integer result) {
   		switch(result){
   		case Constant.GET_PICTURE:

			break;
   		case Constant.PLAYLYRIC: 
   			if(lyricTask.data!=null){
   				hasLyric = true;
   				content = lyricTask.data.getContent();
   				time = lyricTask.data.getTime();
   				timeDistance = lyricTask.data.getTimeDistance();
   				setTextCount(media_player.getCurrentPosition());
   				displayLyricHandler.removeCallbacks(displayLyric);
   				displayLyricHandler.postDelayed(displayLyric, 0);
   				state = true;
   			}
   			else{
   				hasLyric = false;
   				lyricview.setText(new String[]{" "," "," "," "," ","Không có lyric "," "," "," "," "," "," "," "});
   				state = true;
   			}
   			break;
   		case Constant.SEARCHLYRIC:
   			progressDialog.cancel();
   			list_Lyric = LyricTask.listSongLyric;
			if (list_Lyric == null) {
				if (state) {
					restartMusic();
				}
				showDialog(NO_LYRIC);
			}
			else {
				showDialog(SHOW_LYRIC);					
			}
   			break;
   		case Constant.SELECTLYRIC:
   			progressDialog.cancel();
   			if(lyricTask.downloadFileSuccess){
   				Toast.makeText(this, "Tải lyric thành công!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(this, "Tải lyric lỗi!", Toast.LENGTH_LONG).show();
			}
   			
   			//chien code : neu dang o man hinh hien thi lyric se choi luon lyric
   			if(state){
   				restartMusic();
   				lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
				displayLyricHandler.removeCallbacks(displayLyric);
				shiftLyricHandler.removeCallbacks(shiftLyric);
				lyricTask = new LyricTask();
				lyricTask.delegate = MP3PlayerActivity.this;
				lyricTask.playLyric(list.get(currentSong).getName());
   			}
   			break;
   		}
   		return 0;
   	}	
	
	//đối tượng thực hiện việc dịch nội dung lyric
		private Runnable shiftLyric = new Runnable() {

			public void run() {
				count += 2;
				lyricview.setCount(count);
				if (count <= 20) {
					shiftLyricHandler.removeCallbacks(shiftLyric);
					shiftLyricHandler.postDelayed(shiftLyric, 100);
				}
			}
		};
		
		//đối tượng hiển thì frame lyric
		private Runnable displayLyric = new Runnable() {
			
			public void run() {
				if (textCount != 0) {
					count = 0;
					shiftLyricHandler.removeCallbacks(shiftLyric);
					shiftLyricHandler.postDelayed(shiftLyric, 0);
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
					for (int j2 = 5; j2 <13 ; j2++) {
						textDisplay[j2] = content.get(j2-5);
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
						textDisplay[i] = content.get(i-2);
					}
					lyricview.setText(textDisplay);
					break;
				case 5:
					textDisplay[0] = " ";
					for (int i = 1; i < 13; i++) {
						textDisplay[i] = content.get(i-1);
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
					displayLyricHandler.postDelayed(displayLyric,
							timeDistance.get(textCount - 1));
				}

			}
		};
		
		//xac dinh vi tri dong text dang hien thi
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
		
		//ham thuc hien viec quay
		public void applyRotation(int position, float start, float end) {
	        final float centerX = container.getWidth() / 2.0f;
	        final float centerY = container.getHeight() / 2.0f;
	        final Rotate rotation =
	                new Rotate(start, end, centerX, centerY);
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
	            if (mPosition > 0) {//sang man hinh hien thi lyric
					galleryview.setVisibility(View.GONE);
					text_album.setVisibility(View.GONE);
					text_artist.setVisibility(View.GONE);
					frameLyric.setVisibility(View.VISIBLE);
					song_number.setVisibility(View.GONE);
					play_song.setVisibility(View.VISIBLE);
					show_lyric.setVisibility(View.GONE);
					advView.setVisibility(View.GONE);
					rotation = new Rotate(90, 0, centerX, centerY);
//					applyRotationADV(0, 0, 0);
//					advView.pauseAdv();
//					advView.setVisibility(View.GONE);
	            } else {
	            	frameLyric.setVisibility(View.GONE);
					galleryview.setVisibility(View.VISIBLE);
					text_album.setVisibility(View.VISIBLE);
					text_artist.setVisibility(View.VISIBLE);
					song_number.setVisibility(View.VISIBLE);
					show_lyric.setVisibility(View.VISIBLE);
					play_song.setVisibility(View.GONE);
					advView.setVisibility(View.VISIBLE);
					rotation = new Rotate(90, 0, centerX, centerY);
//					applyRotationADV(1, 0, 0);
//					
//					AdvTask advTask = new AdvTask();
//					advTask.startAdv(advView);
//					advView.setVisibility(View.VISIBLE);
//					advView.start();
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
				AlertDialog alert3;
	     		 builder3.setMessage("Không tìm thấy lyric");
	     		 builder3.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
	     			   public void onClick(DialogInterface dialog, int id) {
	     				   dialog.cancel();
	  				   }
	     		   });
	     		 
	     		  alert3 = builder3.create();
	     		  alert3.setCancelable(false);
	     		  alert3.show();
	     		  break;
			case SHOW_LIST:
	     		final ArrayList<String> temp1 = new ArrayList<String>();
	     		for (int i = 0; i < list.size(); i++) {
	     			temp1.add((i + 1) + ". " + list.get(i).getName());
	     		}  	
				AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
				AlertDialog alert4;
	   		 builder4.setTitle("Chọn bài hát");
	   		 final ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, temp1);
	   		 builder4.setAdapter(test, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					if (media_player.isPlaying()) media_player.reset();
					try {			        	
			        	currentSong = which;
						media_player.setDataSource(Utils.convertUrl(urlSource.get(which)));
			        	setText_MP3Player();
			        	media_player.prepare();
						setTime_MP3Player();
						media_player.start();
						gallery.setSelection(currentSong);
						
						//chien code : 
						if (state) {
							lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
							displayLyricHandler.removeCallbacks(displayLyric);
							shiftLyricHandler.removeCallbacks(shiftLyric);
							lyricTask = new LyricTask();
							lyricTask.delegate = MP3PlayerActivity.this;
							lyricTask.playLyric(list.get(currentSong).getName());
							
						}
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
				AlertDialog alertShowLyric;
				builder5.setTitle("Chọn Lyric");
	    		final ArrayAdapter<String> test2 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, temp2);
	    		builder5.setSingleChoiceItems(test2, -1, new DialogInterface.OnClickListener() {				
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						 progressDialog = new ProgressDialog(MP3PlayerActivity.this);
						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setMessage("Đang tải lyric...");
						progressDialog.show();   
						lyricTask = new LyricTask();
						lyricTask.delegate = MP3PlayerActivity.this;
						lyricTask.selectLyric(test2.getItem(which));
					}
				});
	    		 alertShowLyric = builder5.create();
	    		 alertShowLyric.show();
	    		 alertShowLyric.setCancelable(false);
	    		break;
			}
			return super.onCreateDialog(id);
		}
	    //chien code : phuong thuc khoi dong viec choi lai nhac
	    public void restartMusic(){
	    	media_player = new MediaPlayer();
				playSong();
				Thread updateSeekBar = new Thread(new Runnable() {
				
				public void run() {
					try {
						int current = 0;
						int total = media_player.getDuration();
						seek_bar.setMax(total);
						seek_bar.setIndeterminate(false);
						while (media_player !=null && current < total){
							 try {                                
								 Thread.sleep(1000);         
								 current = media_player.getCurrentPosition();  
								 seek_bar.setProgress(current);  
							 }
							 catch (Exception e) {
								 e.printStackTrace();
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
				updateSeekBar.start();
				seek_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onStopTrackingTouch(SeekBar seekBar) {
						//chien code: tam dung hien thi lyric
						displayLyricHandler.removeCallbacks(displayLyric);
						shiftLyricHandler.removeCallbacks(shiftLyric);
						
						media_player.seekTo(seekBar.getProgress());
						time1 = seekBar.getProgress() / 1000;
				        time2 = (duration - seekBar.getProgress()) / 1000;
						time_Handler1.removeCallbacks(displayTime1);
						time_Handler1.postDelayed(displayTime1, 0);
						time_Handler2.removeCallbacks(displayTime2);
						time_Handler2.postDelayed(displayTime2, 0);
						
						//chien code 
						if(state&&hasLyric){  // neu dang o man hinh hien thi lyric
							//hien thi lai lyric
							setTextCount(media_player.getCurrentPosition());
							displayLyricHandler.postDelayed(displayLyric, 0);
						}
					}

					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						
					}

					public void onStartTrackingTouch(SeekBar seekBar) {
						
					}
					
				});
				
				
				media_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					
					public void onCompletion(MediaPlayer mp) {
						try {        	
				        	if (currentSong == (urlSource.size()-1)){
				        		if (isRepeat == 1) currentSong = -1;
				        		else {
									media_player.reset();
								}
				        	}
				        	
				        	if (isShuffle == 0)
								if (currentSong < list.size() - 1) {
									currentSong++;
									playSong();
								}
								else {
									playSong();
								}
							else {
								Random r = new Random();
								currentSong = r.nextInt(list.size());
								playSong();
							}
				        	media_player.reset();
				        	seek_bar.setProgress(0);
				        	seek_bar.setSecondaryProgress(0);
				        	media_player.setDataSource(Utils.convertUrl(urlSource.get(currentSong)));
				        	
				        	//chien code: hien thi lyric luc chuyen sang bai khac
				        	if (state) {
				        		lyricview.setText(new String[]{" "," "," "," "," ","Đang nạp... "," "," "," "," "," "," "," "});
								displayLyricHandler.removeCallbacks(displayLyric);
								shiftLyricHandler.removeCallbacks(shiftLyric);
								lyricTask = new LyricTask();
								lyricTask.delegate = MP3PlayerActivity.this;
								lyricTask.playLyric(list.get(currentSong).getName());
							}
				        	

				        	setText_MP3Player();
				        	
							media_player.prepare();
							
							setTime_MP3Player();
							
							media_player.start();
							gallery.setSelection(currentSong);
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				});
		}
}