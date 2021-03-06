package com.moczul.jbacktester.data;

import com.moczul.jbacktester.StrategyTester;

public class Trade {

	public enum TradeType {
		SHORT("short"), LONG("long"), EMPTY("empty"), CLOSE("close");

		private String mType;

		TradeType(String type) {
			mType = type;
		}

		@Override
		public String toString() {
			return mType;
		}

	}

	private double mShortOpenPrice;
	private double mShortClosePrice;
	private double mLongOpenPrice;
	private double mLongClosePrice;
	private double mEscrow;
	private int mStartPoint;
	private int mEndPoint;

	private int mShortQty;
	private int mLongQty;

	private boolean mIsOpen;

	private TradeType mType;

	public Trade(double shortPrice, int shortQty, double longPrice,
			int longQty, double escrow, int startPoint, TradeType type) {
		mIsOpen = true;
		mShortOpenPrice = shortPrice;
		mShortQty = shortQty;
		mLongOpenPrice = longPrice;
		mLongQty = longQty;
		mEscrow = escrow;
		mStartPoint = startPoint;
		mType = type;
	}
	
	public TradeType getType() {
		return mType;
	}
	
	public double getShortOpenPrice() {
		return mShortOpenPrice;
	}

	public double getShortClosePrice() {
		return mShortClosePrice;
	}

	public double getLongClosePrice() {
		return mLongClosePrice;
	}

	public int getShortQty() {
		return mShortQty;
	}

	public int getLongQty() {
		return mLongQty;
	}

	public double getLongOpenPrice() {
		return mLongOpenPrice;
	}

	public boolean isOpen() {
		return mIsOpen;
	}

	public double getEscrow() {
		return mEscrow;
	}
	
	public int getStartPoint() {
		return mStartPoint;
	}

	public int getEndPoint() {
		return mEndPoint;
	}
	
	public void closeTrade(double shortPrice, double longPrice, int endPoint) {
		mShortClosePrice = shortPrice;
		mLongClosePrice = longPrice;
		mIsOpen = false;
		mEndPoint = endPoint;
	}

	public int getTradeLength() {
		return mEndPoint - mStartPoint;
	}

	private double getShortReturn(double openPrice, double closePrice) {
		double roi = (openPrice - closePrice) / openPrice;
		return roi * 100;
	}

	private double getLongReturn(double openPrice, double closePrice) {
		double roi = (closePrice - openPrice) / openPrice;
		return roi * 100;
	}

	public double getTotalReturn() {
		return getLongReturn() + getShortReturn();
	}

	public double getLongReturn() {
		return getLongReturn(mLongOpenPrice, mLongClosePrice);
	}

	public double getShortReturn() {
		return getShortReturn(mShortOpenPrice, mShortClosePrice);
	}

	public double getCurrentReturn(double shortPirce, double longPrice) {
		double sRoi = getShortReturn(mShortOpenPrice, shortPirce);
		double lRoi = getLongReturn(mLongOpenPrice, longPrice);
		return sRoi + lRoi;
	}

	public double getCurrentValue(double shortPrice, double longPrice) {
		double longVal = longPrice * mLongQty;
		double shortVal = shortPrice * mShortQty; // tyle oddajemy
		
		double r = longVal - shortVal;
		return mEscrow + r;
	}

	public double getCloseValue() {
		return getCloseValue(StrategyTester.COMMISSION,
				StrategyTester.INTEREST_RATE);
	}

	public double getCloseValue(double commission, double interestRate) {
		double longCommision = mLongClosePrice * mLongQty * commission;
		double shortCommission = mShortClosePrice * mShortQty * commission;
		double shortRate = mEscrow * interestRate * getTradeLength() / 365;

		double val = getCurrentValue(mShortClosePrice, mLongClosePrice);
		val -= longCommision;
		val -= shortCommission;
		val -= shortRate;

		return val;
	}
}
