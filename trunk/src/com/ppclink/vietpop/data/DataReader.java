package com.ppclink.vietpop.data;

import java.io.BufferedInputStream;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;

import com.ppclink.vietpop.activity.MP3PlayerActivity;

public class DataReader extends AsyncTask<Integer, Integer, Integer> {

	public 	DataInterface 	delegate;	// Doi tuong the hien cho activity
	public 	List<BaseData> 	data;	
	public 	int 			page = 0;	// Dung trong truong hop lay ve album va bai hat co phan trang
	public 	int 			action;		

	private	String 			url;		// Tham so url truyen vao
	private ArrayList<String> link;		// Chua link dung trong download anh


	@Override
	protected Integer doInBackground(Integer... params) {
		switch(params[0].intValue()){
		case Constant.TOPIC:
			// Lay ve all topic
			action = Constant.TOPIC;
			data = VietPopData.getAllTopic();
			break;
		case Constant.SONG_BY_TOPIC:
			// Thuc hien lay ve listSong
			action = Constant.SONG_BY_TOPIC;
			data = VietPopData.getListSongByTopic(url);
			break;
		case Constant.ALBUM_CATEGORY:
			// Lay ve tat ca the loai album
			action = Constant.ALBUM_CATEGORY;
			data = VietPopData.getAllAlbumCategory();	
			break;
		case Constant.ALBUM_BY_ALBUM_CATEGORY:
			// Lay ve tat ca the loai album
			action = Constant.ALBUM_BY_ALBUM_CATEGORY;
			data = VietPopData.getAllAlbumByAlbumCategory(url, page);	
			break;
		case Constant.SONG_BY_ALBUM:
			// Lay ve tat ca the loai album
			action = Constant.SONG_BY_ALBUM;
			data = VietPopData.getAllSongByAlbum(url);	
			break;
		case Constant.SONG_CATEGORY:
			// Lay ve tat ca the loai album
			action = Constant.SONG_CATEGORY;
			data = VietPopData.getAllSongCategory();	
			break;
		case Constant.SONG_BY_SONG_CATEGORY:
			// Lay ve tat ca the loai album
			action = Constant.SONG_BY_SONG_CATEGORY;
			try {
				data = VietPopData.getAllSongBySongCategory(url, page);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}	
			break;
		case Constant.SONG_BY_SONG_CATEGORY_NO_PAGE:
			action = Constant.SONG_BY_SONG_CATEGORY_NO_PAGE;
			data = VietPopData.getAllSongBySongCategory(url);
			break;
		case Constant.PAGE:
			// Lay ve tat ca the loai album
			action = Constant.PAGE;
			page = VietPopData.getPage(url);		
			break;
		case Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY:
			//Lay ve trang dau tien va page cua the loai bai hat
			action = Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY;
			data = VietPopData.getFirstSongBySongCategory(url);
			int length = data.size();
			BaseData baseData = data.get(length - 1);
			page = Integer.parseInt(baseData.getName());
			data.remove(length - 1);
			break;
		case Constant.GET_PICTURE:
			action = Constant.GET_PICTURE;
			String folder = Constant.PATH_CACHE_PICTURE;
			File root = new File(folder);
			if (!(root.exists() && root.isDirectory())) {
				root.mkdirs();
			}
			for (int i = 0; i < link.size(); i++) {
				if (!link.get(i).equals("default")){
					try {
						String linkPicture = link.get(i);
						int startName = linkPicture.lastIndexOf("/");
						
						// Dat ten file la ten link khi download
						String strFileName = linkPicture.substring(startName + 1,linkPicture.length());
						File file = new File(folder, strFileName);
						if (!file.exists()) {
							URL uPicture = new URL(linkPicture);
							
//							InputStream isPicture = (InputStream) uPicture.getContent();
							
							URLConnection url = uPicture.openConnection();
							InputStream isPicture = url.getInputStream();
							
							FileOutputStream osPicture = new FileOutputStream(file);

							
							
							BufferedInputStream bis = new BufferedInputStream(isPicture, 8196);
							ByteArrayBuffer baf = new ByteArrayBuffer(2048);
							int current = 0;
							while ((current = bis.read()) != -1) {
				                              baf.append((byte) current);
					                }
							osPicture.write(baf.toByteArray());
							 osPicture.close();

							if (delegate instanceof MP3PlayerActivity) {
								((MP3PlayerActivity) delegate).runOnUiThread(new Runnable() {

											public void run() {
												((MP3PlayerActivity) delegate).updateGallery();
											}
										});
							}
							
							
								
						}
				
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
			}

			break;
		}
		return action;
	}	
	@Override
	protected void onPostExecute(Integer result) {
		delegate.onComplete(result);
//		if (progressDialog != null)
//			progressDialog.cancel();
	}

	// Phuong thuc lay ve list topic
	public void getAllTopic() {
		this.execute(Constant.TOPIC);
	}

	// Phuong thuc lay ve list song by topic url
	public void getAllSongByTopic(String url) {
		this.url = url;
		this.execute(Constant.SONG_BY_TOPIC);
	}
	
	// Phuong thuc lay ve list the loai album
	public void getAllAlbumCategory(){
		this.execute(Constant.ALBUM_CATEGORY);
	}
	
	// Phuong thuc lay ve list album trong the loai album theo trang
	public void getAllAlbumByAlbumCategory(String url, int page){
		this.url = url;
		this.page = page;
		this.execute(Constant.ALBUM_BY_ALBUM_CATEGORY);
	}
	
	// Phuong thuc lay ve list song trong album
	public void getAllSongByAlbum(String url) {
		this.url = url;
		this.execute(Constant.SONG_BY_ALBUM);
	}
	
	// Phuong thuc lay ve the loai song
	public void getAllSongCategory(){
		this.execute(Constant.SONG_CATEGORY);
	}
	
	// Phuong thuc lay ve bai hat trong the loai album theo trang
	public void getAllSongBySongCategory(String url, int page){
		this.url = url;
		this.page = page;
		this.execute(Constant.SONG_BY_SONG_CATEGORY);
	}
	
	public void getAllSongBySongCategory(String url){
		this.url = url;
		this.execute(Constant.SONG_BY_SONG_CATEGORY_NO_PAGE);
	}
	
	// Phuong thuc lay ve trang cua mot url
	public void getPage(String url){
		this.url = url;
		this.execute(Constant.PAGE);
	}
	public void getPictures(ArrayList<String> alPicture){
		this.link = alPicture;
		this.execute(Constant.GET_PICTURE);
	}
	public void getFistSongAndPageByCategory(String url){
		this.url = url;
		this.execute(Constant.FIRST_SONG_AND_PAGE_BY_CATEGORY);
	}
}
