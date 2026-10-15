
package busbooking.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * เที่ยวรถ 1 เที่ยว (1 แถวใน trip_schedules.csv) พร้อมเส้นทางและประเภทรถ
 * สร้างโดย busbooking.data.TripRepository
 */
public class Trip {

    private String tripId;
    private LocalDate travelDate;
    private LocalTime departureTime;
    private double price;
    private int availableSeats;

    private Route route;
    private BusType busType;

    public Trip(String tripId,
                LocalDate travelDate,
                LocalTime departureTime,
                double price,
                int availableSeats,
                Route route,
                BusType busType) {

        this.tripId = tripId;
        this.travelDate = travelDate;
        this.departureTime = departureTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.route = route;
        this.busType = busType;
    }

    public String getTripId() {
        return tripId;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public Route getRoute() {
        return route;
    }

    public BusType getBusType() {
        return busType;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public boolean checkAvailability() {
        return availableSeats > 0;
    }

    @Override
    public String toString() {
        return tripId + " | "
                + route.getOrigin() + " - "
                + route.getDestination()
                + " | " + travelDate
                + " | " + departureTime;
    }
}