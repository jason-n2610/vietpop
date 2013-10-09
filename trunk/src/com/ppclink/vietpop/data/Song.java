package com.ppclink.vietpop.data;

public class Song extends BaseData {
	private String linkPicture;
	private String nameArtist;

	// Phương thức khởi tạo
	public Song(String nameSong, String linkSong, String linkPicture, String nameArtist){
		super(linkSong, nameSong);
		this.linkPicture = linkPicture;
		this.nameArtist = nameArtist;
	}

	public Song(String name, String link, String artist) {
		super(link, name);
		this.nameArtist = artist;
	}

	// Set, Get
	public void setNameAritst(String nameAritst) {
		this.nameArtist = nameAritst;
	}

	public String getNameArtist() {
		return nameArtist;
	}

	public void setLinkPicture(String linkPicture) {
		this.linkPicture = linkPicture;
	}

	public String getLinkPicture() {
		return linkPicture;
	}
}
