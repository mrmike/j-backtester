package com.moczul.jbacktester;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.moczul.jbacktester.Utils.DataSource;
import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;

public class StooqFeed implements MarketDataSourceable {

	private ArrayList<Double> mPrices;

	public StooqFeed(String stockName, String filePath) {
		mPrices = new ArrayList<Double>();
		try {
			File f = new File(filePath);
			if (!f.exists()) {
				throw new RuntimeException("File does not exist!");
			}
			Utils.setMarketFeed(f, mPrices, DataSource.STOOQ);
		} catch (IOException e) {
			throw new RuntimeException("Could not read data for " + stockName);
		}
	}

	@Override
	public int getSize() {
		return mPrices.size();
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
