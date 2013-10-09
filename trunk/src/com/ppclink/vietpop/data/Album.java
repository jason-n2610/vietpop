package com.ppclink.vietpop.data;

/*
 * Album bao gom cac bai hat cung mot the loai nhu ca sy, nhac sy, ...
 * Gom 3 thuoc tinh ten album, link album, va ca sy the hien
 * Duoc ke thua tu lop BaseData va them thuoc tinh artist
 */

public class Album extends BaseData {
	
	String artist;
	
	public Album(String name, String link, String artist) {
		super(name, link);
		this.artist = artist;
	}
	
	public Album(String name, String link){
		super(name, link);
	}
	
	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getArtist() {
		return this.artist;
	}
}
