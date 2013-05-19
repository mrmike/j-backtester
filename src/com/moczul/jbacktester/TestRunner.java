package com.moczul.jbacktester;

import java.util.List;

import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.data.Order;
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
			MarketEvent event = mMarketFeed.getMarketEvent(i);
			System.out.println(i + ". Market event: " + event.getClosePrice());
			List<Order> currentOrders = mPortfolio.getOrders();
			for (int j = 0; j < currentOrders.size(); j++) {
				Order order = currentOrders.get(j);
				int index = currentOrders.indexOf(order);
				if (mStrategy.closeTrade(order, event)) {
					mPortfolio.closeOrder(event, index);
				}
			}

			if (mStrategy.openTrade(event)) {
				mPortfolio.openOrder(event, 100, "PKO");
			}
		}

		System.out.println("Portfolio total value: "
				+ mPortfolio.getTotalValue());
	}

}
