package com.moczul.jbacktester.data;

public class MarketEvent {
	
	private double mOpenPrice;
	private double mClosePrice;
	private long mVolume;
	private int mPosition;
	
	public MarketEvent(double openPrice, double closePrice, long volume, int position) {
		mOpenPrice = openPrice;
		mClosePrice = closePrice;
		mVolume = volume;
		mPosition = position;
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
	
	public int getPosition() {
		return mPosition;
	}

}
