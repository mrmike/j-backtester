package com.moczul.jbacktester.data;

import java.util.ArrayList;
import java.util.List;

public class Portfolio {

	private double mInvestedValue;
	private double mMarginValue; // TotalValue = invested + margin
	private int mMaxOrders;
	private double mGlobalStop;
	private String mName;
	private List<Order> mOrders;
	private double mMaxOrderValue;
	private double mCommission = 0.03; // commission as a percentage of
										// transaction value

	public static class Builder {

		Portfolio portfolio;

		public Builder() {
			portfolio = new Portfolio();
			portfolio.mOrders = new ArrayList<Order>();
		}

		public Builder setInitValue(double value) {
			portfolio.mMarginValue = value;
			portfolio.mInvestedValue = 0;
			return this;
		}

		public Builder setMaxOrderNumber(int max) {
			portfolio.mMaxOrders = max;
			return this;
		}

		public Builder setGolbalStop(int minValue) {
			portfolio.mGlobalStop = minValue;
			return this;
		}

		public Builder setName(String name) {
			portfolio.mName = name;
			return this;
		}

		public Builder setMaxValueOrder(double maxOrder) {
			portfolio.mMaxOrderValue = maxOrder;
			return this;
		}

		public Builder setCommission(double commission) {
			portfolio.mCommission = commission;
			return this;
		}

		public Portfolio build() {
			return portfolio;
		}

	}

	private Portfolio() {
		// Portfolio can be only create via builder
	}
	
	public boolean openOrder(MarketEvent event, int qty, String name) {
		double price = event.getClosePrice();
		// check if we can open trade
		if (price * qty > mMarginValue) {
			return false;
		}

		if (mOrders.size() == mMaxOrders) {
			return false;
		}

		Order order = new Order(price, qty, System.currentTimeMillis(), name);
		double value = price * qty;
		mInvestedValue += value;
		mMarginValue -= (value + value * mCommission);
		System.out.println("Open order for " + name + " qty: " + qty
				+ " for price: " + price);
		return mOrders.add(order);
	}

	public void closeOrder(MarketEvent event, int position) {
		double price = event.getClosePrice();
		Order order = mOrders.get(position);
		double value = order.getAmount() * price;
		mInvestedValue -= value;
		mMarginValue += value - value * mCommission;
		System.out.println("Closed order no. " + position + " for qty: " + order.getAmount() + " at price : " + price);
		mOrders.remove(order);
	}

	public double getTotalValue() {
		return mInvestedValue+mMarginValue;
	}
	
	public int getMaxOrders() {
		return mMaxOrders;
	}

	public double getGlobalStopLevel() {
		return mGlobalStop;
	}

	public String getName() {
		return mName;
	}

	public double getMaxOrderValue() {
		return mMaxOrderValue;
	}

	public double getCommission() {
		return mCommission;
	}

	public List<Order> getOrders() {
		return mOrders;
	}

}
