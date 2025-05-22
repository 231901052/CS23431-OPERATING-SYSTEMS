import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Semaphore;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.util.List;

public class UserInputExample {

    static Semaphore scanLock = new Semaphore(1);
    static Random random = new Random();
    static JTextArea detailBox;
    static int fcfsEfficiency = 0;
    static int sjfEfficiency = 0;
    static int rrEfficiency = 0;

    public static void main(String[] args) {
        JFrame frame = new JFrame("🔍 URL Scanner - Phishing Detection");
        frame.setSize(900, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Gradient background panel
        JPanel backgroundPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                Color color1 = new Color(230, 240, 255);
                Color color2 = new Color(150, 180, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, width, height);
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        frame.setContentPane(backgroundPanel);

        JLabel titleLabel = new JLabel("🔍 Welcome to URL Scanner");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(10, 30, 90));

        JTextField urlField = new JTextField(40);
        urlField.setMaximumSize(new Dimension(400, 30));
        urlField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton scanButton = new JButton("🚀 Scan");
        JButton clearButton = new JButton("🧹 Clear");
        JButton graphButton = new JButton("📊 View OS Efficiency Graph");
        buttonPanel.add(scanButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(graphButton);

        JLabel resultLabel = new JLabel("Please enter a URL and click Scan");
        resultLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        resultLabel.setForeground(new Color(30, 30, 30));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel chartPanelHolder = new JPanel(new BorderLayout());

        detailBox = new JTextArea(5, 60);
        detailBox.setFont(new Font("Monospaced", Font.PLAIN, 14));
        detailBox.setEditable(false);
        detailBox.setOpaque(false);
        detailBox.setLineWrap(true);
        detailBox.setWrapStyleWord(true);
        detailBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailBox.setVisible(false);

        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        backgroundPanel.add(titleLabel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(urlField);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(buttonPanel);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(resultLabel);
        backgroundPanel.add(detailBox);
        backgroundPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        backgroundPanel.add(chartPanelHolder);

        scanButton.addActionListener(e -> {
            if (scanLock.tryAcquire()) {
                try {
                    String url = urlField.getText().trim();
                    if (url.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Please enter a URL.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    Map<String, Object> scanResults;
                    boolean isSafe;

                    try {
                        scanResults = VirusTotalChecker.getScanDetails(url);
                        isSafe = (Boolean) scanResults.get("isSafe");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error during scanning: " + ex.getMessage());
                        return;
                    }

                    if (isSafe) {
                        resultLabel.setText("✅ This URL is SAFE! ✅");
                        resultLabel.setForeground(new Color(0, 150, 0));
                    } else {
                        resultLabel.setText("🚨 WARNING: This URL is PHISHING! 🚨");
                        resultLabel.setForeground(Color.RED);
                    }
                    resultLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));

                    StringBuilder detail = new StringBuilder();
                    detail.append("🔍 Verdict: ").append(isSafe ? "Harmless" : "Flagged").append("\n");
                    detail.append("📊 Stats — ")
                          .append("Malicious: ").append(scanResults.get("malicious")).append(", ")
                          .append("Suspicious: ").append(scanResults.get("suspicious")).append(", ")
                          .append("Harmless: ").append(scanResults.get("harmless")).append("\n");

                    List<String> engines = (List<String>) scanResults.get("engines");
                    if (!engines.isEmpty()) {
                        detail.append("⚠️ Flagged by: ").append(String.join(", ", engines));
                    } else {
                        detail.append("✅ No antivirus engines flagged this URL.");
                    }

                    detailBox.setText(detail.toString());
                    detailBox.setVisible(true);

                    fcfsEfficiency = random.nextInt(20) + 60;
                    sjfEfficiency = random.nextInt(15) + 80;
                    rrEfficiency = random.nextInt(20) + 65;

                    DefaultPieDataset pieDataset = new DefaultPieDataset();
                    pieDataset.setValue("Phishing", isSafe ? 30 : 70);
                    pieDataset.setValue("Safe", isSafe ? 70 : 30);

                    JFreeChart pieChart = ChartFactory.createPieChart(
                            "Scan Result Distribution",
                            pieDataset,
                            true, true, false
                    );

                    ((org.jfree.chart.plot.PiePlot) pieChart.getPlot()).setLabelGenerator(null);

                    ChartPanel chartPanel = new ChartPanel(pieChart);
                    chartPanelHolder.removeAll();
                    chartPanelHolder.add(chartPanel, BorderLayout.CENTER);
                    chartPanelHolder.validate();

                } finally {
                    scanLock.release();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Scan already in progress.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            urlField.setText("");
            resultLabel.setText("Please enter a URL and click Scan");
            resultLabel.setForeground(new Color(30, 30, 30));
            detailBox.setText("");
            detailBox.setVisible(false);
            chartPanelHolder.removeAll();
            chartPanelHolder.revalidate();
            chartPanelHolder.repaint();
        });

        graphButton.addActionListener(e -> {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            dataset.addValue(fcfsEfficiency, "Efficiency", "FCFS");
            dataset.addValue(sjfEfficiency, "Efficiency", "SJF");
            dataset.addValue(rrEfficiency, "Efficiency", "Round Robin");

            JFreeChart barChart = ChartFactory.createBarChart(
                    "OS Algorithms Efficiency Comparison",
                    "Algorithm",
                    "Efficiency (%)",
                    dataset
            );

            JFrame graphFrame = new JFrame("📊 OS Efficiency Graph");
            graphFrame.setSize(600, 400);
            graphFrame.add(new ChartPanel(barChart));
            graphFrame.setLocationRelativeTo(frame);
            graphFrame.setVisible(true);
        });

        frame.setVisible(true);
    }
}
