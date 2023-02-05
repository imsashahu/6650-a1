import java.io.BufferedWriter;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        Timestamp startTime = new Timestamp(System.currentTimeMillis());
        LinkedBlockingQueue<String> outputSet = new LinkedBlockingQueue<>();

        Long executedRequests = executeInThreads(183, outputSet);
        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        System.out.format("Total run time in milliseconds is: %d\n",
                (endTime.getTime() - startTime.getTime()));
        System.out.format("Total throughput in requests per second is: %d\n",
                (int)((float)executedRequests/(endTime.getTime() - startTime.getTime())*1000));

        // Write to CSV file
        try (FileWriter writer = new FileWriter("6650_hw1_analysis_report.csv")) {
            BufferedWriter out = new BufferedWriter(writer);
            while (!outputSet.isEmpty()) {
                out.write(outputSet.take());
                out.newLine();
            }
            out.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    private static Long executeInThreads(int numThreads, LinkedBlockingQueue<String> outputSet)
            throws InterruptedException {
        CountDownLatch completed = new CountDownLatch(numThreads);
        List<Integer> threadCounts = divideIntoThreads(numThreads);

        for (int i = 0; i < numThreads; i++) {
            Runnable thread =  new MyRunnable(threadCounts.get(i), completed, outputSet);
            // Runnable thread =  new MyRunnable(500000 / numThreads, completed, outputSet);
            new Thread(thread).start();
        }

        completed.await();
        System.out.println("Total number of successful requests is: " +
                (Long.parseLong("500000") - completed.getCount()));
        System.out.println("Total number of unsuccessful requests is: " + completed.getCount());
        return (Long.parseLong("500000") - completed.getCount());
    }

    private static List<Integer> divideIntoThreads(int numThreads) {
        List<Integer> output = new java.util.ArrayList<>(Collections.nCopies(numThreads, 500000 / numThreads));
        if (500000 % numThreads == 0) {
            return output;
        }
        for (int i = 0; i < 500000 % numThreads; i ++) {
            int index = new Random().nextInt(numThreads);
            output.set(index, output.get(index) + 1);
        }
        return output;
    }

}
