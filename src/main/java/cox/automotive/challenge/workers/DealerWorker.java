package cox.automotive.challenge.workers;

import cox.automotive.challenge.data.Dealer;
import cox.automotive.challenge.utill.Endpoints;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static cox.automotive.challenge.utill.Endpoints.DEALER_DETAIL_API;

public class DealerWorker implements Runnable {
    private final String dataSetId;
    private final String dealerId;
    private final RestTemplate restTemplate;
    private Dealer dealer;

    public DealerWorker(final RestTemplate restTemplate, final String dataSetId, final String dealerId) {
        this.dataSetId = dataSetId;
        this.dealerId = dealerId;
        this.restTemplate = restTemplate;
    }

    public void run() {
        final Map<String, String> paramsDeaker = new HashMap<>();
        paramsDeaker.put(Endpoints.Paths.DATASET_ID, dataSetId);
        paramsDeaker.put(Endpoints.Paths.DEALER_ID, dealerId);
        this.dealer = restTemplate.getForEntity(DEALER_DETAIL_API, Dealer.class, paramsDeaker).getBody();
    }

    public Dealer getDealer() {
        return dealer;
    }
}
