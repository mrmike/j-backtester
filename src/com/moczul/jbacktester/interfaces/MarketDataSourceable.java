package com.moczul.jbacktester.interfaces;

import com.moczul.jbacktester.data.MarketEvent;

public interface MarketDataSourceable {

	int getSize();
	MarketEvent getMarketEvent(int position);
}
