package com.ppclink.vietpop.activity;


import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ppclink.vietpop.data.Constant;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.Translate;

public class OtherActivity extends Activity {		
	
	private final int EXIT = 0;
	private Dialog dialog;
	private String[] otherList = {"Các bài hát đã tải", "Giới thiệu"};
	private int state = 0;
	private ListView list1;
	private ListView list2;
	private ImageView ivAbout;
	private ViewGroup container;
	private ArrayList<String> arrayListFileName;
	private FileAdapter fileAdapter;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (DisplayMode.mode == 1) setContentView(R.layout.other_320x480);
		else if (DisplayMode.mode == 2) setContentView(R.layout.other_480x800);
        container = (ViewGroup)findViewById(R.id.container);
        list1 = (ListView)findViewById(R.id.list1);
        list2 = (ListView)findViewById(R.id.list2);
        ivAbout = (ImageView)findViewById(R.id.ivAbout);
        list1.setAdapter(new OtherAdapter(this.getApplicationContext()));
        arrayListFileName = new ArrayList<String>();
        fileAdapter = new FileAdapter(this);
        list2.setAdapter(fileAdapter);
        setupUI();
    }
    
    public void setupUI() {

    }
    
    @Override
	public Dialog onCreateDialog(int id) {    	
     	 switch(id) {
     	 case EXIT:   
     		 AlertDialog.Builder builder = new AlertDialog.Builder(this);
     		 AlertDialog alert;
     		 builder.setMessage("Bạn muốn thoát khỏi chương trình")
     		   .setPositiveButton("Có", new DialogInterface.OnClickListener() {
     			   public void onClick(DialogInterface dialog, int id) {
     				   OtherActivity.this.finish();
  				   }
     		   })
     		   .setIcon(R.drawable.icon)
     		   .setNegativeButton("Không", new DialogInterface.OnClickListener() {
     			   public void onClick(DialogInterface dialog, int id) {
     				   dialog.cancel();
     			   }});
     		   alert = builder.create();
     		   alert.show();
     		 break;
     	 }
     	 return dialog;     
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(state == 1) {	// list2 -> list1
            	applyRotation(0, 0, 90);
            	state = 0;
               	return true;
            }
            if (state == 0) {
            	showDialog(0);
            }
            if (state == 2){	// about -> list1
            	applyRotation(3, 0, 90);
            	state = 0;
            	return true;
            }
        }
        return false;
    }
    
    
    
    public void applyRotation(int position, float start, float end) {
        final float centerX = container.getWidth() / 3.0f;
        final float centerY = container.getHeight() / 3.0f;
        final Translate rotation =
                new Translate(start, end, centerX, centerY);
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
            Translate rotation = null;            
            if (mPosition == 1) {	//list1 -> list2
                list1.setVisibility(View.GONE);
                list2.setVisibility(View.VISIBLE);
                list2.requestFocus();
                rotation = new Translate(90, 0, centerX, centerY);
            } 
            else if (mPosition == 0){ // list2 -> list1
                list2.setVisibility(View.GONE);
                list1.setVisibility(View.VISIBLE);
                list1.requestFocus();
                rotation = new Translate(90, 0, centerX, centerY);
            }
            else if (mPosition == 2){	// list1 -> about
            	list1.setVisibility(View.GONE);
            	ivAbout.setVisibility(View.VISIBLE);
            	ivAbout.requestFocus();
                rotation = new Translate(90, 0, centerX, centerY);            	
            }
            else if (mPosition == 3){	// about -> list1
            	ivAbout.setVisibility(View.GONE);
            	list1.setVisibility(View.VISIBLE);
            	list1.requestFocus();
                rotation = new Translate(90, 0, centerX, centerY);
            }
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            container.startAnimation(rotation);
        }
    }
    
    
    
    
    class OtherAdapter extends BaseAdapter{            
        private LayoutInflater mInflater;
        private int mIcon1;
        private int mIcon2;
        private int mBackground1;
        private int mBackground2; 
       

        public OtherAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mIcon1 = R.drawable.icon_left_320x480;
            mIcon2 = R.drawable.icon_right_access_320x480;
            mBackground1 = R.drawable.list_item_selector1_320x480;
            mBackground2 = R.drawable.list_item_selector2_320x480;
        }
    	
    	public int getCount() {
    		return otherList.length;
    	}

    	public Object getItem(int position) {
    		return otherList[position];
    	}

    	public long getItemId(int position) {
    		return position;
    	}    	
    	
    	 class OtherHolder {
    		TextView textTopic;
            ImageButton icon1;
            ImageButton icon2;
    	}

    	public View getView(final int position, View convertView, ViewGroup parent) {
    		final String entry = otherList[position];
    		final OtherHolder holder;    		
    		if (convertView == null) {

 				if (DisplayMode.mode == 1) convertView = mInflater.inflate(R.layout.row_browser_320x480, null);
 				else if (DisplayMode.mode == 2) convertView = mInflater.inflate(R.layout.row_browser_480x800, null);			 
                 holder = new OtherHolder();
                 holder.textTopic = (TextView) convertView.findViewById(R.id.text_topic);
                 holder.icon1 = (ImageButton) convertView.findViewById(R.id.icon1);
                 holder.icon2 = (ImageButton)convertView.findViewById(R.id.icon2);                
     			holder.icon2.setImageResource(mIcon2); 
     			holder.icon1.setImageResource(mIcon1);
     			
                 convertView.setTag(holder);                
    		}  else {
                holder = (OtherHolder) convertView.getTag();
            }    	   		
    		convertView.setBackgroundResource((position & 1) == 1 ? mBackground1 : mBackground2); 
    		if (position == 0) holder.icon1.setImageResource(mIcon1);
    			holder.textTopic.setText(entry);     			
    			holder.textTopic.setOnClickListener(new OnClickListener() { 
					public void onClick(View v) {	
						if(holder.textTopic.getText() == otherList[0]) {							
							if (state == 0) {							
								String path = Constant.PATH_DOWNLOAD;
								File file = new File(path);								
								if (file.exists()) {

									File[] listFile = file.listFiles();
									arrayListFileName = null;
									arrayListFileName = new ArrayList<String>();
									int len = listFile.length;
									for (int i = 0; i < len; i++) {
										String fileName = listFile[i].getName();
										int lenName = fileName.length();
										fileName = fileName.substring(0, lenName-4);
										arrayListFileName.add(fileName);
									}
								}
								fileAdapter.notifyDataSetChanged();

								applyRotation(1, 0, 90);
								state = 1;
							}	
						}
						else if (holder.textTopic.getText() == otherList[1]){
							applyRotation(2, 0, 90);
							state = 2;
						}
					}
				});   			
    			holder.icon2.setOnClickListener(new OnClickListener() {					
					public void onClick(View v) {
						if(holder.textTopic.getText() == otherList[0]) {
							if (state == 0) {							
								File file = new File(Constant.PATH_DOWNLOAD);								
								if (file.exists()) {								
									File[] listFile = file.listFiles();
									arrayListFileName = null;
									arrayListFileName = new ArrayList<String>();
									for (int i = 0; i < listFile.length; i++) {
										arrayListFileName.add(listFile[i].getName());
									}
								}
								fileAdapter.notifyDataSetChanged();

								applyRotation(1, 0, 90);
								state = 1;
							}	
						}							
					}
				});       			
    		return convertView;
    	}  	
    }   
    
    class FileAdapter extends BaseAdapter{            
        private LayoutInflater mInflater;
        private int mIcon1;
        private int mIcon2;
        private int mBackground1;
        private int mBackground2; 
       

        public FileAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mIcon1 = R.drawable.icon_left_320x480;
            mIcon2 = R.drawable.icon_right_delete_320x480;
            mBackground1 = R.drawable.list_item_selector1_320x480;
            mBackground2 = R.drawable.list_item_selector2_320x480;
        }
    	
    	public int getCount() {
    		return arrayListFileName.size();
    	}

    	public Object getItem(int position) {
    		return arrayListFileName.get(position);
    	}

    	public long getItemId(int position) {
    		return position;
    	}    	
    	
    	 class FileHolder {
    		TextView textTopic;
            ImageButton icon1;
            ImageButton icon2;
    	}

    	public View getView(final int position, View convertView, ViewGroup parent) {
    		final String entry = arrayListFileName.get(position);
    		final FileHolder holder;    		
    		if (convertView == null) {
    	
 				if (DisplayMode.mode == 1) convertView = mInflater.inflate(R.layout.row_browser_320x480, null);
 				else if (DisplayMode.mode == 2) convertView = mInflater.inflate(R.layout.row_browser_480x800, null);  			 
                 holder = new FileHolder();
                 holder.textTopic = (TextView) convertView.findViewById(R.id.text_topic);
                 holder.icon1 = (ImageButton) convertView.findViewById(R.id.icon1); 
                 holder.icon2 = (ImageButton)convertView.findViewById(R.id.icon2); ;
     			holder.icon1.setImageResource(mIcon1); 
     			holder.icon2.setImageResource(mIcon2); 
                 convertView.setTag(holder);                
    		}  else {
                holder = (FileHolder) convertView.getTag();
            }    	   		
    		convertView.setBackgroundResource((position & 1) == 1 ? mBackground1 : mBackground2); 
    			holder.textTopic.setText(entry);    
    			holder.textTopic.setOnClickListener(new OnClickListener() {					
					public void onClick(View v) {
						Intent intent = new Intent("android.intent.action.MP3PlayerOther");  
    					intent.putExtra("listPlay", arrayListFileName); 
    					intent.putExtra("startPlay", position);    
    					
    					startActivity(intent);						
					}
				});
    			holder.icon2.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View arg0) {
						File file = new File(Constant.PATH_DOWNLOAD + entry+".mp3");
						if (file.exists()) {
							Log.i("2", "delete " + entry);
							file.delete();
							arrayListFileName = null;
							arrayListFileName = new ArrayList<String>();
							File file1 = new File(Constant.PATH_DOWNLOAD);
							File[] listFile = file1.listFiles();
							int len = listFile.length;
							for (int i = 0; i < len; i++) {
								String fileName = listFile[i].getName();
								int lenName = fileName.length();
								fileName = fileName.substring(0, lenName-4);
								arrayListFileName.add(fileName);
							}
						}

						fileAdapter.notifyDataSetChanged();
					}
				});	
    		return convertView;
    	}  	
    }
    
    
}
