package com.moczul.jbacktester;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.moczul.jbacktester.data.Trade;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;

public class PairTestRunner {

	private static final int STOP_LOSS = -15;
	private static final int TAKE_PROFIT = 30;

	private MarketDataSourceable mFeed;
	private MarketDataSourceable mPairFeed;

	private ArrayList<Double> mPrices;
	private ArrayList<Double> mPairPrices;
	private ArrayList<Double> mNormalizePrices;
	private ArrayList<Double> mNormalizePairPrices;
	private ArrayList<Double> mDiff;
	private ArrayList<Double> mStdDev;
	private ArrayList<Trade> mTrades;
	private ArrayList<Integer> mOpenPoints;
	private ArrayList<Integer> mClosePoints;
	
	private boolean mCanOpenShortTrade = true;
	private boolean mCanOpenLongTrade = true;

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
		mTrades = new ArrayList<Trade>();
		mOpenPoints = new ArrayList<Integer>();
		mClosePoints = new ArrayList<Integer>();

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

	public double startBackTest() {
		int size = mFeed.getSize();
		if (mPairFeed.getSize() < size) {
			size = mPairFeed.getSize();
		}

		for (int i = DEFAULT_PERIOD; i < size; i++) {
			double price = mFeed.getMarketEvent(i).getClosePrice();
			double pairPrice = mPairFeed.getMarketEvent(i).getClosePrice();
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
					lastTrade.closeTrade(pairPrice, price, 0);
					mLongTrade = false;
					mCanOpenLongTrade = false;
					mClosePoints.add(i);
					continue;
				}
			} else if (mShortTrade) {
				if (lastDiff < 0 || isOverLimit(price, pairPrice)) {
					System.out.println("Close short trade at price: " + price
							+ " and pair price: " + pairPrice);
					Trade lastTrade = mTrades.get(mTrades.size() - 1);
					lastTrade.closeTrade(price, pairPrice, 0);
					mShortTrade = false;
					mCanOpenShortTrade = false;
					mClosePoints.add(i);
					continue;
				}
			}

			if (mLongTrade || mShortTrade) {
				continue;
			}

			if (lastDiff < -1.5 * stdDev && mCanOpenLongTrade) {
				System.out.println("Open long trade at price: " + price
						+ " and pair price: " + pairPrice);
				mTrades.add(new Trade(pairPrice, 0, price, 0, 0, 0));
				mLongTrade = true;
				mOpenPoints.add(i);
			} else if (lastDiff > 1.5 * stdDev && mCanOpenShortTrade) {
				System.out.println("Open short trade at price: " + price
						+ " and pair price: " + pairPrice);
				mTrades.add(new Trade(price, 0, pairPrice, 0, 0, 0));
				mShortTrade = true;
				mOpenPoints.add(i);
			}
		}

		// prints result
		double totalReturn = 0;
		for (Trade t : mTrades) {
			if (t.isOpen()) {
				continue;
			}
			totalReturn += t.getTotalReturn();
			System.out.println("Trade result: " + t.getTotalReturn());
		}

		return totalReturn;
	}
	
	public XYDataset getDataSet() {
		final XYSeries diff = new XYSeries("Difference");
		final XYSeries stdev = new XYSeries("1.5 * stdev");
		final XYSeries negativeStdev = new XYSeries("-1.5 series");
		final XYSeries openPoints = new XYSeries("Open points");
		final XYSeries closePoints = new XYSeries("close points");
		
		for (int i = 0; i < mDiff.size(); i++) {
			diff.add(i, mDiff.get(i));
			stdev.add(i, mStdDev.get(i) * 1.5);
			negativeStdev.add(i, mStdDev.get(i) * -1.5);
		}
		
		for (int open : mOpenPoints) {
			openPoints.add(open, 0);
		}
		
		for (int close : mClosePoints) {
			closePoints.add(close, 0);
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(openPoints);
		dataset.addSeries(closePoints);
		dataset.addSeries(diff);
		dataset.addSeries(stdev);
		dataset.addSeries(negativeStdev);
		
		return dataset;
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
