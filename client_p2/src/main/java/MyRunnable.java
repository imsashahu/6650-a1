import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class MyRunnable implements Runnable {
    int numRequest;
    CountDownLatch countDown;
    LinkedBlockingQueue<String> outputSet;

    public MyRunnable(int numRequest, CountDownLatch countDown, LinkedBlockingQueue<String> outputSet) {
        this.numRequest = numRequest;
        this.countDown = countDown;
        this.outputSet = outputSet;
    }

    public void run() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://35.87.36.221:8080/swipe_war");
        //apiClient.setBasePath("http://localhost:8080/swipe_war_exploded");
        SwipeApi apiInstance = new SwipeApi(apiClient);
        SwipeDetails body = new SwipeDetails();
        RandomGenerator newPostRequest = new RandomGenerator();
        StringBuilder sb;

        for (int iRequest = 0; iRequest < numRequest; iRequest++) {
            newPostRequest.generate();
            body.setSwiper(newPostRequest.getSwiper());
            body.setSwipee(newPostRequest.getSwipee());
            body.setComment(newPostRequest.getComment());
            String leftOrRight = newPostRequest.getSwipe();

            ApiResponse<Void> apiResponse = null;

            Timestamp startTime = new Timestamp(System.currentTimeMillis());
            Timestamp endTime = null;

            // Try once first + Else try another 5 times, in total 6 times
            int count = 0;
            while (count <= 5 && (apiResponse == null || apiResponse.getStatusCode() != 201)) {
                try {
                    apiResponse = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
                count ++;
            }
            endTime = new Timestamp(System.currentTimeMillis());

            sb = new StringBuilder();
            sb.append(startTime.getTime());
            sb.append(",");
            sb.append(String.valueOf(endTime.getTime() - startTime.getTime()));
            sb.append(",");
            sb.append("POST");
            sb.append(",");
            sb.append(String.valueOf(apiResponse != null ? apiResponse.getStatusCode() : 0));
            try {
                outputSet.put(sb.toString());
            } catch (InterruptedException ignored) {
            }
        }

        countDown.countDown();
    }

}
