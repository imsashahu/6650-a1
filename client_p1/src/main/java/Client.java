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

        Long executedRequests = executeInThreads(183);
        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        System.out.format("Total run time in milliseconds is: %d\n",
                (endTime.getTime() - startTime.getTime()));
        System.out.format("Total throughput in requests per second is: %d\n",
                (int)((float)executedRequests/(endTime.getTime() - startTime.getTime())*1000));

    }

    private static Long executeInThreads(int numThreads)
            throws InterruptedException {
        CountDownLatch completed = new CountDownLatch(numThreads);
        List<Integer> threadCounts = divideIntoThreads(numThreads);

        for (int i = 0; i < numThreads; i++) {
            Runnable thread =  new MyRunnable(threadCounts.get(i), completed);
            // Runnable thread =  new MyRunnable(500000 / numThreads, completed);
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
