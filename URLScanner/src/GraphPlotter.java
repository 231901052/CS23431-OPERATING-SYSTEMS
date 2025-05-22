import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class GraphPlotter {
    public static void plot(int fcfs, int sjf, int rr) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(fcfs, "Time", "FCFS");
        dataset.addValue(sjf, "Time", "SJF");
        dataset.addValue(rr, "Time", "Round Robin");

        JFreeChart chart = ChartFactory.createBarChart(
                "Scheduling Algorithm Efficiency",
                "Algorithm",
                "Time (ms)",
                dataset
        );

        ChartFrame frame = new ChartFrame("Algorithm Comparison", chart);
        frame.setVisible(true);
        frame.setSize(600, 400);
    }
}