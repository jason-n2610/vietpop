package com.ppclink.vietpop.radio;

public interface RadioInterface {
	public void onComplete(int result);
	public void onStarted(int result);
	public void onError(int result);
	public void onTimeOut(int result);
}
