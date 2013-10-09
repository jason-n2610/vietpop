package com.ppclink.vietpop.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.util.Log;

public class Utils {
	static HttpURLConnection con;
	
	public static String convertUrl(String url) { // Chuyen URL sang URL download
		URL uDownload;
		try {
			uDownload = new URL(url);
			con = (HttpURLConnection) uDownload.openConnection();
			con.setRequestMethod("GET");
			con.setInstanceFollowRedirects(false);
			con.connect();
		} catch (MalformedURLException e) {
		} catch (IOException e) {
			Log.i("VietPop", e.getMessage());
		}
		String urlMedia = null;
		urlMedia = con.getHeaderField("location");
		urlMedia = urlMedia.replace(" ", "%20"); // Thay the dau " "
		// bang "%20"
		return urlMedia;
	}
	
	public static int getSize(){
		return con.getContentLength();
	}
}
