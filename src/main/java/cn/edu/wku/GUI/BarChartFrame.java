package cn.edu.wku.GUI;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.CategoryDataset;

import java.awt.*;

/**
 * A simple demonstration application showing how to create a bar chart.
 */
public class BarChartFrame extends ApplicationFrame {

//    @Serial
//    private static final long serialVersionUID = 1L;

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public BarChartFrame(String title, CategoryDataset dataset) {
        super(title);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
        setResizable(false);
    }


    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                "Performance of different lock", "number of trails" /* x-axis label*/,
                "Time/Milliseconds" /* y-axis label */, dataset);

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getLegend().setFrame(BlockBorder.NONE);
        return chart;
    }

}
