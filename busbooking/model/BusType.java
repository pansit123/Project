
package busbooking.model;

public class BusType {

    private String busTypeId;
    private String busName;
    private int totalSeats;
    private double basePrice;

    public BusType(String busTypeId,
                   String busName,
                   int totalSeats,
                   double basePrice) {

        this.busTypeId = busTypeId;
        this.busName = busName;
        this.totalSeats = totalSeats;
        this.basePrice = basePrice;
    }

    public String getBusTypeId() {
        return busTypeId;
    }

    public String getBusName() {
        return busName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return busTypeId + " - " + busName;
    }
}
