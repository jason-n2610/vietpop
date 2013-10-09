package com.ppclink.vietpop.radio;

public class Channel {
	private String id;
	private String name;
	private String description;
	private String link;
	private String host;
	private String url;
	private String playpath;
	private String app;
	private String port;
	private String iconname;
	private String status;
	
	//---constructor---
	public Channel(String id, String name, String description, String link, String host, String url, String playpath, String app, String port, String iconname, String status) {
		this.id = id;
		this.name = name;
		this.setDescription(description);
		this.link = link;
		this.host = host;
		this.url = url;
		this.playpath = playpath;
		this.app = app;
		this.port = port;
		this.setIconname(iconname);
		this.status = status;
	}
	
	public Channel() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPlayPath() {
		return playpath;
	}
	public void setPlayPath(String playpath) {
		this.playpath = playpath;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconname() {
		return iconname;
	}

	public void setIconname(String iconname) {
		this.iconname = iconname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
