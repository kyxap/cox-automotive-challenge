package cox.automotive.challenge;

import com.google.gson.Gson;
import cox.automotive.challenge.data.Answer;
import cox.automotive.challenge.data.Dealer;
import cox.automotive.challenge.data.Vehicle;
import cox.automotive.challenge.data.VehicleIds;
import cox.automotive.challenge.utill.RestHelper;
import cox.automotive.challenge.workers.DealerWorker;
import cox.automotive.challenge.workers.VehicleWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
    private static final int READ_TIMEOUT = 4500;
    private static final int CONN_TIMEOUT = 5000;
    private static final String MAX_CONNECTION = "10";

    public static void main(final String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        System.setProperty("http.maxConnections", MAX_CONNECTION);

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setReadTimeout(READ_TIMEOUT);
        requestFactory.setConnectTimeout(CONN_TIMEOUT);

        return new RestTemplate(requestFactory);
    }

    @Bean
    public RestHelper restHelper() {
        return new RestHelper(restTemplate());
    }

    @Bean
    public String run(final RestTemplate restTemplate) throws InterruptedException {
        final Gson g = new Gson();

        // Get dataSetID <FAST>
        final String dataSetId = restHelper().getDataSetId();

        final VehicleIds vehicleIds = restHelper().getVehicleIds(dataSetId);

        // Get id for each Vehicle <SLOW>
        final List<Vehicle> vehicleList = new ArrayList<>();
        final List<Callable<VehicleWorker>> vehicleWorkerCallableList = new ArrayList<>();
        final ExecutorService executor = Executors.newFixedThreadPool(vehicleIds.getVehicleIds().length);

        for (final String id : vehicleIds.getVehicleIds()) {
            final VehicleWorker newV = new VehicleWorker(restTemplate, dataSetId, id);
            vehicleWorkerCallableList.add(newV);
        }

        executor.invokeAll(vehicleWorkerCallableList);

        for (final Callable<VehicleWorker> v : vehicleWorkerCallableList) {
            vehicleList.add(g.fromJson(((VehicleWorker) v).getVehicleInfo(), Vehicle.class));
        }

        // create dealer to vehicles list <FAST NO CALLS>
        final Map<String, List<Vehicle>> dealerIdToVehicle = new HashMap<>();
        for (final Vehicle v : vehicleList) {
            final String dealerId = v.getDealerId();
            dealerIdToVehicle.computeIfAbsent(dealerId, k -> new ArrayList<>()).add(v);
        }

        // Get Dealer Name and create answer <SLOW BC of Dealer name>
        final Answer answer = new Answer();
        final List<Callable<DealerWorker>> dealerWorkerCallableList = new ArrayList<>();
        for (final Map.Entry<String, List<Vehicle>> entry : dealerIdToVehicle.entrySet()) {
            final DealerWorker newD = new DealerWorker(restTemplate, dataSetId, entry.getKey());
            dealerWorkerCallableList.add(newD);
        }

        executor.invokeAll(dealerWorkerCallableList);
        executor.shutdown();

        for (final Callable<DealerWorker> dW : dealerWorkerCallableList) {
            final Dealer dealer = ((DealerWorker) dW).getDealer();
            dealer.setVehicles(dealerIdToVehicle.get(dealer.getDealerId()));
            answer.addDealer(dealer);
        }

        // Submit answer and get results
        final String completeRez = restHelper().submitAnswer(dataSetId, g.toJson(answer));
        LOGGER.info("Answer: " + completeRez);

        return completeRez;
    }
}
