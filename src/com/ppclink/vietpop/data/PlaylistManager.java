package com.ppclink.vietpop.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PlaylistManager{			
	
	public static String checkPath(){
		String rootDir = Constant.PATH_PLAYLIST;
		File dir = new File(rootDir);
		if (!(dir.exists() && dir.isDirectory()) ){
			// Khong ton tai thi tao thu muc luu tru
			try{
				dir.mkdirs();
			}
			catch (SecurityException e){
				e.printStackTrace();
			}
		}
		return rootDir;
	}
	
	// Lay ve tat ca cac playlist
	public static String[] getPlaylist(){	
		// Kiem tra xem ton tai folder luu tru chua
		String rootDir = checkPath();
		File dirFile = new File(rootDir);
		String playlist[] = dirFile.list();
		if (playlist != null){
			int length = playlist.length;
			for (int i=0; i<length; i++){
				playlist[i] = dirFile.list()[i].substring(0, playlist[i].length()-4);
			}
		}
		else{
			
		}
		return playlist;
	}
	// Them moi mot playlist
	public static boolean addPlaylist(String name){
		boolean info = false;
		String rootDir = checkPath();
		name = name + ".pls";
		File playlist = new File(rootDir, name);
		if (!playlist.exists()) {
			// Playlist chua ton tai, them moi
			try {
				info = playlist.createNewFile();
				return info;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			return false;
		}
		return info;
	}

	// Xoa playlist dua vao ten truyen vao
	public static boolean deletePlaylist(String playlistName){
		String rootDir = checkPath();
		File playlist = new File(rootDir, playlistName+".pls");
		if (!playlist.exists()){
			// Play list ko ton tai
			return false;
		}
		else{
			return playlist.delete();
		}
	}
	
	// Xoa tat ca playlist
	public static boolean deleteAllPlaylist(){
		boolean info = false;
		String rootDir = checkPath();
		File dir = new File(rootDir);
		String listFile[] = dir.list();
		if (listFile != null){
			for (int i=0; i<listFile.length; i++){
				File file = new File(rootDir, listFile[i]);
				if (file.delete())
					info = true;
				else{
					info = false;
					break;
				}
			}
		}
		return info;
	}
	
	// Lay ve cac bai hat cua playlist
	public static ArrayList<Song> getSong(String playlistName){
		String rootDir = checkPath();
		ArrayList<Song> listSong = null;		
		File playlist = new File(rootDir, playlistName+".pls");
		try {
			listSong = new ArrayList<Song>();
			BufferedReader reader = new BufferedReader(new FileReader(playlist), 8192);
			
			String line, name, link, artist, picture;
			Song song;
			try {
				while((line = reader.readLine()) != null){
					StringTokenizer token = new StringTokenizer(line, "\\");	// Phan tich token dang .&&.&&.&&
					if(token.hasMoreTokens()){
						name = token.nextToken();					
						link = token.nextToken();
						picture = token.nextToken();
						artist = token.nextToken();
						if (picture == "default"){
							song = new Song(name, link, artist);
						}
						else{
							song = new Song(name, link, picture, artist);
						}
						listSong.add(song);
					}
				}
			} catch (Exception e) {

			}		
			
			
		}catch (IOException e) {
			e.printStackTrace();
		}
		//playlist.
		
		return listSong;
	}
	
	// Them moi mot bai hat vao playlist
	public static boolean addOneSong(String playlistName, Song song){
		boolean info = false;
		String rootDir = checkPath();
		File playlist = new File(rootDir, playlistName+".pls");
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(playlist,true), 8192);
			String line, name, link, artist, picture;
			name = song.getName();
			link = song.getLink();
			artist = song.getNameArtist();
			picture = song.getLinkPicture();
			if ((picture == null) || (picture.equals(""))){
				picture = "default";
			}
			line = name+"\\"+link+"\\"+picture+"\\"+artist;
			writer.newLine();
			writer.write(line);
			writer.flush();
			writer.close();
			info = true;
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return info;
	}
	// Them mot list bai hat vao playlist
	public static boolean addListSong(String playlistName, ArrayList<Song> listSong){
		boolean result = false;				
		for (int i=0; i<listSong.size(); i++){
			result = addOneSong(playlistName, listSong.get(i));
			if (!result){
				break;
			}
		}
		return result;
	}
	
	
	// Xoa mot bai hat khoi playlist dua vao index truyen vao
	public static boolean deleteSong(String playlistName, int index){
		String rootDir = checkPath();
		ArrayList<Song> listSong = new ArrayList<Song>();
		listSong = getSong(playlistName);
		listSong.remove(index);	// remove song khoi playlist
		File playlist = new File(rootDir, playlistName+".pls");
		try{	// ghi lai playlist voi listsong vua xu ly
			BufferedWriter writer = new BufferedWriter(new FileWriter(playlist), 8192);
			for (int i=0; i<listSong.size(); i++){
				String name = listSong.get(i).getName();
				String link = listSong.get(i).getLink();
				String artist = listSong.get(i).getNameArtist();
				String picture = listSong.get(i).getLinkPicture();
				if (picture == null){
					picture = "default";
				}
				String line = name+"&&"+link+"&&"+picture+"&&"+artist;
				writer.newLine();
				writer.write(line);
			}
			writer.flush();
			writer.close();
			return true;
		}
		catch(IOException e){
			e.printStackTrace();
		}
		return false;
	}
	
	// Xoa tat ca bai hat cua 1 playlist
	public static boolean deleteAllSong(String playlistName){
		// Xoa tat ca bai hat cua 1 playlist thuc te la xoa playlist va tao playlist trung ten
		boolean info = false;
		String rootDir = checkPath();
		File file = new File(rootDir, playlistName+".pls");
		file.delete();
		try {
			return file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}
	
	// Ham thuc thi de tao playlist Now Playing
	// Moi khi ham duoc goi, se tu dong tao Now Playing
	// Neu Now Playing da ton tai thuc hien ghi de 
	// Tham so truyen vao la list Song muon ghi vao Now Playing
	// Gia tri tra ve la true neu update thanh cong, false la nguoc lai
	public static boolean updateNowplaying(ArrayList<Song> listSong){
		String rootDir = checkPath();
		File file = new File(rootDir, "Now Playing.pls");
		if (file.exists()){
			file.delete();
		}
		return addListSong("Now Playing", listSong);
	}
}
