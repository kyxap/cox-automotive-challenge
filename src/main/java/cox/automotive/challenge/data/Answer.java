package cox.automotive.challenge.data;

import java.util.ArrayList;
import java.util.List;

public class Answer {
    public void addDealer(final Dealer dealer) {
        this.dealers.add(dealer);
    }

    private final List<Dealer> dealers = new ArrayList<>();

}
