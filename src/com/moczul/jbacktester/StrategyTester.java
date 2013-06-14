package com.moczul.jbacktester;

import java.text.ParseException;
import java.util.ArrayList;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.moczul.jbacktester.data.PairStock;
import com.moczul.jbacktester.data.Trade.TradeType;

public class StrategyTester {

	private ArrayList<PairStock> mPairs2008 = new ArrayList<PairStock>();
	private ArrayList<PairStock> mPairs2010 = new ArrayList<PairStock>();
	
	public static final double COMMISSION = 0.003;
	public static final double INTEREST_RATE = 0.04;

	private double mAccountMoney = 100000;
	private double mPairAmount = 0.15;
	ArrayList<Double> mHistoricValue;

	public StrategyTester() throws ParseException {
		init();
	}

	public void init() throws ParseException {
		mHistoricValue = new ArrayList<Double>();

		StooqFeed peo = new StooqFeed("peo", "data/2008_2012/peo_d.csv");
		StooqFeed pko = new StooqFeed("pko", "data/2008_2012/pko_d.csv");
		StooqFeed pxm = new StooqFeed("pxm", "data/2008_2012/pxm_d.csv");
		StooqFeed tvn = new StooqFeed("tvn", "data/2008_2012/tvn_d.csv");
		StooqFeed lts = new StooqFeed("lts", "data/2008_2012/lts_d.csv");
		StooqFeed ech = new StooqFeed("ech", "data/2008_2012/ech_d.csv");
		StooqFeed ago = new StooqFeed("ago", "data/2008_2012/ago_d.csv");
		StooqFeed gtn = new StooqFeed("gtn", "data/2008_2012/gtn_d.csv");
		StooqFeed bre = new StooqFeed("bre", "data/2008_2012/bre_d.csv");
		StooqFeed ast = new StooqFeed("ast", "data/2008_2012/ast_d.csv");
		StooqFeed gtc = new StooqFeed("gtc", "data/2008_2012/gtc_d.csv");
		StooqFeed bhw = new StooqFeed("bhw", "data/2008_2012/bhw_d.csv");
		StooqFeed kgh = new StooqFeed("kgh", "data/2008_2012/kgh_d.csv");
		StooqFeed cie = new StooqFeed("cie", "data/2008_2012/cie_d.csv");
		StooqFeed oil = new StooqFeed("oil", "data/2008_2012/oil_d.csv");
		StooqFeed bio = new StooqFeed("bio", "data/2008_2012/bio_d.csv");
		StooqFeed gnb = new StooqFeed("gnb", "data/2008_2012/gnb_d.csv");
		StooqFeed cdr = new StooqFeed("cdr", "data/2008_2012/cdr_d.csv");
		StooqFeed ing = new StooqFeed("ing", "data/2008_2012/ing_d.csv");
		StooqFeed eur = new StooqFeed("eur", "data/2008_2012/eur_d.csv");
		StooqFeed sns = new StooqFeed("sns", "data/2008_2012/sns_d.csv");
		StooqFeed lpp = new StooqFeed("lpp", "data/2008_2012/lpp_d.csv");
		StooqFeed kty = new StooqFeed("kty", "data/2008_2012/kty_d.csv");
		StooqFeed hwe = new StooqFeed("hwe", "data/2008_2012/hwe_d.csv");
		StooqFeed itg = new StooqFeed("itg", "data/2008_2012/itg_d.csv");
		StooqFeed idm = new StooqFeed("idm", "data/2008_2012/idm_d.csv");
		StooqFeed brs = new StooqFeed("brs", "data/2008_2012/brs_d.csv");
		StooqFeed net = new StooqFeed("net", "data/2008_2012/net_d.csv");
		
		// pairs for 2008
		mPairs2008.add(new PairStock(cie, oil));
		mPairs2008.add(new PairStock(peo, pko));
		mPairs2008.add(new PairStock(bio, gnb));
		mPairs2008.add(new PairStock(bio, oil));
		mPairs2008.add(new PairStock(ago, gtn));
		mPairs2008.add(new PairStock(bio, cie));
		mPairs2008.add(new PairStock(gnb, oil));
		mPairs2008.add(new PairStock(cdr, ing));
		mPairs2008.add(new PairStock(pxm, tvn));
		mPairs2008.add(new PairStock(lts, ech));
		mPairs2008.add(new PairStock(bre, ast));
		mPairs2008.add(new PairStock(cie, gnb));
		mPairs2008.add(new PairStock(gtc, pxm));
		mPairs2008.add(new PairStock(ago, pxm));
		mPairs2008.add(new PairStock(gtc, ago));
		mPairs2008.add(new PairStock(bhw, kgh));
		mPairs2008.add(new PairStock(eur, sns));
		
		// pairs for 2010
		mPairs2010.add(new PairStock(sns, cdr));
		mPairs2010.add(new PairStock(brs, eur));
		mPairs2010.add(new PairStock(eur, sns));
		mPairs2010.add(new PairStock(eur, lpp));
		mPairs2010.add(new PairStock(brs, lpp));
		mPairs2010.add(new PairStock(sns, itg));
		mPairs2010.add(new PairStock(brs, sns));
		mPairs2010.add(new PairStock(sns, lpp));
		mPairs2010.add(new PairStock(kgh, net));
		mPairs2010.add(new PairStock(gnb, gtn));
		mPairs2010.add(new PairStock(bio, oil));
		mPairs2010.add(new PairStock(ech, hwe));
		mPairs2010.add(new PairStock(bio, pxm));
		mPairs2010.add(new PairStock(cdr, lpp));
		mPairs2010.add(new PairStock(cie, pxm));
		mPairs2010.add(new PairStock(lts, kty));
		mPairs2010.add(new PairStock(ago, idm));
		
	}

	public void startBackTest() {
		// iterate through data
		for (int i = 0; i < 504; i++) {
			double currentVal = 0;
			for (PairStock pair : mPairs2008) {
				if (i == 503) {
					// close all trades
					mAccountMoney += pair.closeTrade();
					currentVal = 0;
					continue;
				}
				TradeType type = pair.canBeTraded();
				switch (type) {
				case EMPTY:
					break;
				case LONG:
				case SHORT:
					openTrade(pair, type);
					break;
				}
				double closeVal = pair.moveToNext();
				mAccountMoney += closeVal;
				currentVal += pair.getCurrentValue();
			}
			mHistoricValue.add(i, mAccountMoney + currentVal);
		}
		
		int size = mPairs2010.get(1).getSize();
		for (int i = 504; i < size; i++) {
			double currentVal = 0;
			for (PairStock pair : mPairs2010) {
				if (i == size-1) {
					// close all trades
					mAccountMoney += pair.closeTrade();
					currentVal = 0;
					continue;
				}
				TradeType type = pair.canBeTraded();
				switch (type) {
				case EMPTY:
					break;
				case LONG:
				case SHORT:
					openTrade(pair, type);
					break;
				}
				double closeVal = pair.moveToNext();
				mAccountMoney += closeVal;
				currentVal += pair.getCurrentValue();
			}
			mHistoricValue.add(i, mAccountMoney + currentVal);
		}
		

		double roi = 0;
		for (PairStock p : mPairs2008) {
			roi += p.getTotalReturn();
		}
		
		for (PairStock p : mPairs2010) {
			roi += p.getTotalReturn();
		}
		
		int winTrades = 0;
		int lostTrades = 0;
		for (PairStock p : mPairs2008) {
			p.printTradeStats();
			winTrades += p.getWinTrades();
			lostTrades += p.getLostTrades();
		}
		
		for (PairStock p : mPairs2010) {
			p.printTradeStats();
			winTrades += p.getWinTrades();
			lostTrades += p.getLostTrades();
		}
		
		System.out.println("===========");
		System.out.println("Total return: " + roi);
		System.out.println("Total trades: " + winTrades + lostTrades);
		System.out.println("Avg return: " + roi / (double) (winTrades + lostTrades));
		System.out.println("Win Trades: " + winTrades);
		System.out.println("Lost trades: " + lostTrades);
	}

	private void openTrade(PairStock pair, TradeType type) {
		double longPrice;
		double shortPrice;
		if (TradeType.LONG.equals(type)) {
			longPrice = pair.getFirstPrice();
			shortPrice = pair.getSecondPrice();
		} else if (TradeType.SHORT.equals(type)) {
			longPrice = pair.getSecondPrice();
			shortPrice = pair.getFirstPrice();
		} else {
			throw new RuntimeException("Trade has to be LONG or SHORT");
		}
		
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
			break;
		}
	}
	
	public XYDataset getHistoricCapital() {
		final XYSeries capital = new XYSeries("Krzywa kapita³u");
		for (int i = 0; i < mHistoricValue.size(); i++) {
			double p = mHistoricValue.get(i);
			capital.add(i, p);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(capital);
		return dataset;
	}

}
