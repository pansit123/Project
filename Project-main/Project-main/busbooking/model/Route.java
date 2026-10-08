
package busbooking.model;

public class Route {

    private String routeId;
    private String origin;
    private String destination;
    private String stop;

    public Route(String routeId, String origin,
                 String destination, String stop) {

        this.routeId = routeId;
        this.origin = origin;
        this.destination = destination;
        this.stop = stop;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getStop() {
        return stop;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    @Override
    public String toString() {
        return routeId + " : "
                + origin + " - "
                + destination;
    }
}