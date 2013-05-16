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

	// simple condition buy when price movments day-day was greater than 3%
	@Override
	public boolean openTrade(MarketEvent event) {
		int pos = event.getPosition();
		if (!mMarketFeed.hasPreviousPrice(pos)) {
			return false;
		}
		double currentPrice = event.getClosePrice();
		double previousPrice = mMarketFeed.getMarketEvent(event.getPosition()-1)
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
