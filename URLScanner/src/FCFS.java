import java.util.List;

public class FCFS {
    public static int execute(List<URLTask> tasks) {
        int totalTime = 0;
        for (URLTask task : tasks) {
            totalTime += task.scanTime;
            try {
                Thread.sleep(task.scanTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return totalTime;
    }
}