package com.ppclink.vietpop.activity;

import java.io.IOException;


import java.io.InputStream;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppclink.vietpop.adv.AdvTask;
import com.ppclink.vietpop.adv.AdvViewGroup;
import com.ppclink.vietpop.radio.Channel;
import com.ppclink.vietpop.radio.RadioConstant;
import com.ppclink.vietpop.radio.RadioEngine;
import com.ppclink.vietpop.radio.RadioInterface;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.TranslateADV;

public class RadioPlayerActivity extends Activity implements RadioInterface {
	private Context mContext;
	private ImageButton list_song, previous, play, next, show_lyric, pause;	
	private TextView  time_play, time_remain, text_artist, now_playing;
	private ArrayList<Channel> list;
	private RadioEngine radioEngine;
	private AssetManager asset;
	private MediaPlayer mediaPlayer;
	private int pos;
	private AdvViewGroup advView;
	private ProgressDialog progressDialog;
	
	
	/*
	 * Reset lai MediaPlayer
	 * */
	public void resetMediaPlayer() {
		if(mediaPlayer != null) {
			mediaPlayer.reset();
			radioEngine.stopMediaPlayer();
			mediaPlayer.release();
			mediaPlayer = null;	
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (DisplayMode.mode == 1) setContentView(R.layout.radio_player_320x480);
		else if (DisplayMode.mode == 2) setContentView(R.layout.radio_player_480x800);
		
		/*
		 * Thuc hien play kenh da click tu RadioActivity
		 * */
		progressDialog = ProgressDialog.show(this, "", "Đang kết nối server ...", true);
		advView = (AdvViewGroup) findViewById(R.id.advViewGroup1);
		if (!advView.isPlaying) {
			AdvTask advTask = new AdvTask();
			advTask.startAdv(advView);
		}
        advView.setVisibility(View.VISIBLE);
		applyRotationADV(1, 0, 50);
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("position");         
        
        radioEngine = new RadioEngine(this);
        radioEngine.delegate = this;
        
        asset = getAssets();
        String[] files;       
        
        try {
			files = asset.list("file");
			InputStream in = asset.open("file/" + files[0]);
			list = radioEngine.getChannels(in);			        	        
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
		}
        
        radioEngine.play(list, pos);        
         
        setupUI();
        play.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        
      final Gallery gallery = (Gallery)findViewById(R.id.gallery);
       
        gallery.setAdapter(new ImageAdapter(this));  
        
        text_artist.setText(list.get(pos).getDescription());
        gallery.setSelection(pos);
        gallery.setFocusable(true);
        
        
        /*
         * Khi doi kenh o giao dien RadioPlayerActivity
         * */
        gallery.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {	
				if (pos == position) return;
				if(((radioEngine.checkOnOff(list, position)) == RadioConstant.HTTP_OFFLINE) || (radioEngine.checkOnOff(list, pos) == RadioConstant.RTMP_OFFLINE)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(RadioPlayerActivity.this);
					builder.setTitle("Thông báo");
					builder.setMessage("Kênh này hiện offline");
					builder.show();
				}
				else {
					progressDialog.cancel();
					progressDialog = ProgressDialog.show(RadioPlayerActivity.this, "", "Đang kết nối server ...");
					pos = position;
					resetMediaPlayer();
					play.setVisibility(View.GONE);
					pause.setVisibility(View.VISIBLE);
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					radioEngine.play(list, pos);
					now_playing.setText(list.get(pos).getName());
				}							
			}        	
		});        
    }
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		
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
	
	public void setupUI() {	
		now_playing = (TextView)findViewById(R.id.now_playing);
		now_playing.setText(list.get(pos).getName());
		list_song = (ImageButton) findViewById(R.id.list);
        previous = (ImageButton) findViewById(R.id.previous);
        play = (ImageButton) findViewById(R.id.play);
        next = (ImageButton) findViewById(R.id.next);
        show_lyric = (ImageButton) findViewById(R.id.show_lyric);
        time_play = (TextView) findViewById(R.id.time_play);
        time_play.setText("--:--");
        time_remain = (TextView) findViewById(R.id.time_remain);
        time_remain.setText("--:--");
        text_artist = (TextView) findViewById(R.id.text_artist);
        pause = (ImageButton)findViewById(R.id.pause);
        
        list_song.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
//				Toast.makeText(RadioPlayerActivity.this, "" + radioEngine.getGet(), Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(), "Không cho phép khi nghe Radio", 2000).show();
			}
		});
        
        previous.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Không cho phép khi nghe Radio", 2000).show();
			}
		});
        
        next.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Không cho phép khi nghe Radio", 2000).show();
			}
		});
        
        show_lyric.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Không cho phép khi nghe Radio", 2000).show();
			}
		});
        
        /*
         * Su kien nhan button Play
         * */
        play.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				radioEngine.play(list, pos);
		        play.setVisibility(View.GONE);
		        pause.setVisibility(View.VISIBLE);
			}
		});
        
        /*
         * Su kien nhan Pause kenh dang choi
         * */
        pause.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {				
				resetMediaPlayer();
				 play.setVisibility(View.VISIBLE);
			        pause.setVisibility(View.GONE);
			}
		});        
	}
    
	/*
	 * Su kien nhan nut Back
	 * */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {   
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		progressDialog.cancel();
    		resetMediaPlayer();    		
			this.finish();
			
        	return super.onKeyDown(keyCode, event);
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
            mGalleryItemBackground = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
            a.recycle();
        }

        public int getCount() {
            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {        	
        	
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView i = new ImageView(mContext);
            int id = getResources().getIdentifier(list.get(position).getIconname(), "drawable", getPackageName());
            i.setImageResource(id);
            if (DisplayMode.mode == 1) i.setLayoutParams(new Gallery.LayoutParams(100, 80));
            else if (DisplayMode.mode == 2) i.setLayoutParams(new Gallery.LayoutParams(200, 150));
            i.setBackgroundResource(mGalleryItemBackground);            
            return i;
        }
    }
 
	public void onComplete(int result) {
		progressDialog.cancel();
				
		if(result == RadioConstant.SUCCESS) {				
									
			mediaPlayer = new MediaPlayer();

	    	try {
	    		mediaPlayer.reset();
	    		mediaPlayer.setDataSource(list.get(pos).getLink());
	    		text_artist.setText(list.get(pos).getDescription());
	    		mediaPlayer.prepare();	    		
	    		mediaPlayer.start();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}

	    	/*
	    	 * Kiem tra xem co nhan duoc packet ve khong ?
	    	 * Neu khong nhan duoc thi dung ket noi
	    	 * */
	    	if(radioEngine.getGet() != 1) {
	    		new AlertDialog.Builder(RadioPlayerActivity.this)    		
	    		.setMessage("Không nhận được dữ liệu từ đài phát")
	    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						radioEngine.closeServerSocket();
						radioEngine.stopMediaPlayer();
					}
				})
				.show();
	    	}
		}
		else {
			new AlertDialog.Builder(RadioPlayerActivity.this)    		
    		.setMessage("Không play được kênh radio này")
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.show();
		}
	}

	public void onStarted(int result) {
		progressDialog.cancel();
		
		if(result == RadioConstant.SERVER_FAIL) {
			new AlertDialog.Builder(RadioPlayerActivity.this)
    		.setMessage("Lỗi kết nối, bạn muốn thử kết nối lại ngay không?")
    		.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					try {
						Thread.sleep(1500);
					} catch (Exception e) {
						e.printStackTrace();
					}
					radioEngine.play(list, pos);
				}
			})
			.setNegativeButton("Không", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.show();					
		}
	}

	public void onError(int result) {
		progressDialog.cancel();
		
		if(result == RadioConstant.CONNECT_FAIL) {
			new AlertDialog.Builder(RadioPlayerActivity.this)    		
    		.setMessage("Lỗi nhận dữ liệu")
    		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
				}
			})
			.show();
		}
	}

	public void onTimeOut(int result) {
		
	}
}