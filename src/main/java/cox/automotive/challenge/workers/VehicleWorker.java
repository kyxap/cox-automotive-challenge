package cox.automotive.challenge.workers;

import cox.automotive.challenge.utill.Endpoints;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import static cox.automotive.challenge.utill.Endpoints.VEHICLE_INFO_API;

public class VehicleWorker implements Callable<VehicleWorker> {
    private final String dataSetId;
    private final String vehicleid;
    private final RestTemplate restTemplate;
    private String vehicleInfo;

    public VehicleWorker(final RestTemplate restTemplate, final String dataSetId, final String vehicleid) {
        this.dataSetId = dataSetId;
        this.vehicleid = vehicleid;
        this.restTemplate = restTemplate;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    @Override
    public VehicleWorker call() {

        final Map<String, String> paramsForVechInfo = new HashMap<>();
        paramsForVechInfo.put(Endpoints.Paths.DATASET_ID, dataSetId);
        paramsForVechInfo.put(Endpoints.Paths.VEHICLE_ID, vehicleid);
        this.vehicleInfo = restTemplate.getForObject(VEHICLE_INFO_API, String.class, paramsForVechInfo);
        return null;
    }
}
