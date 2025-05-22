import java.util.List;
import java.util.Comparator;
import java.util.Collections;

public class SJF {
    public static int execute(List<URLTask> tasks) {
        Collections.sort(tasks, Comparator.comparingInt(t -> t.scanTime));
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