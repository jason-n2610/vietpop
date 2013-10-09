/**
 * 
 */
package com.ppclink.vietpop.lyric;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ppclink.vietpop.data.Constant;

import android.os.Environment;

/**
 * @author CHIEN NGUYEN
 *
 */
public class LyricWorker {
	
	//phương thức tìm kiếm lyric trên mạng và trả về list lyric tìm được
	public static LinkedHashMap<String, Integer> searchLyric(String nameSong){
		
		LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		String[] nameLyric=null;
		Integer[] idLyric = null;
		String urlSearch=null;
		TagNode root = null;
		try {
			nameSong = URLEncoder.encode(nameSong, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		urlSearch = Constant.SEARCH_LYRIC_SONG + nameSong + "&x=0&y=0";  //url tìm lyric
		root = HtmlHelper.parseFromUrl(urlSearch);
		if (root !=null) {
			
			//lấy về tên bài hát chứa lyric
			ArrayList<String> temp1 = null;
			if ((temp1=HtmlHelper.getNameLyric(root))!=null) {
				if (temp1.size()>Constant.MAX_LYRIC_SONG) {
					nameLyric = new String[Constant.MAX_LYRIC_SONG];
					for (int i = 0; i < nameLyric.length; i++) {
						nameLyric[i] = temp1.get(i);
						nameLyric[i] = nameLyric[i].replace("&nbsp;", " ");
					}
				}
				else{
					nameLyric =  temp1.toArray(new String[1]);
					for (int i = 0; i < nameLyric.length; i++) {
						nameLyric[i] = nameLyric[i].replace("&nbsp;", " ");
					}
				}
					
			}
			else return null;
			
			//lấy về id của bài hát
			ArrayList<Integer> temp2 = null;
			if ( (temp2=HtmlHelper.getID(root)) != null) {
				if (temp2.size()>Constant.MAX_LYRIC_SONG) {
					idLyric = new Integer[Constant.MAX_LYRIC_SONG];
					for (int i = 0; i < idLyric.length; i++) {
						idLyric[i] = temp2.get(i);
					}
				}
				else{
					idLyric = temp2.toArray(new Integer[1]);
				}
			}
			else return null;
			
			//add tên bài hát và id vào linkedhashmap
			for (int i = 0; i < nameLyric.length; i++) {
				if ((i>0)&&(nameLyric[i].equals(nameLyric[i-1]))) {
					nameLyric[i] = nameLyric[i]+" ";
				}
				temp.put(nameLyric[i], idLyric[i]);
				
			}
		}
		else return null;
		return temp;
	}
	
	//download lyric
	public static String downloadLyricFile(int id){
		String url = null;
		String temp= null;
		TagNode root = null;
		root = HtmlHelper.parseFromUrl(Constant.LYRIC_INFO + String.valueOf(id));
		if(root !=null){
			url = HtmlHelper.getUrlOfLyric(root);
		}
		StringBuilder sb = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpRequestBase httpRequest = null;
		HttpResponse httpResponse = null;
		InputStream inputStream = null;
		url = url.replaceAll(" ", "%20");  //thay dấu cách bằng %20 trong url download lyric
		try {
			httpRequest = new HttpGet(url);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		if(httpRequest != null)
		{
			try
			{
				httpResponse = httpClient.execute(httpRequest);
				inputStream = httpResponse.getEntity().getContent();
				int len = (int)httpResponse.getEntity().getContentLength();
				byte[] data = new byte[1024];
				len = 0;
				while(-1 != (len = inputStream.read(data)))
				{
					sb.append(new String(data,0,len));
				}
				inputStream.close();
				temp = sb.toString();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (IllegalStateException e) {
				e.printStackTrace();
			}
			
		}
		return temp;
	}
	
	//ghi lyric ra file
	public static boolean writeLyric(String lyricSong,String nameSong){
		boolean isSuccess = true;
		String pathFile = Constant.PATH_LYRIC + nameSong + ".xml";   //đường dẫn đến file lưu lyric
		File Dir = new File(Constant.PATH_LYRIC);		
		Dir.mkdirs();				//tạo thư mục chứa lyric
		File file = new File(pathFile);
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw, 8196);
			bw.write(lyricSong);
			bw.flush();
			bw.close();
			fw.close();
		} catch (IOException e) {
			isSuccess = false;
			e.printStackTrace();
		}
		return isSuccess;
	}
	
	
	//parse lyric từ file lưu trong SDCARD
	public static LyricData parseLyric(String lyricSong){
		ArrayList<String> content = new ArrayList<String>();
		ArrayList<Integer> time = new ArrayList<Integer>();
		
		String url = Constant.PATH_LYRIC + lyricSong + ".xml";
		LyricData data = new LyricData();
		String state = Environment.getExternalStorageState();
		if((Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))||(Environment.MEDIA_MOUNTED.equals(state))){
			
			FileInputStream fis;
			try {
				File fileLyric = new File(url);
				if (fileLyric.exists()){
					fis = new FileInputStream(fileLyric);
					DocumentBuilderFactory dbf = DocumentBuilderFactory
							.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(fis);
					// parse lyric
					Element rootElement = doc.getDocumentElement();
					NodeList listParam = rootElement.getChildNodes();
					for (int i = 0; i < listParam.getLength(); i++) {
						Node nodeParam = listParam.item(i);
						if (nodeParam instanceof Element) {
							Element elementParam = (Element) nodeParam;
	
							// lay list the <i>
							NodeList listI = elementParam.getChildNodes();
							StringBuilder sb = new StringBuilder();
							for (int j = 0; j < listI.getLength(); j++) {
								Node nodeI = listI.item(j);
								if (nodeI instanceof Element) {
									Element elementI = (Element) nodeI;
									if (j == 1) {
										String str = elementI.getAttribute("va");
										Integer temp = parseTime(str);
										if (temp != null) {
											time.add(temp);
										}
									}
									String str = elementI.getTextContent();
									sb.append(str);
									if(i==(listParam.getLength()-2)&&(j==listI.getLength()-2)){
										String st = elementI.getAttribute("va");
										Integer tem = parseTime(st);
										if (tem!=null) {
											time.add(tem);
										}
									}
								}
							}
							content.add(sb.toString());
						}
						
					}
					content.add(0, " ");
					time.add(0, 0);
					data.setContent(content);
					data.setTime(time);
					data.setTimeDistance(createTimeDistance(time));
				}	
				else
					return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		else{
			return null;
		}
		
		return data;
	}
	
	//tách thời gian của lyric
	private static Integer parseTime(String str){
    	Integer temp = new Integer(0);
    	try {
    		int v1 = Integer.valueOf(str.substring(0, 2))*60*1000;
        	int v2 = Integer.valueOf(str.substring(3, 5))*1000;
        	int v3= Integer.valueOf(str.substring(6,str.length()));
        	temp = (v1+v2+v3);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
		return temp;
    }
	
	//tạo timedistance trong lyricdata
	public static ArrayList<Integer> createTimeDistance(ArrayList<Integer> time){
		ArrayList<Integer> timeDistance = new ArrayList<Integer>();
		int temp;
		for (int i = 0; i < time.size()-1; i++) {
			temp = time.get(i+1)-time.get(i);
			timeDistance.add(temp);
		}
		return timeDistance;
	}	
}

