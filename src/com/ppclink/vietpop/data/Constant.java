package com.ppclink.vietpop.data;


public class Constant{
	// Cac hang dung khi lay du lieu ve
	public final static int TOPIC = 1;
	public final static int SONG_BY_TOPIC = 2;
	public final static int ALBUM_CATEGORY = 3;
	public final static int ALBUM_BY_ALBUM_CATEGORY = 4;
	public final static int SONG_BY_ALBUM = 5;
	public final static int SONG_CATEGORY = 6;
	public final static int SONG_BY_SONG_CATEGORY = 7;
	public final static int SONG_BY_SONG_CATEGORY_NO_PAGE = 17;
	public final static int FIRST_SONG_AND_PAGE_BY_CATEGORY = 18;
	
	// Cac hang kiem tra kieu du lieu
	public final static int SONG = 8;
	public final static int ALBUM = 9;
	
	public final static int PAGE = 10;
	
	// Cac hang dung trong search du lieu
	public final static int SEARCH_SONG = 11;
	public final static int SEARCH_ARTIST = 12;
	public final static int SEARCH_ALBUM = 13;
	
	//hằng số sử dụng trong lyric
	public final static int SEARCHLYRIC = 14;
	public final static int SELECTLYRIC = 15;
	public final static int PLAYLYRIC = 16;	
	public static final int MAX_LYRIC_SONG = 5; //số lyric lấy về
	
	//hang so su dung trong radio
	public final static int CONNECT_SUCCESS = 100;
	public final static int CONNECT_FAIL = 101;
	public final static int GET_PICTURE = 200;
	
	// path
	public static final String PATH_LYRIC = "/mnt/sdcard/data/vietpop/data/lyric/";  //đường dẫn đến thư mục lưu lyric
	public static final String LYRIC_INFO = "http://star.zing.vn/includes/fnGetSongInfo.php?id=";
	public static final String SEARCH_LYRIC_SONG = "http://star.zing.vn/star/search/do.html?t=0&q=";
	public static final String PATH_PLAYLIST = "/mnt/sdcard/data/vietpop/data/playlist/";
	public static final String PATH_DOWNLOAD = "/mnt/sdcard/data/vietpop/data/download/";
	public static final String PATH_CACHE_PICTURE = "/mnt/sdcard/data/vietpop/data/cache/picture/";
	
	// enum dung trong trang thai download	
	public enum DownloadState
	{
		DOWNLOAD_IDLE,
		DOWNLOAD_INPROGRESS,
		DOWNLOAD_CANCELED,
		DOWNLOAD_COMPLETE
	}
}

