package com.moczul.jbacktester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;

public class EQRFeed implements MarketDataSourceable {

	private ArrayList<Double> mPrices;
	
	public EQRFeed(String stockName, Date startDate, Date endDate) {
		mPrices = new ArrayList<Double>();
		try {
			Utils.getStockFromYahoo(stockName, startDate, endDate, mPrices);
		} catch (IOException e) {
			throw new RuntimeException("Could not read data for " + stockName);
		}
		// we are using feed from yahoo so we should reverse order
		Collections.reverse(mPrices);
	}
	
	@Override
	public int getSize() {
		return mPrices.size();
	}

	@Override
	public MarketEvent getMarketEvent(int position) {
		if (position > getSize()) {
			throw new RuntimeException("Position is larger than data feed size.");
		}
		
		return new MarketEvent(-1, mPrices.get(position), -1, position);
	}

	@Override
	public boolean hasPreviousPrice(int position) {
		return position > 0;
	}

}
