package com.moczul.jbacktester;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {
	
	public enum DataSource {
		YAHOO("yahoo"), STOOQ("stooq");
		
		String mSource;
		
		DataSource(String source) {
			mSource = source;
		}
		
		@Override
		public String toString() {
			return mSource;
		}
	}
	
	public static void setMarketFeed(File csvFile, List<Double> prices, DataSource source)
			throws FileNotFoundException {
		int closePriceIndex = -1;
		if (DataSource.YAHOO.equals(source)) {
			closePriceIndex = AppConsts.YAHOO_ADJ_CLOSE;
		} else if (DataSource.STOOQ.equals(source)) {
			closePriceIndex = AppConsts.CSV_CLOSE_PRICE;
		} else {
			throw new RuntimeException("Unkown data source");
		}
		
		if (!csvFile.exists()) {
			throw new RuntimeException("File: " + csvFile.getName()
					+ " does not exist.");
		}

		BufferedReader reader = new BufferedReader(new FileReader(csvFile));
		try {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] data = line.split(",");
				double closePrice = Double
						.valueOf(data[closePriceIndex]);
				prices.add(closePrice);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// perfect case for multi-threading TODO
		public static void getStockFromYahoo(String stockName, Date startDate,
				Date endDate, List<Double> prices) throws IOException {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			int startDay = calendar.get(Calendar.DAY_OF_MONTH);
			int startMonth = calendar.get(Calendar.MONTH);
			int startYear = calendar.get(Calendar.YEAR);
			calendar.setTime(endDate);
			int endDay = calendar.get(Calendar.DAY_OF_MONTH);
			int endMonth = calendar.get(Calendar.MONTH);
			int endYear = calendar.get(Calendar.YEAR);
			
			File directorty = new File("stock_data");
			if (!directorty.exists()) {
				directorty.mkdir();
			}

			String url = getYahooStockUrl(stockName, startDay, startMonth,
					startYear, endDay, endMonth, endYear);
			String fileName = getFileName(stockName, startDay, startMonth, startYear, endDay, endMonth, endYear);
			File f = new File(fileName);
			if (f.exists()) {
				System.out.println("File " + fileName + " already exists");
				setMarketFeed(f, prices, DataSource.YAHOO);
				return;
			}
			BufferedInputStream inputStream = null;
			FileOutputStream outputStream = null;

			try {
				inputStream = new BufferedInputStream(new URL(url).openStream());
				outputStream = new FileOutputStream(f);
				byte data[] = new byte[1024];
				int count;
				while ((count = inputStream.read(data)) != -1) {
					outputStream.write(data, 0, count);
				}
			} catch (MalformedURLException e) {
				// simply ignore
				return;
			} catch (IOException e) {
				// ignore
				System.out.println("For this period stock data for " + stockName + " could not be find");
				return;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			System.out.println("Downloaded data for " + stockName);
			setMarketFeed(f, prices, DataSource.YAHOO);
		}
	
	private static String getFileName(String stockName, int startDay,
			int startMonth, int startYear, int endDay, int endMonth, int endYear) {
		String format = "stock_data/%s_%d_%d_%d_to_%d_%d_%d.csv";
		return String.format(format, stockName, startDay, startMonth,
				startYear, endDay, endMonth, endYear);
	}

	private static String getYahooStockUrl(String stockName, int startDay,
			int startMonth, int startYear, int endDay, int endMonth, int endYear) {
		return String.format(AppConsts.YAHOO_FORMAT, stockName, startMonth,
				startDay, startYear, endMonth, endDay, endYear);
	}
}
