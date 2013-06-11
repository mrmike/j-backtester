package com.moczul.jbacktester;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.moczul.jbacktester.Utils.DataSource;
import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;

public class StooqFeed implements MarketDataSourceable {

	private ArrayList<Double> mPrices;
	private ArrayList<Long> mDates;
	private String mName;

	public StooqFeed(String stockName, String filePath) throws ParseException {
		mPrices = new ArrayList<Double>();
		mDates = new ArrayList<Long>();
		mName = stockName;
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				throw new RuntimeException("File does not exist!");
			}
			Utils.setMarketFeed(f, mPrices, mDates, DataSource.STOOQ);
		} catch (IOException e) {
			throw new RuntimeException("Could not read data for " + stockName);
		}
	}
	
	public String getName() {
		return mName;
	}

	@Override
	public int getSize() {
		return mPrices.size();
	}
	
	public Date getDate(int position) {
		return new Date(mDates.get(position));
	}

	@Override
	public MarketEvent getMarketEvent(int position) {
		if (position > getSize()) {
			throw new RuntimeException(
					"Position is larger than data feed size.");
		}

		return new MarketEvent(-1, mPrices.get(position), -1, position);
	}

	@Override
	public boolean hasPreviousPrice(int position) {
		return position > 0;
	}

}
