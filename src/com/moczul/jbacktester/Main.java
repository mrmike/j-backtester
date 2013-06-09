/** 
 *
 * Copyright 2013 Michał Moczulski

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moczul.jbacktester;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class Main {
	
	public static void main(String[] args) {
		StrategyTester tester = new StrategyTester();
		tester.startBackTest();
		showCapitalChart(tester.getHistoricCapital());
	}

	/*public static void main(String[] args) {
		ArrayList<Double> returns = new ArrayList<Double>();
		StooqFeed cie = new StooqFeed("cie", "data/2008_2012/cie_d.csv");
		StooqFeed oil = new StooqFeed("oil", "data/2008_2012/oil_d.csv");

		StooqFeed ago = new StooqFeed("ago", "data/2008_2012/ago_d.csv");
		StooqFeed mds = new StooqFeed("mds", "data/2008_2012/mds_d.csv");

		StooqFeed peo = new StooqFeed("peo", "data/2008_2012/peo_d.csv");
		StooqFeed pko = new StooqFeed("pko", "data/2008_2012/pko_d.csv");

		StooqFeed cdr = new StooqFeed("cdr", "data/2008_2012/cdr_d.csv");
		StooqFeed ing = new StooqFeed("ing", "data/2008_2012/ing_d.csv");

		StooqFeed pxm = new StooqFeed("pxm", "data/2008_2012/pxm_d.csv");
		StooqFeed tvn = new StooqFeed("tvn", "data/2008_2012/tvn_d.csv");

		StooqFeed lpp = new StooqFeed("lpp", "data/2008_2012/lpp_d.csv");

		PairTestRunner runner = new PairTestRunner(cie, oil);
		// returns.add(runner.startBackTest());

		// runner = new PairTestRunner(ago, mds);
		// returns.add(runner.startBackTest());
		//
		runner = new PairTestRunner(peo, pko);
		returns.add(runner.startBackTest());
		//
		// runner = new PairTestRunner(cdr, ing);
		// returns.add(runner.startBackTest());
		//
		// runner = new PairTestRunner(pxm, tvn);
		// returns.add(runner.startBackTest());

		double total = 0;
		for (int i = 0; i < returns.size(); i++) {
			double roi = returns.get(i);
			total += roi;
			System.out.println(i + 1 + ". Return: " + roi);
		}

		System.out.println("===============");
		System.out.println("Total retun: " + total);
		System.out.println("===============");

		XYDataset dataset = runner.getDataSet();
		showChart(dataset);
	}*/

	private void yahooTest() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2008, 0, 1);
		Date startDate = calendar.getTime();
		calendar.set(2012, 11, 31);
		Date endDate = calendar.getTime();

		AVBFeed avbFeed = new AVBFeed("ALTR", startDate, endDate);
		EQRFeed eqrFeed = new EQRFeed("MCHP", startDate, endDate);

		PairTestRunner pairTest = new PairTestRunner(avbFeed, eqrFeed);
		pairTest.startBackTest();
	}

	private static void showChart(XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"PKO - PEO", // chart title
				"X", // x axis label
				"Y", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));

		XYPlot plot = chart.getXYPlot();
		final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, false);
		renderer.setSeriesLinesVisible(1, false);
		renderer.setSeriesShapesVisible(2, false);
		renderer.setSeriesShapesVisible(3, false);
		renderer.setSeriesShapesVisible(4, false);
		plot.setRenderer(renderer);

		JFrame frame = new JFrame();
		frame.setContentPane(chartPanel);

		frame.pack();
		frame.setVisible(true);
	}
	
	private static void showCapitalChart(XYDataset dataset) {
		final JFreeChart chart = ChartFactory.createXYLineChart(
				"Capital curve", // chart title
				"X", // x axis label
				"Y", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		
		JFrame frame = new JFrame();
		frame.setContentPane(chartPanel);

		frame.pack();
		frame.setVisible(true);
	}
}
