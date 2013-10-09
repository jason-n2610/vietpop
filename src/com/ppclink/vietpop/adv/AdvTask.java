
package com.ppclink.vietpop.adv;

import android.os.AsyncTask;


/**
 * @author CHIEN NGUYEN
 *
 */
public class AdvTask extends AsyncTask<AdvInterface, Void, AdvAction> {
	AdvInterface delegate;
	AdvAction action;
	@Override
	protected void onPostExecute(AdvAction result) {
		if(delegate instanceof AdvViewGroup){
			((AdvViewGroup) delegate).onCompleteAdv(result);
		}
		super.onPostExecute(result);
	}

	@Override
	protected AdvAction doInBackground(AdvInterface... arg0) {
		delegate = arg0[0];
		AdvController.updateFileConfig();
		return action;
	}
	
	public void startAdv(AdvInterface advView){
		action = AdvAction.START_ADV;
		this.execute(advView);
	}
	
	public void updateAdv(AdvInterface advView){
		action = AdvAction.UPDATE_ADV;
		this.execute(advView);
	}

}
