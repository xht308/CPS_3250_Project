package cn.edu.wku;

        import org.jfree.chart.ChartFactory;
        import org.jfree.chart.ChartPanel;
        import org.jfree.chart.JFreeChart;
        import org.jfree.chart.axis.NumberAxis;
        import org.jfree.chart.block.BlockBorder;
        import org.jfree.chart.plot.CategoryPlot;
        import org.jfree.chart.title.TextTitle;
        import org.jfree.chart.ui.ApplicationFrame;
        import org.jfree.data.category.CategoryDataset;
        import org.jfree.data.category.DefaultCategoryDataset;

        import java.awt.*;

/**
 * A simple demonstration application showing how to create a bar chart.
 */
public class BarChartDemo1 extends ApplicationFrame {

    private static final long serialVersionUID = 1L;
    static String LockChoose = null;
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
     static UIFrame UIFrame = new UIFrame();

    public static String getLockChoose() {
        return LockChoose;
    }

    public static void setLockChoose(String lockChoose) {
        LockChoose = lockChoose;
    }

    /**
     * Creates a new demo instance.
     *
     * @param title  the frame title.
     */
    public BarChartDemo1(String title) {
        super(title);
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartPanel);
        setResizable(false);
    }

//    public void addData(long time, String Lock, String xAxis) {
//        dataset.addValue(time, Lock, xAxis);
//    }

//    public static void checkLock(Boolean SpinFlag, Boolean MutexFlag, Boolean MCSFlag, Boolean ImprovedMCSFlag){
//        if(SpinFlag){
//            LockChoose = LockChoose + "SpinLock ";
//        }if(MutexFlag){
//            LockChoose = LockChoose + "MutexLock ";
//        }if(MCSFlag){
//            LockChoose = LockChoose + "MCSLock ";
//        }if(ImprovedMCSFlag){
//            LockChoose = LockChoose + "ImprovedMCSLock ";
//        }
//        System.out.println("choose");
//        System.out.println(getLockChoose());
////        return LockChoose;
//    }

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
                "Milliseconds" /* y-axis label */, dataset);
//        checkLock(UIFrame.getSpinFlag(), UIFrame.getMutexFlag(), UIFrame.getMCSFlag(), UIFrame.getImprovedMutexFlag());
//        System.out.println(LockChoose);
//        chart.addSubtitle(new TextTitle(LockChoose));

        chart.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getLegend().setFrame(BlockBorder.NONE);
        return chart;
    }

}
