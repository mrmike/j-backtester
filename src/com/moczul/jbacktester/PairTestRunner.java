package com.moczul.jbacktester;

import java.util.ArrayList;
import java.util.List;

import com.moczul.jbacktester.interfaces.MarketDataSourceable;

public class PairTestRunner {

	private MarketDataSourceable mFeed;
	private MarketDataSourceable mPairFeed;

	private ArrayList<Double> mPrices;
	private ArrayList<Double> mPairPrices;
	private ArrayList<Double> mNormalizePrices;
	private ArrayList<Double> mNormalizePairPrices;
	private ArrayList<Double> mDiff;
	private ArrayList<Double> mStdDev;

	private boolean mLongTrade = false;
	private boolean mShortTrade = false;
	
	private static final int DEFAULT_PERIOD = 252;

	PairTestRunner(MarketDataSourceable feed, MarketDataSourceable pairFeed) {
		mFeed = feed;
		mPairFeed = pairFeed;

		mPrices = new ArrayList<Double>();
		mPairPrices = new ArrayList<Double>();
		mNormalizePrices = new ArrayList<Double>();
		mNormalizePairPrices = new ArrayList<Double>();
		mDiff = new ArrayList<Double>();
		mStdDev = new ArrayList<Double>();

		initData();
	}

	private void initData() {
		for (int i = 0; i < DEFAULT_PERIOD; i++) {
			mPrices.add(mFeed.getMarketEvent(i).getClosePrice());
			mPairPrices.add(mPairFeed.getMarketEvent(i).getClosePrice());
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

	public void startBackTest() {
		int size = mFeed.getSize();
		if (size != mPairFeed.getSize()) {
			throw new RuntimeException("Both feed should have the same length!");
		}

		for (int i = DEFAULT_PERIOD; i < size; i++) {
			double price = mFeed.getMarketEvent(i).getClosePrice();
			double pairPrice = mPairFeed.getMarketEvent(i).getClosePrice();
			addNormPrices(price, pairPrice);

			double lastDiff = mDiff.get(mDiff.size() - 1);
			double stdDev = mStdDev.get(mStdDev.size() - 1);
			
			if (mLongTrade) {
				if (lastDiff > 0) {
					System.out.println("Close long trade at price: " + price + " and pair price: " + pairPrice);
					mLongTrade = false;
					continue;
				}
			} else if (mShortTrade) {
				if (lastDiff < 0) {
					System.out.println("Close short trade at price: " + price + " and pair price: " + pairPrice);
					mShortTrade = false;
					continue;
				}
			}
			
			if (mLongTrade || mShortTrade) {
				continue;
			}
			
			if (lastDiff < -1.5 * stdDev) {
				System.out.println("Open long trade at price: " + price + " and pair price: " + pairPrice);
				mLongTrade = true;
			} else if (lastDiff > 1.5 * stdDev) {
				System.out.println("Open short trade at price: " + price + " and pair price: " + pairPrice);
				mShortTrade = true;
			}
		}
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

}
