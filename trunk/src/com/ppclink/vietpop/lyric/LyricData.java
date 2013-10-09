/**
 * 
 */
package com.ppclink.vietpop.lyric;

import java.util.ArrayList;

/**
 * @author CHIEN NGUYEN
 *
 */
public class LyricData {
	
	//thuộc tính
	private ArrayList<String> content = new ArrayList<String>();
	private ArrayList<Integer> time = new ArrayList<Integer>();
	private ArrayList<Integer> timeDistance = new ArrayList<Integer>();
	
	//setter va getter
	public ArrayList<String> getContent() {
		return content;
	}

	public void setContent(ArrayList<String> content) {
		this.content = content;
	}

	public ArrayList<Integer> getTime() {
		return time;
	}

	public void setTime(ArrayList<Integer> time) {
		this.time = time;
	}

	public ArrayList<Integer> getTimeDistance() {
		return timeDistance;
	}

	public void setTimeDistance(ArrayList<Integer> timeDistance) {
		this.timeDistance = timeDistance;
	}

}
