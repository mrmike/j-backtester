package com.moczul.jbacktester.interfaces;

import com.moczul.jbacktester.data.MarketEvent;
import com.moczul.jbacktester.data.Order;

public interface Tradable {

	boolean openTrade(MarketEvent event);

	boolean closeTrade(Order order, MarketEvent event);

}
