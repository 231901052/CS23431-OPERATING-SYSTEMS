import java.util.List;
import java.util.LinkedList;

public class RoundRobin {
    public static int execute(List<URLTask> tasks, int quantum) {
        LinkedList<URLTask> queue = new LinkedList<>(tasks);
        int totalTime = 0;
        
        while (!queue.isEmpty()) {
            URLTask task = queue.poll();
            if (task.scanTime <= quantum) {
                totalTime += task.scanTime;
                try {
                    Thread.sleep(task.scanTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                totalTime += quantum;
                task.scanTime -= quantum;
                try {
                    Thread.sleep(quantum);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.add(task);
            }
        }
        return totalTime;
    }
}