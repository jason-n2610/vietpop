/**
 * 
 */
package com.ppclink.vietpop.adv;



import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.ppclink.vietpop.activity.R;
import com.ppclink.vietpop.widget.DisplayMode;
import com.ppclink.vietpop.widget.Rotate;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * @author CHIEN NGUYEN
 *
 */
public class AdvViewGroup extends FrameLayout implements AdvInterface{
	
	int timeShowAdv; 							//thoi gian hien thi quang cao cua minh
	int timeExchangeStyle=-1;   				//bien de thay doi style hien thi anh quang cao
	Handler runAdvHandler = new Handler();	 	//handler dung goi su kien chuyen quang cao
	Handler switchAdvHandler = new Handler(); 	//handler dung goi su kien doi quang cao google va cua minh
	Handler updateConfigHandler = new Handler();  //handler dung de update file config
	AdView googleAdv;
	ViewFlipper myAdv;
	ViewGroup container;
	boolean state = true; 						// trang thai hien thi cua quang cao true = hien thi quang cao cua minh
    public boolean isPlaying = false ;   				
	
	public AdvViewGroup(Context context) {
		super(context);
		initAdvViewGroup();
	}
	
	//ham khoi tao trong xml
	public AdvViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAdvViewGroup();
	}
	
	//ham khoi tao thanh phan cua lop
	public void initAdvViewGroup(){
		LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (DisplayMode.mode == 1) inflater.inflate(R.layout.adv_view_320x480, this, true);
		else if (DisplayMode.mode == 2) inflater.inflate(R.layout.adv_view_480x800, this, true);
		container = (ViewGroup) findViewById(R.id.container);
		googleAdv = (AdView) findViewById(R.id.adView);
		myAdv = (ViewFlipper) findViewById(R.id.viewFlipper1);
	}
	
	// start ADV
	public void start(){
		isPlaying = true;
		//start quang cao cua google
		AdRequest request = new AdRequest();
		request.setTesting(true);
		googleAdv.loadAd(request);
		if (AdvController.isUpdateSuccess) { //neu update file cau hinh thanh cong
			showAdv();
			updateConfigHandler.postDelayed(updateConfig, AdvController.intervalUpdateConfig);
		}else{  
			myAdv.setVisibility(View.GONE);
		}
	}
	
	//ham thuc hien hien thi quang cao
	public void showAdv(){
		timeExchangeStyle = -1;  
		state = true;   //hien thi quang cao cua minh truoc
		timeShowAdv = AdvController.MAX_TIME_SHOW_ADV*AdvController.percentShowAdv/100; //thoi gian hien thi quang cao cua minh trong tong so 50(s)
		myAdv.removeAllViews(); //xoa het anh quang cao cu
		
		//set su kien cho nhung anh quang cao
		int length1 = AdvController.listProductAdv.size();
		for (int i = 0; i < length1; i++) {
			final int t = i; 
			ImageView iv = new ImageView(getContext());
			iv.setImageBitmap(BitmapFactory.decodeFile(AdvController.PATH_IMAGE_STORAGE + AdvController.listProductAdv.get(t).getNameImage()));
			iv.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					getContext().startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(AdvController.listProductAdv.get(t).getLinkProductAdv())));
				}
			});
			myAdv.addView(iv);
		}
		if(timeShowAdv ==AdvController.MAX_TIME_SHOW_ADV){  //neu thoi gian hien thi quang cao cua minh la 100%
			googleAdv.setVisibility(View.GONE);
			if(AdvController.listProductAdv.size() !=0){
				if(AdvController.listProductAdv.size()!=1){		//neu co it nhat 2 quang cao thi se thuc hien chuyen
					runAdvHandler.removeCallbacks(runAdv);
					runAdvHandler.postDelayed(runAdv, AdvController.intervalUpdateProduct);
				}
				
			}else{
				myAdv.setVisibility(View.GONE);
			}
			
		}
		else if(timeShowAdv==0){
			myAdv.setVisibility(View.GONE);
		}
		
		else{
			if(AdvController.listProductAdv.size()!=0){ 				//neu co san pham quang cao
				googleAdv.setVisibility(View.GONE);		 //an quang cao cua google
				if(AdvController.listProductAdv.size()!=1){		//neu co it nhat 2 quang cao thi se thuc hien chuyen
					runAdvHandler.removeCallbacks(runAdv);
					runAdvHandler.postDelayed(runAdv, AdvController.intervalUpdateProduct);
				}
				switchAdvHandler.removeCallbacks(switchAdv);
				switchAdvHandler.postDelayed(switchAdv, timeShowAdv);
			}
			else{  				//neu minh khong co san pham nao quang cao
				myAdv.setVisibility(View.GONE);
            }
		}
	}
	
	//tam dung hien thi quang cao
	public void pauseAdv(){
		runAdvHandler.removeCallbacks(runAdv);
		switchAdvHandler.removeCallbacks(switchAdv);
	}
	
	//dung quang cao , dung update
	public void stopAdv(){
		isPlaying = false;
		pauseAdv();
		updateConfigHandler.removeCallbacks(updateConfig);
	}
	
	//hien thi quang cao
	Runnable runAdv = new Runnable() {
		
		
		public void run() {
			timeExchangeStyle++;
			if(timeExchangeStyle==6) timeExchangeStyle = -1;
			if(timeExchangeStyle<3){
				myAdv.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in));
				myAdv.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_left_out));
			}
			if(timeExchangeStyle >= 3){
				myAdv.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in));
				myAdv.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.push_up_out));
			}
			myAdv.showNext();
			runAdvHandler.postDelayed(runAdv, AdvController.intervalUpdateProduct);
		}
	};
	
	//chuyen quang cao
	Runnable switchAdv = new Runnable() {
		
		
		public void run() {
			if (state) {
				applyRotation(1, 0, 90);
				runAdvHandler.removeCallbacks(runAdv); //ngung chuyen anh quang cao
				state = !state;
				switchAdvHandler.postDelayed(switchAdv, AdvController.MAX_TIME_SHOW_ADV-timeShowAdv);
			}
			else {
				applyRotation(0, 0, 90);
				runAdvHandler.postDelayed(runAdv, AdvController.intervalUpdateProduct);  //chay quang cao cua minh
				state = !state;
				switchAdvHandler.postDelayed(switchAdv, timeShowAdv);
			}
		}
	};
	
	//update file config
	Runnable updateConfig = new Runnable() {
		
		public void run() {
			pauseAdv();  	//tam dung quang cao
			AdvTask advTask = new AdvTask();   
			advTask.updateAdv(AdvViewGroup.this);	//hien thi lai quang cao
		}
	};
	
	
	//ham thuc hien viec quay
	public void applyRotation(int position, float start, float end) {
        final float centerX = container.getWidth() / 2.0f;
        final float centerY = container.getHeight() / 2.0f;
        final Rotate rotation =
                new Rotate(start, end, centerX, centerY);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(position));
        container.startAnimation(rotation);
    }
    
    private final class DisplayNextView implements Animation.AnimationListener {
        private final int mPosition;
        
        private DisplayNextView(int position) {
            mPosition = position;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            container.post(new SwapViews(mPosition));
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    private final class SwapViews implements Runnable {
        private final int mPosition;

        public SwapViews(int position) {
            mPosition = position;
        }

        public void run() {
            final float centerX = container.getWidth() / 2.0f;
            final float centerY = container.getHeight() / 2.0f;
            Rotate rotation;            
            if (mPosition > 0) {  //chuyen sang quang cao cua google
            	myAdv.setVisibility(View.GONE);
            	googleAdv.setVisibility(View.VISIBLE);
            	rotation = new Rotate(90, 0, centerX, centerY);
            } else { // chuyen sang quang cao cua minh
            	myAdv.setVisibility(View.VISIBLE);
                googleAdv.setVisibility(View.GONE);
            	rotation = new Rotate(90, 0, centerX, centerY);
            }
            rotation.setDuration(500);
            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            container.startAnimation(rotation);
        }
    }
    
    public void onCompleteAdv(AdvAction action) {
		switch (action) {
		case START_ADV:
			this.start();
			break;
		case UPDATE_ADV:
			if (AdvController.isUpdateSuccess) {  //update lai cau hinh thanh cong
				if(!state&&(AdvController.listProductAdv.size()!=0)){
					myAdv.setVisibility(View.VISIBLE);
					googleAdv.setVisibility(View.GONE);
				}
				showAdv();
				updateConfigHandler.postDelayed(updateConfig, AdvController.intervalUpdateConfig);
			}else{
				myAdv.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
		
	}
}
