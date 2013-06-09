package com.moczul.jbacktester;

import java.util.ArrayList;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.moczul.jbacktester.data.PairStock;
import com.moczul.jbacktester.data.Portfolio;
import com.moczul.jbacktester.data.Trade.TradeType;

public class StrategyTester {

	private Portfolio mPortfolio;
	private ArrayList<PairStock> mPairs;

	public static final double COMMISSION = 0.003;
	public static final double INTEREST_RATE = 0.045;

	private double mAccountMoney = 100000;
	private double mPairAmount = 0.05;
	ArrayList<Double> mHistoricValue;

	public StrategyTester() {
		init();
	}

	public void init() {
		mPairs = new ArrayList<PairStock>();
		mHistoricValue = new ArrayList<Double>();

		StooqFeed peo = new StooqFeed("peo", "data/2008_2012/peo_d.csv");
		StooqFeed pko = new StooqFeed("pko", "data/2008_2012/pko_d.csv");
		
		StooqFeed ago = new StooqFeed("ago", "data/2008_2012/ago_d.csv");
		StooqFeed mds = new StooqFeed("mds", "data/2008_2012/mds_d.csv");

		StooqFeed cdr = new StooqFeed("cdr", "data/2008_2012/cdr_d.csv");
		StooqFeed ing = new StooqFeed("ing", "data/2008_2012/ing_d.csv");

		StooqFeed pxm = new StooqFeed("pxm", "data/2008_2012/pxm_d.csv");
		StooqFeed tvn = new StooqFeed("tvn", "data/2008_2012/tvn_d.csv");
		
		StooqFeed cie = new StooqFeed("cie", "data/2008_2012/cie_d.csv");
		StooqFeed oil = new StooqFeed("oil", "data/2008_2012/oil_d.csv");
		
		StooqFeed bio = new StooqFeed("bio", "data/2008_2012/bio_d.csv");
		

		mPairs.add(new PairStock("PEO", "PKO", peo, pko));
		mPairs.add(new PairStock("AGO", "MDS", ago, mds));
		mPairs.add(new PairStock("CDR", "ING", cdr, ing));
		mPairs.add(new PairStock("PXM", "TVN", pxm, tvn));
//		mPairs.add(new PairStock("CIE", "OIL", cie, oil));
//		mPairs.add(new PairStock("BIO", "OIL", bio, oil));
	}

	public void startBackTest() {
		// iterate through data
		int size = mPairs.get(0).getSize();
		for (int i = 0; i < size; i++) {
			double currentVal = 0;
			for (PairStock pair : mPairs) {
				double firstPrice = pair.getFirstPrice();
				double secondPrice = pair.getSecondPrice();
				switch (pair.canBeTraded()) {
				case EMPTY:
					break;
				case LONG:
					openTrade(pair, secondPrice, firstPrice, TradeType.LONG);
					break;
				case SHORT:
					openTrade(pair, firstPrice, secondPrice, TradeType.SHORT);
					break;
				}
				double closeVal = pair.moveToNext();
				mAccountMoney += closeVal;
				currentVal += pair.getCurrentValue();
			}
			mHistoricValue.add(i, mAccountMoney + currentVal);
		}

		double roi = 0;
		for (PairStock p : mPairs) {
			roi += p.getTotalReturn();
		}
		System.out.println("Total portfolio return: " + roi);

		// for each iteration check if you are able to open trade

	}

	private void openTrade(PairStock pair, double shortPrice, double longPrice,
			TradeType type) {
		double ratio = 0;
		int shortQty = 0;
		int longQty = 0;
		double capital = mAccountMoney * mPairAmount;
		if (longPrice > shortPrice) {
			ratio = longPrice / shortPrice;
			shortQty = (int) (capital / shortPrice);
			longQty = (int) (shortQty / ratio);
		} else {
			ratio = shortPrice / longPrice;
			shortQty = (int) (capital / shortPrice);
			longQty = (int) (shortQty * ratio);
		}

		while (longQty * longPrice > capital) {
			longQty -= 1;
		}

		while (shortQty * shortPrice < longQty * longPrice) {
			longQty -= 1;
		}

		double shortValue = shortQty * shortPrice;
		double longValue = longQty * longPrice;

		// pay comissions
		mAccountMoney -= COMMISSION * (shortValue + longValue);
		double escrow = shortQty * shortPrice;
		mAccountMoney -= escrow;
		switch (type) {
		case LONG:
			pair.openLongTrade(longQty, shortQty, escrow);
			break;
		case SHORT:
			pair.openShortTrade(longQty, shortQty, escrow);
		}
	}
	
	public XYDataset getHistoricCapital() {
		final XYSeries capital = new XYSeries("Capital");
		for (int i = 0; i < mHistoricValue.size(); i++) {
			double p = mHistoricValue.get(i);
			capital.add(i, p);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(capital);
		return dataset;
	}

}
