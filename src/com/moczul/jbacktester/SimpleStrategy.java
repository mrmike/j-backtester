package com.moczul.jbacktester;

import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.data.Order;
import com.moczul.jbacktester.interfaces.MarketDataSourceable;
import com.moczul.jbacktester.interfaces.Tradable;

public class SimpleStrategy implements Tradable {

	private MarketDataSourceable mMarketFeed;

	private double mDefaultStopLoss = 0.05;
	private double mDefaultTakeProfit = 0.05;

	public SimpleStrategy(MarketDataSourceable source) {
		mMarketFeed = source;
	}

	@Override
	public boolean openTrade(MarketEvent event) {
		double currentPrice = event.getClosePrice();
		double previousPrice = mMarketFeed.getMarketEvent(event.getPosition())
				.getClosePrice();
		double roi = (currentPrice - previousPrice) / previousPrice;
		return roi > 0.03;

	}

	@Override
	public boolean closeTrade(Order order, MarketEvent event) {
		double orderPrice = order.getOpenPrice();
		double currentPrice = event.getClosePrice();
		double roi = (currentPrice - orderPrice) / orderPrice;
		if (roi >= mDefaultTakeProfit || roi < -mDefaultStopLoss) {
			return true;
		}
		return false;
	}

}
