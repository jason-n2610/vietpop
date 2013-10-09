/**
 * 
 */
package com.ppclink.vietpop.lyric;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.ppclink.vietpop.data.Constant;
import com.ppclink.vietpop.data.DataInterface;

import android.os.AsyncTask;

/**
 * @author CHIEN NGUYEN
 *
 */
public class LyricTask extends AsyncTask<Integer, Void, Integer> {
	
	
	//thuoc tinh
	public int action;
	public DataInterface delegate;
	public static ArrayList<Integer> listIdLyric;
	public static ArrayList<String> listSongLyric;
	public LyricData data;
	public static String nameSongSearch;
	public String nameSongSelect;
	public String nameSongPlay;
	public boolean downloadFileSuccess;

	
	@Override
	protected Integer doInBackground(Integer... params) {
		
		switch (params[0]) {
		case Constant.SEARCHLYRIC:
			LinkedHashMap<String, Integer> temp1 = LyricWorker.searchLyric(nameSongSearch);
			if ((temp1 != null)&&(temp1.size()!=0)) {
				listSongLyric = new ArrayList<String>();
				listIdLyric = new ArrayList<Integer>();
				Set<String> temp2 = temp1.keySet();
				for (Iterator<String> iterator = temp2.iterator(); iterator.hasNext();) {
					String string =  iterator.next();
					listSongLyric.add(string);
					listIdLyric.add(temp1.get(string));
				}
			}
			else{
				listSongLyric = null;
				listIdLyric = null;
			}
			break;
		case Constant.SELECTLYRIC:
			String str = LyricWorker.downloadLyricFile(listIdLyric.get(listSongLyric.indexOf(nameSongSelect)));
			if(str!=null){
				downloadFileSuccess = LyricWorker.writeLyric(str, nameSongSearch);
			}
			break;
		case Constant.PLAYLYRIC:
			data = LyricWorker.parseLyric(nameSongPlay);
			break;
		default:
			break;
		}
		return action;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
//		if (!(delegate instanceof MP3PlayerActivity))
//		{
//			progressDialog.cancel();
//		}
//		else if ((action == Constant.SEARCHLYRIC)||(action == Constant.SELECTLYRIC)) {
//			progressDialog.cancel();
//		}
		
		delegate.onComplete(result);
	}
	
	//phuong thuc tim kiem lyric tren mang
	public void searchLyric(String nameSong){
		action= Constant.SEARCHLYRIC;
		nameSongSearch = nameSong;
		this.execute(Constant.SEARCHLYRIC);
		
	}
	
	//phuong thuc chon lyric 
	public void selectLyric(String nameSongSelect){
		action = Constant.SELECTLYRIC;
		this.nameSongSelect = nameSongSelect;
		this.execute(Constant.SELECTLYRIC);
		
	}
	
	//phuong thuc de play lyric
	public void playLyric(String nameSongPlay){
		action = Constant.PLAYLYRIC;
		this.nameSongPlay = nameSongPlay;
		this.execute(Constant.PLAYLYRIC);
		
	}
	
	
}
