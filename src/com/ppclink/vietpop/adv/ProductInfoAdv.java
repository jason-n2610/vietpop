/**
 * 
 */
package com.ppclink.vietpop.adv;

/**
 * @author CHIEN NGUYEN
 *
 */
public class ProductInfoAdv {
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameImage() {
		return nameImage;
	}
	public void setNameImage(String nameImage) {
		this.nameImage = nameImage;
	}
	public String getLinkProductAdv() {
		return linkProductAdv;
	}
	public void setLinkProductAdv(String linkImage) {
		this.linkProductAdv = linkImage;
	}
	private String name;
	private boolean enable;
	private String nameImage;
	private String linkProductAdv;
	
}
