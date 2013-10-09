/**
 * 
 */
package com.ppclink.vietpop.lyric;

import java.io.IOException
;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 * @author CHIEN NGUYEN
 *
 */
public class HtmlHelper {
	
	//trang html cần parse
	public static TagNode parseFromUrl(String url) {
		
		TagNode root = null;
		HtmlCleaner cleaner= new HtmlCleaner();
		CleanerProperties pro = cleaner.getProperties();
		pro.setAllowHtmlInsideAttributes(true);
		pro.setAllowMultiWordAttributes(true);
		pro.setRecognizeUnicodeChars(true);
		pro.setOmitComments(true);
		
		URL urlObj;
		try {
			urlObj = new URL(url);
			
			try {
				root = cleaner.clean(urlObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	//lấy về id bài hát
	public  static ArrayList<Integer> getID(TagNode root){
		
		ArrayList<Integer> temp = new ArrayList<Integer>();
		ArrayList<TagNode> linkList = new ArrayList<TagNode>();
		TagNode[] linkElements = root.getElementsByName("a", true);
		for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals("font_12 bold"))
            {
                linkList.add(linkElements[i]);
            }
        }
		for (Iterator<TagNode> iterator = linkList.iterator(); iterator.hasNext();) {
			TagNode divElement = (TagNode) iterator.next();
			Integer id = parseString(divElement.getAttributeByName("href"));
			temp.add(id);
		}
		if (temp.size()==0) {
			return null;
		}
        return temp;
	}
	
	//lay ve ten lyric bai hat
	public static ArrayList<String> getNameLyric(TagNode root){
		
		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<TagNode> linkList = new ArrayList<TagNode>();
		TagNode[] linkElements = root.getElementsByName("a", true);
		for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals("font_12 bold"))
            {
                linkList.add(linkElements[i]);
            }
        }
		for (Iterator<TagNode> iterator = linkList.iterator(); iterator.hasNext();) {
			TagNode divElement = (TagNode) iterator.next();
			String content = divElement.getText().toString();
			temp.add(content);
		}
		
		if (temp.size()==0) {
			return null;
		}
        return temp;
	}
	
	//lấy về URL của file chứa lyric
	public static String getUrlOfLyric(TagNode root){
		String temp = null;
		TagNode node = root.findElementByName("lyric", true);
		temp = node.getText().toString();
		return temp;
		
	}
	
	//loc string de lay ve id
	public static Integer parseString(String value){
    	Integer temp=null;
		Pattern p1 = Pattern.compile("\\.");
    	Pattern p2 = Pattern.compile(".html");
    	int t1=0,t2=0;
		Matcher m1 = p1.matcher(value);
		Matcher m2 = p2.matcher(value);
		if(m1.find()) t1 = m1.start();
		if(m2.find()) t2 = m2.start();
		temp = Integer.parseInt(value.substring(t1+1, t2));
		return temp;
    }
	
}
