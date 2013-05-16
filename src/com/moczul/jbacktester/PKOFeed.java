package com.moczul.jbacktester;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;

public class PKOFeed implements MarketDataSourceable {

	private List<Double> mPrices;

	public PKOFeed() {
		mPrices = new ArrayList<Double>();

		// init arrayList
		File f = new File("data/2010_pko_daily.csv");
		try {
			Utils.setMarketFeed(f, mPrices);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSize() {
		return mPrices.size();
	}

	@Override
	public MarketEvent getMarketEvent(int position) {
		if (position > mPrices.size()) {
			throw new RuntimeException("Position is out of range.");
		}

		double closePrice = mPrices.get(position);
		return new MarketEvent(10, closePrice, 25, position);
	}

}
