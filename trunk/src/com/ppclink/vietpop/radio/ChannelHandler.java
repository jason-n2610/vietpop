package com.ppclink.vietpop.radio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ppclink.vietpop.radio.Channel;

public class ChannelHandler extends DefaultHandler {
	private ArrayList<Channel> lstChannel = new ArrayList<Channel>();
	private Channel aChannel;
	private boolean in_id = false;
	private boolean in_channel = false;
	private boolean in_name = false;
	private boolean in_description = false;
	private boolean in_link = false;
	private boolean in_host = false;
	private boolean in_url = false;
	private boolean in_playpath = false;
	private boolean in_app = false;
	private boolean in_port = false;
	private boolean in_iconname = false;
	private boolean in_status = false;
	//---constructor
	public ChannelHandler() {
		
	}
	
	public ArrayList<Channel> getChannels() {
		return this.lstChannel;
	}
	
	@Override
	public void startDocument() throws SAXException {
		
	}
	
	@Override
	public void endDocument() throws SAXException {
		//---nothing to do
	}
	
	@Override
	public void startElement(String n, String l, String q, Attributes a) throws SAXException {
		if(l.equals("channel")) {
			this.in_channel = true;
			this.aChannel = new Channel();
		} 
		else if(l.equals("id")) this.in_id = true;
		else if (l.equals("name")) this.in_name = true;
		else if (l.equals("description")) this.in_description = true;
		else if (l.equals("link")) this.in_link = true;
		else if (l.equals("host")) this.in_host = true;
		else if (l.equals("url")) this.in_url = true;
		else if (l.equals("playpath")) this.in_playpath = true;
		else if (l.equals("app")) this.in_app = true;
		else if (l.equals("port")) this.in_port = true;
		else if (l.equals("iconname")) this.in_iconname = true;
		else if(l.equals("status")) this.in_status = true;
	}
	
	@Override
	public void endElement(String n, String l, String q) throws SAXException {
		if(l.equals("channel")) {
			this.lstChannel.add(aChannel);
			this.in_channel = false;
		}
		else if(l.equals("id")) this.in_id = false;
		else if (l.equals("name")) this.in_name = false;
		else if (l.equals("description")) this.in_description = false;
		else if (l.equals("link")) this.in_link = false;
		else if (l.equals("host")) this.in_host = false;
		else if (l.equals("url")) this.in_url = false;
		else if (l.equals("playpath")) this.in_playpath = false;
		else if (l.equals("app")) this.in_app = false;
		else if (l.equals("port")) this.in_port = false;
		else if (l.equals("iconname")) this.in_iconname = false;
		else if (l.equals("status")) this.in_status = false;
	}
	
	@Override
	public void characters(char ch[], int start, int length) {
		if(this.in_id == true && this.in_channel == true)
			this.aChannel.setId(new String(ch, start, length));
		else if(this.in_name == true && this.in_channel == true) 
			this.aChannel.setName(new String(ch,start, length));
		else if (this.in_description == true && this.in_channel == true) 
			this.aChannel.setDescription(new String(ch, start, length));
		else if (this.in_link == true && this.in_channel == true) 
			this.aChannel.setLink(new String(ch, start, length));
		else if (this.in_host == true && this.in_channel == true) 
			this.aChannel.setHost(new String(ch, start, length));
		else if (this.in_url == true && this.in_channel == true) 
			this.aChannel.setUrl(new String(ch, start, length));
		else if (this.in_playpath == true && this.in_channel == true) 
			this.aChannel.setPlayPath(new String(ch, start, length));
		else if (this.in_app == true && this.in_channel == true) 
			this.aChannel.setApp(new String(ch, start, length));
		else if (this.in_port == true && this.in_channel == true) 
			this.aChannel.setPort(new String(ch, start, length));
		else if (this.in_iconname == true && this.in_channel == true) 
			this.aChannel.setIconname(new String(ch, start, length));
		else if (this.in_status == true && this.in_channel == true) 
			this.aChannel.setStatus(new String(ch, start, length));
	}
	
	//---phuong thuc doc file xml tra ve 1 ArrayList
	public ArrayList<Channel> read(InputStream is) {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp;
		
		try {
			sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ChannelHandler handler = new ChannelHandler();
			xr.setContentHandler(handler);
			xr.parse(new InputSource(is));
			
			return handler.getChannels();
		} catch (ParserConfigurationException e) {			
		} catch (SAXException e) {			
		} catch (IOException e) {			
		}	
		
		return null;
	}
}
