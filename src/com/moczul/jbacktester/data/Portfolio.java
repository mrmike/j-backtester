package com.moczul.jbacktester.data;

import java.util.List;

public class Portfolio {

	private double mTotalValue;
	private double mInvestedValue;
	private double mMarginValue; // TotalValue = invested + margin
	private int mMaxOrders;
	private double mGlobalStop;
	private String mName;
	private List<Order> mOrders;
	private double mMaxOrderValue;
	private double mCommission = 0.03; // commission as a percentage of transaction value

	static class Builder {

		Portfolio portfolio;

		public Builder() {
			portfolio = new Portfolio();
		}

		public Builder setInitValue(double value) {
			portfolio.mTotalValue = value;
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
	
	private void openOrder() {
		
	}
	
	private void closeOrder() {
		
	}
	
	public double getTotalValue() {
		return mTotalValue;
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
