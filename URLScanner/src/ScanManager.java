import java.util.concurrent.Semaphore;
import java.util.List;

public class ScanManager {
    private static final Semaphore semaphore = new Semaphore(3);

    public static void scan(URLTask task) {
        try {
            semaphore.acquire();
            System.out.println("Scanning URL: " + task.url);
            Thread.sleep(task.scanTime);
            System.out.println("Result: " + (task.isPhishing ? "Phishing" : "Safe"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    public static void scanAll(List<URLTask> tasks) {
        for (URLTask task : tasks) {
            new Thread(() -> scan(task)).start();
        }
    }
}