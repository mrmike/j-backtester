package com.moczul.jbacktester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Utils {

	public static void setMarketFeed(File csvFile, List<Double> prices)
			throws FileNotFoundException {
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
						.valueOf(data[AppConsts.CSV_CLOSE_PRICE]);
				prices.add(closePrice);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
