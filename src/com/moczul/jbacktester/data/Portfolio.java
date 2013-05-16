package com.moczul.jbacktester.data;

import java.util.List;

public class Portfolio {

	private double mValue;
	private int mMaxOrders;
	private double mGlobalStop;
	private String mName;
	private List<Order> mOrders;
	private double mMaxOrderValue;

	static class Builder {

		Portfolio portfolio;

		public Builder() {
			portfolio = new Portfolio();
		}

		public Builder setInitValue(double value) {
			portfolio.mValue = value;
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

		public Portfolio build() {
			return portfolio;
		}

	}

	private Portfolio() {
		// Portfolio can be only create via builder
	}
	
	public double getCurrentValue() {
		return mValue;
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
	
	public List<Order> getOrders() {
		return mOrders;
	}

}
