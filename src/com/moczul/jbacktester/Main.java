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

import java.text.ParseException;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

public class Main {
	
	public static void main(String[] args) throws ParseException {
		StrategyTester tester = new StrategyTester();
		tester.startBackTest();
		showCapitalChart(tester.getHistoricCapital());
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
