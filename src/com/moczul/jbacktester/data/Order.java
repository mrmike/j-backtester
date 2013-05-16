package com.moczul.jbacktester.data;

public class Order {
	
	private double mOpenPrice;
	private int mAmount;
	private long mOpenTime;
	private String mStockName;
	
	public Order(double openPrice, int amount, long openTime, String stockName) {
		mOpenPrice = openPrice;
		mAmount = amount;
		mOpenTime = openTime;
		mStockName = stockName;
	}
	
	public double getOpenPrice() {
		return mOpenPrice;
	}
	
	public int getAmount() {
		return mAmount;
	}
	
	public long getOpenTime() {
		return mOpenTime;
	}
	
	public String getStockName() {
		return mStockName;
	}

}
