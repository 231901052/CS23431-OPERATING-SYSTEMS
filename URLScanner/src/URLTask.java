public class URLTask {
    public String url;
    public int scanTime; // Simulated scan time in milliseconds
    public boolean isPhishing; // Randomly assigned for simulation

    public URLTask(String url, int scanTime, boolean isPhishing) {
        this.url = url;
        this.scanTime = scanTime;
        this.isPhishing = isPhishing;
    }
}