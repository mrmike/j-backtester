package com.moczul.jbacktester.data;

import java.awt.MediaTracker;
import java.util.ArrayList;
import java.util.List;

import com.moczul.jbacktester.StooqFeed;
import com.moczul.jbacktester.StrategyTester;
import com.moczul.jbacktester.data.Trade.TradeType;

public class PairStock {

	private static final int STOP_LOSS = -15;
	private static final int TAKE_PROFIT = 30;

	private static final int DEFAULT_PERIOD = 252;

	private String mFirstName;
	private String mSecondName;

	private StooqFeed mFirstData;
	private StooqFeed mSecondData;

	private boolean mCanOpenShortTrade = true;
	private boolean mCanOpenLongTrade = true;

	private boolean mLongTrade = false;
	private boolean mShortTrade = false;
	
	private ArrayList<Double> mPrices;
	private ArrayList<Double> mPairPrices;
	private ArrayList<Double> mNormalizePrices;
	private ArrayList<Double> mNormalizePairPrices;
	private ArrayList<Double> mDiff;
	private ArrayList<Double> mStdDev;
	private ArrayList<Trade> mTrades;
	private ArrayList<Integer> mOpenPoints;
	private ArrayList<Integer> mClosePoints;

	private int mCurrentItem = DEFAULT_PERIOD - 1;

	private int mSize = 0;

	public PairStock(String aName, String bName, StooqFeed aData,
			StooqFeed bData) {
		mFirstName = aName;
		mSecondName = bName;

		mFirstData = aData;
		mSecondData = bData;

		if (aData.getSize() > bData.getSize()) {
			mSize = bData.getSize();
		} else {
			mSize = aData.getSize();
		}

		init();
	}

	public int getSize() {
		return mSize;
	}

	private void init() {
		mPrices = new ArrayList<Double>();
		mPairPrices = new ArrayList<Double>();
		mNormalizePrices = new ArrayList<Double>();
		mNormalizePairPrices = new ArrayList<Double>();
		mDiff = new ArrayList<Double>();
		mStdDev = new ArrayList<Double>();
		mTrades = new ArrayList<Trade>();
		mOpenPoints = new ArrayList<Integer>();
		mClosePoints = new ArrayList<Integer>();

		for (int i = 0; i < DEFAULT_PERIOD; i++) {
			mPrices.add(mFirstData.getMarketEvent(i).getClosePrice());
			mPairPrices.add(mSecondData.getMarketEvent(i).getClosePrice());
		}

		double avg = getAverage(DEFAULT_PERIOD, mPrices);
		double stdDev = getStdDev(DEFAULT_PERIOD, mPrices);

		for (Double price : mPrices) {
			double normPrice = (price - avg) / stdDev;
			mNormalizePrices.add(normPrice);
		}

		avg = getAverage(DEFAULT_PERIOD, mPairPrices);
		stdDev = getStdDev(DEFAULT_PERIOD, mPairPrices);
		for (Double price : mPairPrices) {
			double normPrice = (price - avg) / stdDev;
			mNormalizePairPrices.add(normPrice);
		}

		for (int i = 0; i < mNormalizePrices.size(); i++) {
			double price = mNormalizePrices.get(i);
			double pairPrice = mNormalizePairPrices.get(i);
			mDiff.add(price - pairPrice);
		}

		stdDev = getStdDev(mDiff.size(), mDiff);
		for (int i = 0; i < DEFAULT_PERIOD; i++) {
			mStdDev.add(stdDev);
		}
	}

	public TradeType canBeTraded() {
		if (mLongTrade || mShortTrade) {
			return TradeType.EMPTY;
		}

		double lastDiff = mDiff.get(mDiff.size() - 1);
		double stdDev = mStdDev.get(mStdDev.size() - 1);

		if (lastDiff < -1.5 * stdDev && mCanOpenLongTrade) {
			return TradeType.LONG;
		} else if (lastDiff > 1.5 * stdDev && mCanOpenShortTrade) {
			return TradeType.SHORT;
		}

		return TradeType.EMPTY;
	}
	
	public double getCurrentValue() {
		if (!mLongTrade && !mShortTrade) {
			return 0;
		}
		
		double price = mPrices.get(mCurrentItem);
		double pairPrice = mPairPrices.get(mCurrentItem);
		Trade t = mTrades.get(mTrades.size() - 1);
		if (mLongTrade) {
			return t.getCurrentValue(pairPrice, price);
		} else if (mShortTrade) {
			return t.getCurrentValue(price, pairPrice);
		}
		
		return 0;
	}

	public void openLongTrade(int longQty, int shortQty, double escrow) {
		double price = mPrices.get(mCurrentItem);
		double pairPrice = mPairPrices.get(mCurrentItem);
		mTrades.add(new Trade(pairPrice, shortQty, price, longQty, escrow, mCurrentItem));
		mLongTrade = true;
		mOpenPoints.add(mCurrentItem);
		System.out.println("Long trade: " + price + " for " + longQty + " And short " + pairPrice + " for : " + shortQty);
	}

	public void openShortTrade(int longQty, int shortQty, double escrow) {
		double price = mPrices.get(mCurrentItem);
		double pairPrice = mPairPrices.get(mCurrentItem);
		mTrades.add(new Trade(price, shortQty, pairPrice, longQty, escrow, mCurrentItem));
		mShortTrade = true;
		mOpenPoints.add(mCurrentItem);
		System.out.println("Short trade: " + price + " for: " + shortQty + " and long: " + pairPrice + " for " + longQty);
	}
	
	public double getTotalReturn() {
		double roi = 0;
		for (Trade t : mTrades) {
			roi += t.getTotalReturn();
		}
		
		return roi;
	}

	public int getCurrentItem() {
		return mCurrentItem;
	}

	public double getFirstPrice() {
		return mPrices.get(mCurrentItem);
	}

	public double getSecondPrice() {
		return mPairPrices.get(mCurrentItem);
	}
	
	public double moveToNext() {
		if (mCurrentItem + 1 == mSize) {
			return 0;
		}
		
		mCurrentItem++;

		double price = mFirstData.getMarketEvent(mCurrentItem).getClosePrice();
		double pairPrice = mSecondData.getMarketEvent(mCurrentItem)
				.getClosePrice();
		addNormPrices(price, pairPrice);

		double lastDiff = mDiff.get(mDiff.size() - 1);
		double stdDev = mStdDev.get(mStdDev.size() - 1);

		if (Math.abs(lastDiff) < 0.1 * stdDev) {
			mCanOpenLongTrade = true;
			mCanOpenShortTrade = true;
		}

		if (mLongTrade) {
			if (lastDiff > 0 || isOverLimit(pairPrice, price)) {
				System.out.println("Close long trade at price: " + price
						+ " and pair price: " + pairPrice);
				Trade lastTrade = mTrades.get(mTrades.size() - 1);
				lastTrade.closeTrade(pairPrice, price, mCurrentItem);
				mLongTrade = false;
				mCanOpenLongTrade = false;
				mClosePoints.add(mCurrentItem);
				return lastTrade.getCloseValue();
			}
		} else if (mShortTrade) {
			if (lastDiff < 0 || isOverLimit(price, pairPrice)) {
				System.out.println("Close short trade at price: " + price
						+ " and pair price: " + pairPrice);
				Trade lastTrade = mTrades.get(mTrades.size() - 1);
				lastTrade.closeTrade(price, pairPrice, mCurrentItem);
				mShortTrade = false;
				mCanOpenShortTrade = false;
				mClosePoints.add(mCurrentItem);
				return lastTrade.getCloseValue();
			}
		}

		return 0;
	}
	
	private double getAverage(int period, List<Double> prices) {
		int size = prices.size();
		double sum = 0;
		for (int i = period; i > 0; i--) {
			sum += prices.get(size - i);
		}

		return sum / period;
	}

	private double getStdDev(int period, List<Double> prices) {
		int size = prices.size();
		double sum = 0;
		double average = getAverage(period, prices);
		for (int i = period; i > 0; i--) {
			sum += Math.pow(prices.get(size - i) - average, 2);
		}

		return Math.pow(sum / period, 0.5);
	}

	private void addNormPrices(double price, double pairPrice) {
		mPrices.add(price);
		mPairPrices.add(pairPrice);
		double avg = getAverage(DEFAULT_PERIOD, mPrices);
		double stdDev = getStdDev(DEFAULT_PERIOD, mPrices);
		double normPrice = (price - avg) / stdDev;
		mNormalizePrices.add(normPrice);

		avg = getAverage(DEFAULT_PERIOD, mPairPrices);
		stdDev = getStdDev(DEFAULT_PERIOD, mPairPrices);
		double normPairPrice = (pairPrice - avg) / stdDev;
		mNormalizePairPrices.add(normPairPrice);

		mDiff.add(normPrice - normPairPrice);
		mStdDev.add(getStdDev(mDiff.size(), mDiff));
	}

	private boolean isOverLimit(double shortPrice, double longPrice) {
		Trade lastTrade = mTrades.get(mTrades.size() - 1);
		double roi = lastTrade.getCurrentReturn(shortPrice, longPrice);
		if (roi > TAKE_PROFIT) {
			System.out.println("TAKE_PROFIT: " + roi);
			return true;
		} else if (roi < STOP_LOSS) {
			System.out.println("STOP_LOSS: " + roi);
			return true;
		}

		return false;
	}

}
