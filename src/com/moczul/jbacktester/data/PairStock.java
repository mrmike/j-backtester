package com.moczul.jbacktester.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.moczul.jbacktester.StooqFeed;
import com.moczul.jbacktester.data.Trade.TradeType;

public class PairStock {

	private static final int STOP_LOSS = -10;
	private static final int TAKE_PROFIT = 20;

	private static final int DEFAULT_PERIOD = 252;
	private static final double M_DEV_MULTIPLIER = 2;

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

	public PairStock(StooqFeed aData, StooqFeed bData) {
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

		if (lastDiff < -M_DEV_MULTIPLIER * stdDev && mCanOpenLongTrade) {
			return TradeType.LONG;
		} else if (lastDiff > M_DEV_MULTIPLIER * stdDev && mCanOpenShortTrade) {
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
		} else {
			return t.getCurrentValue(price, pairPrice);
		}
	}

	public void openLongTrade(int longQty, int shortQty, double escrow) {
		double price = mPrices.get(mCurrentItem);
		double pairPrice = mPairPrices.get(mCurrentItem);
		mTrades.add(new Trade(pairPrice, shortQty, price, longQty, escrow,
				mCurrentItem, TradeType.LONG));
		mLongTrade = true;
		mOpenPoints.add(mCurrentItem);
		System.out.println("Long trade(" + mFirstData.getName() + "): " + price
				+ " for " + longQty + " And short (" + mSecondData.getName()
				+ ") " + pairPrice + " for : " + shortQty);
	}

	public void openShortTrade(int longQty, int shortQty, double escrow) {
		double price = mPrices.get(mCurrentItem);
		double pairPrice = mPairPrices.get(mCurrentItem);
		mTrades.add(new Trade(price, shortQty, pairPrice, longQty, escrow,
				mCurrentItem, TradeType.SHORT));
		mShortTrade = true;
		mOpenPoints.add(mCurrentItem);
		System.out.println("Short trade: (" + mFirstData.getName() + ") "
				+ price + " for: " + shortQty + " and long: ("
				+ mSecondData.getName() + ")" + pairPrice + " for " + longQty);
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

	public double closeTrade() {
		mCurrentItem++;
		double price = mFirstData.getMarketEvent(mCurrentItem).getClosePrice();
		double pairPrice = mSecondData.getMarketEvent(mCurrentItem)
				.getClosePrice();
		if (mShortTrade) {
			Trade lastTrade = mTrades.get(mTrades.size() - 1);
			lastTrade.closeTrade(price, pairPrice, mCurrentItem);
			mShortTrade = false;
			mCanOpenShortTrade = false;
			mClosePoints.add(mCurrentItem);
			return lastTrade.getCloseValue();
		} else if (mLongTrade) {
			Trade lastTrade = mTrades.get(mTrades.size() - 1);
			lastTrade.closeTrade(pairPrice, price, mCurrentItem);
			mLongTrade = false;
			mCanOpenLongTrade = false;
			mClosePoints.add(mCurrentItem);
			return lastTrade.getCloseValue();
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

	public void printTradeSummary() {
		for (Trade trade : mTrades) {
			int start = trade.getStartPoint();
			int end = trade.getEndPoint();
			Date startDate = mFirstData.getDate(start);
			Date endDate = mFirstData.getDate(end);
			String longName;
			String shortName;
			if (trade.getType().equals(TradeType.LONG)) {
				longName = mFirstData.getName();
				shortName = mSecondData.getName();
			} else {
				longName = mSecondData.getName();
				shortName = mFirstData.getName();
			}

			String format = "%s | %d | %f | %f";
			String longInfo = String.format(format, longName,
					trade.getLongQty(), trade.getLongOpenPrice(),
					trade.getLongClosePrice());
			String shortInfo = String.format(format, shortName,
					trade.getShortQty(), trade.getShortOpenPrice(),
					trade.getShortClosePrice());
			double roi = trade.getTotalReturn();
			System.out.println("=====================");
			System.out.println("StartDate: " + startDate.toString());
			System.out.println("LONG " + longInfo);
			System.out.println("SHORT " + shortInfo);
			System.out.println("EndDate: " + endDate.toString());
			System.out.println("Return: " + roi);
			System.out.println("=====================");
		}
	}
	
	public void printTradeStats() {
		int trades = mTrades.size();
		double totalReturn = 0;
		for (Trade t : mTrades) {
			totalReturn += t.getTotalReturn();
		}
		System.out.println("=========Stats==========");
		System.out.println("Pair: " + mFirstData.getName() + " and " + mSecondData.getName());
		System.out.println("Total trades: " + getTotalTrades());
		System.out.println("Win trades: " + getWinTrades());
		System.out.println("Lost trades: " + getLostTrades());
		System.out.println("Average return: " + totalReturn / (double) trades);
	}
	
	public int getWinTrades() {
		int winTrades = 0;
		for (Trade t : mTrades) {
			if (t.getTotalReturn() > 0) {
				winTrades += 1;
			}
		}
		
		return winTrades;
	}
	
	public int getLostTrades() {
		int lostTrades = 0;
		for (Trade t : mTrades) {
			if (t.getTotalReturn() < 0) {
				lostTrades += 1;
			}
		}
		
		return lostTrades;
	}
	
	public int getTotalTrades() {
		return getLostTrades() + getWinTrades();
	}
}
