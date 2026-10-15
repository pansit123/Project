
package busbooking.model;

public class BusType {

    // ข้อมูลประเภทรถบัส
    private String busTypeId;
    private String busTypeName;
    private int totalSeats;

    // Constructor
    public BusType(String busTypeId, String busTypeName, int totalSeats) {
        this.busTypeId = busTypeId;
        this.busTypeName = busTypeName;
        this.totalSeats = totalSeats;
    }

    // Constructor ว่าง
    public BusType() {
    }

    // Getter
    public String getBusTypeId() {
        return busTypeId;
    }

    public String getBusTypeName() {
        return busTypeName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    // Setter
    public void setBusTypeId(String busTypeId) {
        this.busTypeId = busTypeId;
    }

    public void setBusTypeName(String busTypeName) {
        this.busTypeName = busTypeName;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    // แสดงข้อมูลประเภทรถ
    @Override
    public String toString() {
        return busTypeId + " - " + busTypeName
                + " (" + totalSeats + " ที่นั่ง)";
    }
}
