package com.ppclink.vietpop.data;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.htmlcleaner.TagNode;

import android.util.Log;

import com.ppclink.vietpop.lyric.HtmlHelper;

public class VietPopData {	
	private static final String TAG = "VietPopData";
	/*
	 * CAC PHUONG THUC LAY DU LIEU
	 * BAO GOM:
	 * 	- TOPIC, ALBUM_CATEGORY, SONG_CATEGORY
	 * 	- ALL_SONG_BY_TOPIC, ALL_ALBUM_BY_ALBUM_CATEGORY 
	 * 	- ALL_SONG_BY_ALBUM, ALL_SONG_BY_SONG_CATEGORY
	 */
	// Phuong thuc lay ve tat ca cac chu de trang mp3.zing.vn
	public static ArrayList<BaseData> getAllTopic() {
		ArrayList<BaseData> listTopic = null;
		TagNode root = HtmlHelper.parseFromUrl("http://mp3.zing.vn/");
		Log.d(TAG, "root data: "+root.getText().toString());
		if (root != null) {
			listTopic = new ArrayList<BaseData>();
			TagNode ulNode[] = root.getElementsByAttValue("class", "category", true, false);
			Log.e(TAG, "ulNode length: "+ulNode.length);
			if (ulNode.length == 0)
				return null;
			TagNode aNode[] = ulNode[0].getElementsByName("a", true);
			Topic t;
			String name;
			String link;
			for (int i = 0; aNode != null && i < (aNode.length - 1); i++) {
				name = aNode[i].getText().toString();
				link = "http://mp3.zing.vn" + aNode[i].getAttributeByName("href");
				t = new Topic(name, link);
				listTopic.add(t);
			}
		}
		else{
			return null;
		}
		return listTopic;
	}
	
	// Phuong thuc lay chu de tren mp3.m.zing.vn
	public static ArrayList<BaseData> m_getAllTopic(){
		ArrayList<BaseData> listTopic = null;
		TagNode root = HtmlHelper.parseFromUrl("http://mp3.m.zing.vn/web/topic/hot?quality=1&ver=w");
		if (root != null) {
			listTopic = new ArrayList<BaseData>();
			String name, link;
			Topic topic;
			TagNode main[] = root.getElementsByAttValue("class", "contain", true, false);
			TagNode items[] = main[0].getElementsByAttValue("class", "fr", true, false);
			for (int i=1; i<items.length; i++){
				TagNode item[] = items[i].getElementsByName("a", true);
				link = "http://mp3.m.zing.vn/" + item[0].getAttributeByName("href");
				name = item[1].getText().toString();
				topic = new Topic(name, link);
				listTopic.add(topic);
			}
		}
		else{
			return null;
		}
		return listTopic;
	}
	
	// Lay cac bai hat trong chu de trenmp3.m.zing.vn
	public static ArrayList<BaseData> m_getPageSongByTopic(String url, int page){
		ArrayList<BaseData> listSongs = null;
		TagNode root = HtmlHelper.parseFromUrl(url+"&page="+page);
		if (root!= null){
			listSongs = new ArrayList<BaseData>();
			TagNode items[] = root.getElementsByAttValue("class", "c", true, false);
			String name, link, artist;
			Song song;
			for (int i=0; i<items.length; i++){
				TagNode item[] = items[i].getElementsByName("a", true);
				name = item[0].getText().toString();
				link = "http://mp3.m.zing.vn"+item[0].getAttributeByName("href");
				item = items[i].getElementsByName("span", true);
				artist = item[0].getText().toString();
				song = new Song(name, link, artist);
				listSongs.add(song);
			}
		}		
		else {
			return null;
		}
		return listSongs;
	}
	
	public static ArrayList<BaseData> m_getAllSongByTopic(String url){
		ArrayList<BaseData> listSongs = new ArrayList<BaseData>();
		int page = 1;
		TagNode item[], items[], root;
		while(true){
			root = HtmlHelper.parseFromUrl(url+"&page="+page);
			items = root.getElementsByAttValue("class", "c", true, false);
			String name, link, artist;
			Song song;
			if (items.length == 0)
				break;
			for (int i=0; i<items.length; i++){
				item = items[i].getElementsByName("a", true);
				name = item[0].getText().toString();
				link = "http://mp3.m.zing.vn"+item[0].getAttributeByName("href");
				item = items[i].getElementsByName("span", true);
				artist = item[0].getText().toString();
				song = new Song(name, link, artist);
				listSongs.add(song);
			}
			page++;
		}
		return listSongs;
	}

	// Phuong thuc lay va cac bai hat trong 1 chu de
	public static ArrayList<BaseData> getListSongByTopic(String urlList) {
		ArrayList<BaseData> listSongs = null;
		TagNode root = HtmlHelper.parseFromUrl(urlList);
		if (root != null) {
			// Lay ve html cua list tat ca bai hat
			listSongs = new ArrayList<BaseData>();
			TagNode tnList[] = root.getElementsByAttValue("class",
					"bxh_rowsub line rel", true, false);
			Song song;
			String name, link, artist, picture;
			TagNode tnSong[], temp[];
			// Lay tung bai hat
			for (int i = 0; tnList != null && i < tnList.length; i++) {
				tnSong = tnList[i].getElementsByName("a", true);
				temp = tnSong[0].getElementsByName("img", true);
				// Lay ve link picture
				picture = temp[0].getAttributeByName("src");
//				// Lay ten bai hat
				name = tnSong[2].getText().toString();
//				// Lay ten ca si
				artist = tnSong[3].getText().toString();
//				// Lay ve link download
				link = tnSong[5].getAttributeByName("href");
				song = new Song(name, link, picture, artist);
				listSongs.add(song);
			}
		} else {
			return null;
		}
		return listSongs;
	}

	// Phuong thuc lay ve the loai cac album (giong voi Topic)
	public static ArrayList<BaseData> getAllAlbumCategory() {
		ArrayList<BaseData> list = null;
		TagNode root = HtmlHelper.parseFromUrl("http://mp3.zing.vn/nhac/index.html");
		if (root != null){
			list = new ArrayList<BaseData>();
			TagNode tnAlbum[] = root.getElementsByAttValue("class", "block_menu",
					true, false);
			TagNode tnList[] = tnAlbum[0].getElementsByName("a", true);
			Topic a;
			String name, link;
			for (int i = 0; i < tnList.length; i++) {
				name = tnList[i].getText().toString();
				link = "http://mp3.zing.vn" + tnList[i].getAttributeByName("href");
				a = new Topic(name, link);
				list.add(a);
			}
		}
		else{
			return null;
		}
		return list;
	}

	// Phuong thuc lay ve cac Album trong the loai Album phan theo trang
	public static ArrayList<BaseData> getAllAlbumByAlbumCategory(String uAlbumcategory, int page) {
		ArrayList<BaseData> list = null;
		TagNode root = HtmlHelper.parseFromUrl(uAlbumcategory + "?p=" + String.valueOf(page));
		if (root != null) {
			list = new ArrayList<BaseData>();
			TagNode tnList[] = root.getElementsByAttValue("class",
					"rowMusic pdtop7 line rel", true, false);
			TagNode tnTemp[] = null;
			TagNode tnAlbum[] = null;
			Album a;
			String name, artist;
			String link;
			for (int i = 0; i < tnList.length; i++) {
				tnTemp = tnList[i].getElementsByAttValue("class",
						"wdMusic fleft", true, false);
				tnAlbum = tnTemp[0].getElementsByName("a", true);
				name = tnAlbum[0].getText().toString();
				link = "http://mp3.zing.vn"
						+ tnAlbum[0].getAttributeByName("href");
				artist = tnAlbum[1].getText().toString();
				a = new Album(name, link, artist);
				list.add(a);
			}
//			// Lay rieng cho album cuoi cung do chi co cau truc khac
//			tnList = root.getElementsByAttValue("class",
//					"rowMusic pdtop7  rel", true, false);
//			tnTemp = tnList[0].getElementsByAttValue("class", "wdMusic fleft",
//					true, false);
//			tnAlbum = tnTemp[0].getElementsByName("a", true);
//			name = tnAlbum[0].getText().toString();
//			link = "http://mp3.zing.vn" + tnAlbum[0].getAttributeByName("href");
//			artist = tnAlbum[1].getText().toString();
//			a = new Album(name, link, artist);
//			list.add(a);
		} else {
			return null;
		}
		return list;
	}
	
	// Phuong thuc lay tat ca cac album trong the loai album ko phan trang
	public static ArrayList<BaseData> getAllAlbumByAlbumCategory(String sAlbumCategory){
		ArrayList<BaseData> list = null;
		int page = getPage(sAlbumCategory);
		for (int i=1; i<=page; i++){
			list = new ArrayList<BaseData>();
			ArrayList<BaseData> temp;
			temp = getAllAlbumByAlbumCategory(sAlbumCategory, i);
			for (int j=0; j<temp.size(); j++){
				list.add(temp.get(j));
			}
		}
		return list;
	}

	// Phuong thuc lay ve list cac bai hat tu link album truyen vao
	public static ArrayList<BaseData> getAllSongByAlbum(String uAlbum) {
		ArrayList<BaseData> list = null;
		TagNode root = HtmlHelper.parseFromUrl(uAlbum);
		if (root != null) {
			list = new ArrayList<BaseData>();
			TagNode tnUl[] = root.getElementsByAttValue("id", "_plContainer",
					true, false);
			TagNode tnList[] = tnUl[0].getElementsByName("li", true);
			TagNode temp[], temp2[];
			String name, artist, link;
			Song s;
			for (int i = 0; i < tnList.length; i++) {
				temp = tnList[i].getElementsByName("a", true);
				temp2 = tnList[i].getElementsByName("h2", true);
				name = temp2[0].getText().toString();
				temp2 = tnList[i].getElementsByName("h4", true);				
				artist = temp2[0].getText().toString();
				link = temp[6].getAttributeByName("href");
				s = new Song(name, link, artist);
				list.add(s);
			}
		}
		else{		
			return null;			
		}
		return list;
	}

	// Phuong thuc lay ve list cac SongCateogry
	public static ArrayList<BaseData> getAllSongCategory() {
		ArrayList<BaseData> list = null;
		TagNode root = HtmlHelper.parseFromUrl("http://mp3.zing.vn/nhac/index.html");
		if (root != null){
			list = new ArrayList<BaseData>();
			TagNode tnAlbum[] = root.getElementsByAttValue("class", "block_menu",
					true, false);
			TagNode tnList[] = tnAlbum[2].getElementsByName("a", true);
			Topic a = null;
			String name, link;
			for (int i = 0; i < tnList.length; i++) {
				name = tnList[i].getText().toString();
				link = "http://mp3.zing.vn" + tnList[i].getAttributeByName("href");
				a = new Topic(name, link);
				list.add(a);
			}
		}
		else {
			return null;
		}
		return list;
	}

	// Phuong thuc lay ve list cac Song trong SongCategory dua vao page truyen vao
	public static ArrayList<BaseData> getAllSongBySongCategory(String uSongCategory, int page) throws MalformedURLException {
		ArrayList<BaseData> list = null;
		TagNode root = HtmlHelper.parseFromUrl(uSongCategory + "?p=" + String.valueOf(page));
		if (root != null){
			list = new ArrayList<BaseData>();
			TagNode tnList[] = root.getElementsByAttValue("class",
					"rowNor pdtop7 line rel", true, false);
			TagNode temp[];
			String name, artist, link;
			Song s;
			for (int i = 0; i < tnList.length; i++) {
				temp = tnList[i].getElementsByName("a", true);
				name = temp[0].getText().toString();
				artist = temp[1].getText().toString();
				link = temp[3].getAttributeByName("href");
				s = new Song(name, link, artist);
				list.add(s);
			}
			// Lay rieng cho bai hat cuoi do co cau truc khac
//			tnList = root.getElementsByAttValue("class", "rowNor pdtop7  rel",
//					true, false);
//			temp = tnList[0].getElementsByName("a", true);
//			name = temp[0].getText().toString();
//			artist = temp[1].getText().toString();
//			link = temp[3].getAttributeByName("href");
//			s = new Song(name, link, artist);
//			list.add(s);
		}
		else{
			return null;
		}
		return list;
	}
	
	public static ArrayList<BaseData> getFirstSongBySongCategory(String url){
		ArrayList<BaseData> list = null;
		TagNode root = HtmlHelper.parseFromUrl(url);
		String page;
		if (root != null){
			list = new ArrayList<BaseData>();
			TagNode tnList[] = root.getElementsByAttValue("class",
					"rowNor pdtop7 line rel", true, false);
			TagNode temp[];
			String name, artist, link;
			Song s;
			for (int i = 0; i < tnList.length; i++) {
				temp = tnList[i].getElementsByName("a", true);
				name = temp[0].getText().toString();
				artist = temp[1].getText().toString();
				link = temp[3].getAttributeByName("href");
				s = new Song(name, link, artist);
				list.add(s);
			}
//			// Lay rieng cho bai hat cuoi do co cau truc khac
//			tnList = root.getElementsByAttValue("class", "rowNor pdtop7  rel",
//					true, false);
//			temp = tnList[0].getElementsByName("a", true);
//			name = temp[0].getText().toString();
//			artist = temp[1].getText().toString();
//			link = temp[3].getAttributeByName("href");
//			s = new Song(name, link, artist);
//			list.add(s);
			
			// Lay ve page
			TagNode tnPages = root.findElementByAttValue("class",
					"pagination txtCenter pdright10 pdtop20", true, false);
			TagNode tnPage[] = tnPages.getElementsByName("a", true);
			if (tnPage.length == 1)
				page = "1";
			else {
				int length = tnPage.length - 1;
				String endPage = tnPage[length].getAttributeByName("href");
				int indexEqual = endPage.lastIndexOf("=");
				page = endPage.substring(indexEqual + 1, endPage.length());
			}
			BaseData basePage = new BaseData("null", page);
			list.add(basePage);
		}
		else{
			return null;
		}
		return list;
	}
	
	// Lay ve Song by SongCategory ko phan trang
	public static ArrayList<BaseData> getAllSongBySongCategory(String url){
		ArrayList<BaseData> list = null;
		int page = getPage(url);
		for (int i=1; i<=page; i++){
			list = new ArrayList<BaseData>();
			ArrayList<BaseData> temp;
			try {
				temp = getAllSongBySongCategory(url, i);
				for (int j=0; j<temp.size(); j++){
					list.add(temp.get(j));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	// Tra ve tong so trang cua mot AlbumCategory dua vao link truyen vao
	public static int getPage(String url) {
		int page = 1;
		TagNode root = HtmlHelper.parseFromUrl(url);
		if (root != null){
			TagNode tnPages = root.findElementByAttValue("class",
					"pagination txtCenter pdright10 pdtop20", true, false);
			TagNode tnPage[] = tnPages.getElementsByName("a", true);
			if (tnPage.length == 1)
				page = 1;
			else {
				int length = tnPage.length - 1;
				String temp = tnPage[length].getAttributeByName("href");
				int indexEqual = temp.lastIndexOf("=");
				page = Integer
						.valueOf(temp.substring(indexEqual + 1, temp.length()));
			}
		}
		else{
			return 0;
		}

		return page;
	}
	
	/*
	 * CAC PHUONG THUC TIM KIEM 
	 * BAO GOM:	- PHUONG THUC LAY TONG SO TRANG KHI NHAP VAO KEY VA TYPE
	 * 			- PHUONG THUC LAY LIST<BASE> TU KEY, TYPE VA PAGE 
	 */
	public static int getPageSearch(String key, int searchType){
		int page = 0;
		try {
			key = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String sUrl = null;
		switch(searchType){
		case Constant.SEARCH_SONG:
			sUrl = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q="+key+"&t=title";
			break;
		case Constant.SEARCH_ARTIST:
			sUrl = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q="+key+"&t=artist";
			break;
		case Constant.SEARCH_ALBUM:
			sUrl = "http://mp3.zing.vn/tim-kiem/playlist.html?q="+key+"&t=title";
		}
		TagNode root = HtmlHelper.parseFromUrl(sUrl);
		if (root != null){
			TagNode tnPage[] = root.getElementsByAttValue("class", "pagination txtCenter pdright10 pdtop20", true, false);
			if (tnPage.length != 0 && tnPage != null){
				TagNode tnLastPage[] = tnPage[0].getElementsByName("a", true);
				String temp = tnLastPage[tnLastPage.length-1].getAttributeByName("href");
				if (temp != null){
					int indexEqual = temp.lastIndexOf("=");
					page = Integer.valueOf(temp.substring(indexEqual+1, temp.length()));
				}
				else{	// Truong hop chi co 1 trang ket qua
					return 1;
				}
			}
			else{
				return 0;
			}
		}
		else{
			return 0;
		}
		return page;
	}
	
	// Phuong thuc lay ve list ket qua phan theo trang
	public static ArrayList<BaseData> getSearchResult(String key, int searchType, int page){
		ArrayList<BaseData> list = null;
		try {
			key = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String sUrl = null;		
		TagNode tnLists[] = null, tnList[] = null, root = null;
		String name, link, artist;
		Song s;
		Album a;
		switch(searchType){
		case Constant.SEARCH_SONG:
			list = new ArrayList<BaseData>();
			sUrl = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q="+key+"&t=title&p="+page;
			root = HtmlHelper.parseFromUrl(sUrl);
			tnLists = root.getElementsByAttValue("class", "pdhit rowNor line rel", true, false);
			for (int i=0; i<tnLists.length; i++){
				tnList = tnLists[i].getElementsByName("a", true);
				name = tnList[0].getText().toString();
				artist = tnList[1].getText().toString();
				link = tnList[5].getAttributeByName("rel");
				s = new Song(name, link, artist);
				list.add(s);
			}
			// Lay ve bai hat cuoi cung cua trang
			tnLists = root.getElementsByAttValue("class", "pdhit rowNor  rel", true, false);
			tnList = tnLists[0].getElementsByName("a", true);	
			name = tnList[0].getText().toString();
			artist = tnList[1].getText().toString();
			link = tnList[5].getAttributeByName("rel");
			s = new Song(name, link, artist);
			list.add(s);		
			break;
		case Constant.SEARCH_ARTIST:
			sUrl = "http://mp3.zing.vn/tim-kiem/bai-hat.html?q="+key+"&t=artist&p="+page;
			list = new ArrayList<BaseData>();
			root = HtmlHelper.parseFromUrl(sUrl);
			tnLists = root.getElementsByAttValue("class", "pdhit rowNor line rel", true, false);
			for (int i=0; i<tnLists.length; i++){
				tnList = tnLists[i].getElementsByName("a", true);
				name = tnList[0].getText().toString();
				artist = tnList[1].getText().toString();
				link = tnList[4].getAttributeByName("rel");
				s = new Song(name, link, artist);
				list.add(s);
			}			
			// Lay ve bai hat cuoi cung cua trang
			tnLists = root.getElementsByAttValue("class", "pdhit rowNor  rel", true, false);
			tnList = tnLists[0].getElementsByName("a", true);	
			name = tnList[0].getText().toString();
			artist = tnList[1].getText().toString();
			link = tnList[4].getAttributeByName("rel");
			s = new Song(name, link, artist);
			list.add(s);		
			break;
		case Constant.SEARCH_ALBUM:
			sUrl = "http://mp3.zing.vn/tim-kiem/playlist.html?q="+key+"&p="+page;
			list = new ArrayList<BaseData>();
			root = HtmlHelper.parseFromUrl(sUrl);
			tnLists = root.getElementsByAttValue("class", "rowNor pdtop7 line pdright60 rel", true, false);
			for (int i=0; i<tnLists.length; i++){
				tnList = tnLists[i].getElementsByName("a", true);
				name = tnList[0].getAttributeByName("title");
				link = "http://mp3.zing.vn"+tnList[0].getAttributeByName("href");
				a = new Album(name, link);
				list.add(a);
			}
			// Them album cuoi cung
			tnLists = root.getElementsByAttValue("class", "rowNor pdtop7  pdright60 rel", true, false);
			tnList = tnLists[0].getElementsByName("a", true);
			name = tnList[0].getAttributeByName("title");
			link = "http://mp3.zing.vn"+tnList[0].getAttributeByName("href");
			a = new Album(name, link);
			list.add(a);
		}
		return list;
	}
	
}
