package com.ppclink.vietpop.data;

import java.io.File;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ppclink.vietpop.data.Constant.DownloadState;

public class FileDownload extends AsyncTask<String, Integer, String> {


	public 	FileDownloadInterface 	delegate;	// The hien cho Activity su dung class 
	private int 					tag;		// The hien nhu id cua moi class FileDownload
	private	DownloadState 			state;		// Trang thai download
	private ProgressBar 			progress;
	private TextView 				tvDownloadInfo, tvArtistName;
	private ImageButton 			ibCancel, ibDownload;
	private int 					lengthOfFile;
	boolean 						cancel;		// Cancel khi dang download
	String 							link, name, dir;

	public FileDownload(String name, String link, String dir){
		this.name = name;
		this.link = link;
		this.dir = dir;
		this.state =  DownloadState.DOWNLOAD_IDLE;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public DownloadState getState() {
		return state;
	}

	public void setState(DownloadState state) {
		this.state = state;
	}
	// Update progress trong khi download
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		progress.setProgress(((values[0].intValue()*100)/lengthOfFile));
		tvDownloadInfo.setText(String.valueOf(values[0].intValue()/(1024))+"/"+String.valueOf(lengthOfFile/(1024))+" (Kb)");
	}


	@Override
	protected String doInBackground(String... params) {
		// Lay ra link, ten file va duong dan folder
		state = DownloadState.DOWNLOAD_INPROGRESS;
		if (params.length == 3) { // Nhap vao du 3 chuoi
			try {
				String fileName = params[0] + ".mp3";
				URL url1 = new URL(params[1]);
				String directory = params[2];

				// Chuyen tu doi tuong url sang link download thuc te
				HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
				connection1.setRequestMethod("GET");
				// ngan ko cho tu dong redirect
				connection1.setInstanceFollowRedirects(false); 	
				connection1.connect();
				// link download thuc te nam trong header name = location
				String location = connection1.getHeaderField("location"); 	
				location = location.replace(" ", "%20"); // Thay the dau " " bang "%20"
				URL url2 = new URL(location); // Link can lay de thuc hien get
												// inputstream
				HttpURLConnection connection2 = (HttpURLConnection) url2
						.openConnection();
				connection2.setDoOutput(true);
				connection2.setRequestMethod("GET");
				connection2.connect();
				// Lay ve tong dung luong file
				lengthOfFile = connection2.getContentLength();
				int total = 0; // Phan dung luong file da download ve
				// Ghi file
				File file = new File(directory, fileName);
				FileOutputStream output = new FileOutputStream(file);
				InputStream input = connection2.getInputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = input.read(buffer)) != -1) {
					total += len;
					publishProgress(total); 	// Phan tram download
					if (cancel){
						state = DownloadState.DOWNLOAD_CANCELED;
						break;
					}
					output.write(buffer, 0, len);
				}
				output.flush();
				output.close();
				input.close();
				state = DownloadState.DOWNLOAD_COMPLETE;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { // Ko nhap du so chuoi
			
		}
		return null;
	}

	// Ham thuc hien khi download xong
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (cancel){
			state = DownloadState.DOWNLOAD_CANCELED;
			File file = new File(dir, name+".mp3");
			if(file.exists()){
				file.delete();
			};			
		};
		// update activity
		if (delegate!=null)
			delegate.onFinishDownload(this);
		progress.setProgress(0);
		progress.setVisibility(ProgressBar.GONE);
		tvDownloadInfo.setVisibility(TextView.GONE);
		ibCancel.setVisibility(ImageButton.GONE);
		ibDownload.setVisibility(ImageButton.VISIBLE);
		tvArtistName.setVisibility(ImageButton.VISIBLE);
	}
	
	// Phuong thuc download
	public void download(ProgressBar progress, TextView tvDownload, ImageButton ibCancel, ImageButton ibDownload, TextView tvArtist){
		this.execute(this.name, this.link, this.dir);
		this.progress = progress;
		this.tvDownloadInfo = tvDownload;
		this.ibCancel = ibCancel;
		this.tvArtistName = tvArtist;
		this.ibDownload = ibDownload;
	}
	
	public void cancelDownload(boolean cancel) {
		this.cancel = cancel;
	}
	public void init(ProgressBar progress, TextView tvDownload, ImageButton ibCancel, ImageButton ibDownload, TextView tvArtist){
		this.progress = progress;
		this.tvDownloadInfo = tvDownload;
		this.ibCancel = ibCancel;
		this.tvArtistName = tvArtist;
		this.ibDownload = ibDownload;
	}
	public void start(){
		this.execute(this.name, this.link, this.dir);
	}
}
