package com.ppclink.vietpop.activity;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.ppclink.vietpop.adv.AdvTask;
import com.ppclink.vietpop.adv.AdvViewGroup;
import com.ppclink.vietpop.radio.Channel;
import com.ppclink.vietpop.radio.RadioConstant;
import com.ppclink.vietpop.radio.RadioEngine;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.TranslateADV;

public class RadioActivity extends Activity {	
	private AssetManager asset;
	private ArrayList<Channel> lstChannels;
	private RadioEngine radioEngine;	
	private MediaPlayer mediaPlayer;
	private int channlePos;	
	private AdvViewGroup advView;	
	
	//---reset media player
	public void resetMediaPlayer() {
		if(mediaPlayer != null) {
			mediaPlayer.reset();
			new RadioEngine(RadioActivity.this).stopMediaPlayer();
			mediaPlayer.release();
			mediaPlayer = null;					
		}		
	}		
	
	//---su kien khi nhan KEYCODE_BACK
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		//---hoi nguoi dung co muon exit
    		new AlertDialog.Builder(RadioActivity.this)   		
    		.setMessage("Bạn có muốn thoát khỏi chương trình")
    		.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					//---dung activity va dung mediaplayer
					RadioActivity.this.finish();
					System.exit(0);
				}
			})
			.setNegativeButton("Quay lại", null)
			.show();   
    		
    		return true;
    	}
    	else 
    		return super.onKeyDown(keyCode, event);
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
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (DisplayMode.mode == 1) setContentView(R.layout.radio_320x480);
		else if (DisplayMode.mode == 2) setContentView(R.layout.radio_480x800);
               
        ListView list_channel = (ListView)findViewById(R.id.list_channel);               
        asset = getAssets();
        String[] files;       
        advView = (AdvViewGroup) findViewById(R.id.advViewGroup1);
		if (!advView.isPlaying) {
			AdvTask advTask = new AdvTask();
			advTask.startAdv(advView);
		}
        applyRotationADV(1, 0, 50);
        try {
			files = asset.list("file");
			InputStream in = asset.open("file/" + files[0]);				
			radioEngine = new RadioEngine(this);					
			lstChannels = radioEngine.getChannels(in);

	        if(lstChannels == null) 
	        	Log.i("VietPop", "Danh sach rong");	        	        
		} catch (Exception e1) {
			e1.printStackTrace(System.out);
		} 
        //---hien thi list kenh radio
        list_channel.setAdapter(new ChannelAdapter(this, lstChannels));         
    }

	class ChannelAdapter extends BaseAdapter {
        private List<Channel> listChannel;        
        private LayoutInflater mInflater;
        private int mBackground1;
        private int mBackground2;        

        public ChannelAdapter(Context context, List<Channel> listChannel) {
            this.listChannel = listChannel;
            mInflater = LayoutInflater.from(context);
            mBackground1 = R.drawable.list1_320x480;
            mBackground2 = R.drawable.list2_320x480;
        }
    	
    	public int getCount() {
    		return listChannel.size();
    	}

    	public Object getItem(int position) {
    		return listChannel.get(position);
    	}

    	public long getItemId(int position) {
    		return position;
    	}    	
    	
    	 class ChannelHolder {
    		TextView textTopic;
            ImageButton icon1;
    	}

    	public View getView(int position, View convertView, ViewGroup parent) {
    		final Channel entry = listChannel.get(position);
    		ChannelHolder holder;    		
    		if (convertView == null) {
    		
 				if (DisplayMode.mode == 1) convertView = mInflater.inflate(R.layout.row_browser_320x480, null);
 				else if (DisplayMode.mode == 2) convertView = mInflater.inflate(R.layout.row_browser_480x800, null); 			 
                 holder = new ChannelHolder();
                 holder.textTopic = (TextView) convertView.findViewById(R.id.text_topic);
                 holder.icon1 = (ImageButton) convertView.findViewById(R.id.icon1);
                 convertView.setTag(holder);                
    		}  else {
                holder = (ChannelHolder) convertView.getTag();
            }    	   		
    		convertView.setBackgroundResource((position & 1) == 1 ? mBackground1 : mBackground2);    		
    		if (entry != null) {  
    			holder.textTopic.setText(entry.getDescription());
    			holder.icon1.setImageResource(getResources().getIdentifier(entry.getIconname(), "drawable", getPackageName()));
    			holder.textTopic.setOnClickListener(new OnClickListener() {
    				public void onClick(View v) {   
   					
    					/*
    					 * Click vao 1 kenh se kiem tra online hay offline
    					 * Neu online thi truyen vi tri kenh vua click sang giao dien RadioPlayerActivity
    					 * */
    					ProgressDialog progressDialog = ProgressDialog.show(RadioActivity.this, "", "Connecting ...");
    					
    					radioEngine = new  RadioEngine(RadioActivity.this);			
    					int pos = Integer.parseInt(entry.getId());    					
    					channlePos = pos;    					
    					asset = getAssets();
    			        String[] files;   			       
    			        try {
    						files = asset.list("file");
    						InputStream in = asset.open("file/" + files[0]);   						
    						lstChannels = radioEngine.getChannels(in);        	        
    					} catch (Exception e1) {
    						e1.printStackTrace(System.out);
    					}
    			        
    					int check = radioEngine.checkOnOff(lstChannels, channlePos);    					
    					if(check == RadioConstant.HTTP_OFFLINE || check == RadioConstant.RTMP_OFFLINE) {
    						progressDialog.cancel();
    						AlertDialog.Builder builder = new AlertDialog.Builder(RadioActivity.this);
    						builder.setTitle("Thông báo");
    						builder.setMessage("Kênh này hiện offline");
    						builder.show();
    					}
    					else {
    						progressDialog.cancel();
    						Intent intent = new Intent("android.intent.action.RadioPlayer");
        					intent.putExtra("position", channlePos);        					
        					
        					startActivity(intent);
    					}
    				}
    			});
    		}
    		return convertView;
    	}    	
    }
}