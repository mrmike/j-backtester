package com.moczul.jbacktester.data;

public class MarketEvent {
	
	private double mOpenPrice;
	private double mClosePrice;
	private long mVolume;
	
	public MarketEvent(double openPrice, double closePrice, long volume) {
		mOpenPrice = openPrice;
		mClosePrice = closePrice;
		mVolume = volume;
	}
	
	public double getOpenPrice() {
		return mOpenPrice;
	}
	
	public double getClosePrice() {
		return mClosePrice;
	}
	
	public long getVolume() {
		return mVolume;
	}

}
