package cox.automotive.challenge.data;

import java.util.List;

public class Dealer {

    private String dealerId;
    private String name;
    private List<Vehicle> vehicles;

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Dealer(String dealerId, String name, List<Vehicle> vehicles) {
        this.dealerId = dealerId;
        this.vehicles = vehicles;
        this.name = name;
    }

    public String getDealerId() {
        return dealerId;
    }
}
