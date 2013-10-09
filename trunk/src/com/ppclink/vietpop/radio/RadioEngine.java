package com.ppclink.vietpop.radio;

import java.io.InputStream;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

public class RadioEngine {
	
	//---load thu vien rtmp
	static {
		System.loadLibrary("rtmp");
	}
	
	//---khai bao native function
	public native int startServer();
	public native int connect();
	public native void stop(); 
	public native int createServerSocket();
	public native int rtmpConnect();
	public native int setupParams(String host, String url, String playPath, String app, int port);
	public native int getGet();
	public native int closeServerSocket();
	private ChannelHandler handler;
	private Context context;
	private ArrayList<Channel> listChannels;
	private MediaPlayer mediaPlayer;
	public RadioInterface delegate;
	public ProgressDialog dialog = null;
	
	//---test nativeMethod---
	public native void nativeMethod(int depth);
	public void callBack(int depth) {
		if(depth < 5) {
			Log.i("VietPop Native Method", "In Java, depth = " + depth + ", about to enter C");
			nativeMethod(depth + 1);
			Log.i("VietPop Native Method", "In Java, depth = " + depth + ", back from C");
		}
		else {
			Log.i("VietPop Native Method", "In Java, depth = " + depth + ", limit exceeded");
		}
	}
	
	
	
	//---ham tao
	public RadioEngine(Context context) {
		handler = new ChannelHandler();
		mediaPlayer = null;	
		this.context = context;
	}
	
	//---phuong thuc gui tra ve 1 list kenh
	public ArrayList<Channel> getChannels(InputStream is) {	
		listChannels = handler.read(is);			
		return listChannels;
	}
	
	/*
	 * stop media player
	 * */
	public void stopMediaPlayer() {
		stop();
	}
	
	/*
	 * Phuong thuc play 1 kenh radio
	 * Tham so truyen vao la vi tri kenh trong list va list cac kenh
	 * */
	public int checkOnOff(ArrayList<Channel> list, int position) {						
		//---neu la kenh http
		if(position > 4) {
			if(list.get(position).getStatus().equals("online"))
				return RadioConstant.HTTP_ONLINE;
			else {
				return RadioConstant.HTTP_OFFLINE;
			}
		}
		//---neu la kenh rtmp
		else {
			if(list.get(position).getStatus().equals("online")) {
				return RadioConstant.RTMP_ONLINE;
			}
			else {
				return RadioConstant.RTMP_OFFLINE;
			}			
		}
	}
	
	/*
	 * Phuong thuc play 1 kenh 
	 * */
	public void play(ArrayList<Channel> list, int position) {
		if(position > 4) {
			new PlayHttpChannelTask().execute(list.get(position).getLink());
		}
		else {
			new PlayRtmpChannelTask().execute(list.get(position).getHost(), 
					list.get(position).getUrl(), 
					list.get(position).getPlayPath(), 
					list.get(position).getApp(), 
					list.get(position).getPort(), 
					list.get(position).getLink());
		}
	}
	
	/*
	 * Phuong thuc kiem tra mediaplayer co nhan duoc du lieu khong
	 * */
	public void checkTimeOut() {
		new TimeoutMediaPlayer().execute();
	}
	
	//---play rtmp channel task
    private class PlayRtmpChannelTask extends AsyncTask<String, Void, Integer> {     
    	

		@Override
		protected Integer doInBackground(String... params) {						
			
			int port = Integer.parseInt(params[4]);
			
			setupParams(params[0], params[1], params[2], params[3], port);
			int connectState = rtmpConnect();
			if(connectState == -1) {
				return RadioConstant.CONNECT_FAIL;
			}
			
			connect();
			
			//---khoi dong server o cong 8888
			int serverState = createServerSocket();
			if(serverState == -1) {
				return RadioConstant.SERVER_FAIL;
			}
			
			startServer();					
			
			return RadioConstant.SUCCESS;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			switch(result) {
			case RadioConstant.SERVER_FAIL :
				delegate.onStarted(result);
				break;
			case RadioConstant.CONNECT_FAIL :
				delegate.onError(result);
				break;
			default :
				delegate.onComplete(result);
			}															
		}    		
    }
	
	//---play http channel task
    private class PlayHttpChannelTask extends AsyncTask<String, Void, Integer> {     	    

		@Override
		protected Integer doInBackground(String... params) {
			
			return RadioConstant.SUCCESS;
		}
		
		@Override
		protected void onPreExecute() {			

		}
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			switch(result) {
			case RadioConstant.SERVER_FAIL :
				delegate.onStarted(result);
				break;
			case RadioConstant.CONNECT_FAIL :
				delegate.onError(result);
				break;
			default :
				delegate.onComplete(result);
			}																
		}
    }
    
    /*
     * Kiem tra ket noi vuot qua thoi gian
     * */
    private class TimeoutMediaPlayer extends AsyncTask<Void, Void, Integer> {

		@Override
		protected Integer doInBackground(Void... params) {
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			while(end - start < 5000) {
				end = System.currentTimeMillis();
			}
			return RadioConstant.CONNECTION_TIMEOUT;
		}
    	
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			
			delegate.onTimeOut(result);
		}
    }
}
