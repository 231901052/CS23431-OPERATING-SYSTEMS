import java.util.*;

public class URLScannerApp {
    public static void main(String[] args) {
        List<URLTask> tasks = new ArrayList<>();
        Random rand = new Random();

        for (int i = 1; i <= 10; i++) {
            String url = "http://example" + i + ".com";
            int scanTime = rand.nextInt(1000) + 500;
            boolean isPhishing = rand.nextBoolean();
            tasks.add(new URLTask(url, scanTime, isPhishing));
        }

        List<URLTask> tasksFCFS = new ArrayList<>(tasks);
        List<URLTask> tasksSJF = new ArrayList<>(tasks);
        List<URLTask> tasksRR = new ArrayList<>(tasks);

        System.out.println("Scanning with FCFS...");
        int fcfsTime = FCFS.execute(tasksFCFS);

        System.out.println("Scanning with SJF...");
        int sjfTime = SJF.execute(tasksSJF);

        System.out.println("Scanning with Round Robin...");
        int rrTime = RoundRobin.execute(tasksRR, 500);

        System.out.println("\nTime taken (milliseconds):");
        System.out.println("FCFS: " + fcfsTime);
        System.out.println("SJF: " + sjfTime);
        System.out.println("Round Robin: " + rrTime);

        GraphPlotter.plot(fcfsTime, sjfTime, rrTime);
    }
}