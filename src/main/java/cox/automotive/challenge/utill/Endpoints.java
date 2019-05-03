package cox.automotive.challenge.utill;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Endpoints {
    private static final String BASE = "http://api.coxauto-interview.com/api";
    public static final String DATA_SET_API = BASE + "/datasetId";
    public static final String VEHICLE_IDS_API = BASE + "/{datasetId}/vehicles";
    public static final String VEHICLE_INFO_API = BASE + "/{datasetId}/vehicles/{vehicleid}";
    public static final String DEALER_DETAIL_API = BASE + "/{datasetId}/dealers/{dealerid}";
    public static final String ANSWER_API = BASE + "/{datasetId}/answer";

    @UtilityClass
    public static class Paths {
        public static final String DATASET_ID = "datasetId";
        public static final String ANSWER = "answer";
        public static final String DEALER_ID = "dealerid";
        public static final String VEHICLE_ID = "vehicleid";
    }
}
