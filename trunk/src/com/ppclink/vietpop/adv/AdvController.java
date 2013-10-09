/**
 * 
 */
package com.ppclink.vietpop.adv;

import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author CHIEN NGUYEN
 *
 */
public class AdvController {
	
	static ArrayList<ProductInfoAdv> listProductAdv;    //list chua thong tin san pham can quang cao
	public final static String NAME_FILE_CONFIG = "adv_config.xml";   //ten file config
	public final static String PATH_FILE_CONFIG = "http://www.vietdorje.com/uploads/product_data/vietradio/adv/android/"; //url file config tren server
	public final static String PATH_IMAGE = "http://www.vietdorje.com/uploads/product_data/vietradio/adv/android/images/"; //url file image quang cao tren server
	public final static String PATH_IMAGE_STORAGE = "data/data/com.ppclink.vietpop.activity/adv/images/";
	public final static String PATH_ADV_DEFAULT = "http://itunes.apple.com/us/app/hicalc-your-trusted-calculator/id442665708?mt=8";
	public final static int MAX_TIME_SHOW_ADV = 60000;
	static int intervalUpdateConfig;    				//thoi gian update laj file config
	static int intervalUpdateProduct;  				// khoang thoi gian chuyen giua cac san pham quang cao
	static int percentShowAdv;							//phan tram hien thi quang cao cua minh
	static boolean isUpdateSuccess = false;
	
	//phuong thuc update file config
	public static boolean updateFileConfig(){
		
		listProductAdv = new ArrayList<ProductInfoAdv>();
		try {
			URL urlAdvConfig = new URL(PATH_FILE_CONFIG + NAME_FILE_CONFIG);  //url cua file config
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(urlAdvConfig.openStream());
			Element rootElement = doc.getDocumentElement();
			NodeList listNode = rootElement.getChildNodes();
			for (int i = 0; i < listNode.getLength(); i++){
				if(listNode.item(i) instanceof Element){
					Element element = (Element) listNode.item(i);
					//neu la the IntervalUpdateConfig
					if(element.getNodeName().equals("IntervalUpdateConfig")){
						intervalUpdateConfig = Integer.parseInt(element.getTextContent());
					}
					//neu la the IntervalUpdateProduct
					else if(element.getNodeName().equals("IntervalUpdateProduct")){
						intervalUpdateProduct = Integer.parseInt(element.getTextContent());
					}
					//neu la the percentshowADV
					else if(element.getNodeName().equals("PercentShowADV")){
						percentShowAdv = Integer.parseInt(element.getTextContent());
					}
					//neu la the product
					else if(element.getNodeName().equals("Product")){
						NodeList listP = element.getChildNodes();
						for (int j = 0; j < listP.getLength(); j++) {
							if(listP.item(j) instanceof Element){
								Element product = (Element) listP.item(j);  //the san pham
								NodeList listT = product.getChildNodes();
								ProductInfoAdv productInfo = new ProductInfoAdv();
								for (int k = 0; k < listT.getLength(); k++) {
									if(listT.item(k) instanceof Element){
										Element properties = (Element) listT.item(k);  // các thẻ thuộc tính sản phẩm
										//kiem tra xem có cho sản phẩm này quảng cáo không
										if(properties.getAttribute("name").equals("enable")) productInfo.setEnable(Boolean.parseBoolean(properties.getTextContent()));
										else if(properties.getAttribute("name").equals("name_image")) productInfo.setNameImage(properties.getTextContent());
										else if(properties.getAttribute("name").equals("link_product_adv")) productInfo.setLinkProductAdv(properties.getTextContent());
									}
								}
								productInfo.setName(product.getAttribute("name"));
								if((productInfo.isEnable()==true)&&(downloadImageAdv(PATH_IMAGE + productInfo.getNameImage(), productInfo.getNameImage()))) listProductAdv.add(productInfo);
							}
						}
					}
				}
			}
			isUpdateSuccess = true;
		} catch (Exception e) {
			isUpdateSuccess = false;
			e.printStackTrace();
		}
		return isUpdateSuccess;
	}
	
	//download anh tu server
	public static boolean downloadImageAdv(String url,String nameImage){
		boolean isSuccess = true;
		String pathImageStorage = PATH_IMAGE_STORAGE + nameImage;   //đường dẫn đến file lưu anh
		try {
			File Dir = new File(PATH_IMAGE_STORAGE);					 
			Dir.mkdirs();				//tạo thư mục chứa image
			File fileImage = new File(pathImageStorage);
			if(!fileImage.exists()){  //kiem tra neu file da ton tai se khong down ve nua
				fileImage.createNewFile();
				
				URL urlConnect = new URL(url);
				HttpURLConnection con = (HttpURLConnection)urlConnect.openConnection();
				con.setDoOutput(true);
				con.setRequestMethod("GET");
				con.connect();
				
				FileOutputStream fos = new FileOutputStream(fileImage);
				InputStream is = con.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len =  is.read(buffer)) != -1) {
					fos.write(buffer,0,len);
					fos.flush();
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			isSuccess = false;
			e.printStackTrace();
		}
		return isSuccess;
	}
}
