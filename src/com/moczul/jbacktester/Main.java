package com.moczul.jbacktester;

import com.moczul.jbacktester.data.Portfolio;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;
import com.moczul.jbacktester.interfaces.Tradable;

public class Main {

	public static void main(String[] args) {
		MarketDataSourceable feed = new PKOFeed();
		Tradable simpleStrategy = new SimpleStrategy(feed);
		Portfolio portfolio = getPortfolio();
		TestRunner pkoRunner = new TestRunner(feed, simpleStrategy, portfolio);
		pkoRunner.runTest();
	}

	private static Portfolio getPortfolio() {
		Portfolio.Builder builder = new Portfolio.Builder();
		return builder.setInitValue(10000).setMaxOrderNumber(10)
				.setGolbalStop(3000).setName("My portfolio")
				.setCommission(0.03).build();
	}
}
