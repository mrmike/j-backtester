package com.moczul.jbacktester.graph;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class DiffGraph extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DiffGraph() {
        JFreeChart chart = createGraph();
        // we put the chart into a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        // default size
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        // add it to our application
        setContentPane(chartPanel);
	}

	private PieDataset createDataSet() {
		DefaultPieDataset ds = new DefaultPieDataset();
		ds.setValue("Linux", 29);
		ds.setValue("Windows", 54);
		ds.setValue("OS X", 14);

		return ds;
	}

	private JFreeChart createGraph() {
		JFreeChart chart = ChartFactory.createPieChart("OS", createDataSet(),
				true, true, false);

		return chart;
	}
}
