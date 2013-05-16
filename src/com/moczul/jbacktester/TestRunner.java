package com.moczul.jbacktester;

import com.moczul.jbacktester.data.Portfolio;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;
import com.moczul.jbacktester.interfaces.Tradable;

public class TestRunner {

	private MarketDataSourceable mMarketFeed;
	private Tradable mStrategy;
	private Portfolio mPortfolio;

	public TestRunner(MarketDataSourceable source, Tradable strategy,
			Portfolio portfolio) {
		mMarketFeed = source;
		mStrategy = strategy;
		mPortfolio = portfolio;
	}
	
	public void runTest() {
		for (int i = 0; i < mMarketFeed.getSize(); i++) {
			// (1) Manage old orders
			// (2) check if you can open a new one
			// (3) move next
		}
	}

}
