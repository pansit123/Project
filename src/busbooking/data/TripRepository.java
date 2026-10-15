package busbooking.data;

import busbooking.model.BusType;
import busbooking.model.Route;
import busbooking.model.Trip;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * อ่านข้อมูลเส้นทาง / ประเภทรถ / เที่ยวรถ จากไฟล์ CSV เป็นอ็อบเจกต์ Route, BusType, Trip
 * (หน้าจอฝั่งผู้โดยสารและ BookingService ใช้คลาสนี้ ไม่ต้องแยกคอลัมน์ CSV เอง)
 *
 * ไม่มีไฟล์ = ยังไม่มีข้อมูล (คืนรายการว่าง) ส่วนอ่านไฟล์ไม่ได้ด้วยเหตุอื่นจะโยน DataException
 */
public class TripRepository {

    private final Path dir;
    private int skippedTrips;

    public TripRepository() {
        this(DataFiles.DIR);
    }

    /** ระบุโฟลเดอร์ข้อมูลเอง (ใช้ตอนทดสอบ) */
    public TripRepository(Path dir) {
        this.dir = dir;
    }

    /** เส้นทางทั้งหมด: รหัสเส้นทาง -> Route (เรียงตามลำดับในไฟล์) */
    public Map<String, Route> loadRoutes() {
        Map<String, Route> routes = new LinkedHashMap<>();
        for (String[] c : readRows(DataFiles.ROUTES.getFileName().toString())) {
            if (c.length >= 3 && !c[0].isEmpty()) {
                routes.put(c[0], new Route(c[0], c[1], c[2], c.length > 3 ? c[3] : "-"));
            }
        }
        return routes;
    }

    /** ประเภทรถทั้งหมด: รหัสประเภทรถ -> BusType (แถวที่จำนวนที่นั่งอ่านไม่ได้จะถูกข้าม) */
    public Map<String, BusType> loadBusTypes() {
        Map<String, BusType> types = new LinkedHashMap<>();
        for (String[] c : readRows(DataFiles.BUS_TYPES.getFileName().toString())) {
            try {
                int seats = Integer.parseInt(c[2]);
                if (!c[0].isEmpty() && seats > 0) {
                    types.put(c[0], new BusType(c[0], c[1], seats));
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                System.out.println("ข้ามประเภทรถที่อ่านไม่ได้: " + String.join(",", c));
            }
        }
        return types;
    }

    /**
     * เที่ยวรถทั้งหมด พร้อมเส้นทางและประเภทรถของแต่ละเที่ยว
     * แถวที่ข้อมูลผิด (วันที่/เวลา/ราคาอ่านไม่ได้ หรืออ้างเส้นทาง/ประเภทรถที่ไม่มี) จะถูกข้าม
     * และนับไว้ที่ getSkippedTrips() ให้หน้าจอแจ้งเตือนได้
     */
    public List<Trip> loadTrips() {
        Map<String, Route> routes = loadRoutes();
        Map<String, BusType> busTypes = loadBusTypes();
        List<Trip> trips = new ArrayList<>();
        skippedTrips = 0;
        for (String[] c : readRows(DataFiles.TRIP_SCHEDULES.getFileName().toString())) {
            Trip trip = toTrip(c, routes, busTypes);
            if (trip == null) {
                skippedTrips++;
            } else {
                trips.add(trip);
            }
        }
        return trips;
    }

    /** จำนวนแถวเที่ยวรถที่ถูกข้ามในการเรียก loadTrips() ครั้งล่าสุด */
    public int getSkippedTrips() {
        return skippedTrips;
    }

    private static Trip toTrip(String[] c, Map<String, Route> routes, Map<String, BusType> busTypes) {
        if (c.length < DataFiles.TRIP_COLUMNS) {
            return null;
        }
        Route route = routes.get(c[DataFiles.TRIP_ROUTE_ID]);
        BusType busType = busTypes.get(c[DataFiles.TRIP_BUS_TYPE_ID]);
        if (c[DataFiles.TRIP_ID].isEmpty() || route == null || busType == null) {
            return null;
        }
        try {
            double price = Double.parseDouble(c[DataFiles.TRIP_PRICE]);
            int seats = Integer.parseInt(c[DataFiles.TRIP_SEATS]);
            if (price < 0 || Double.isNaN(price) || Double.isInfinite(price) || seats < 0) {
                return null;
            }
            return new Trip(c[DataFiles.TRIP_ID],
                    LocalDate.parse(c[DataFiles.TRIP_DATE]),
                    LocalTime.parse(c[DataFiles.TRIP_TIME]),
                    price, seats, route, busType);
        } catch (DateTimeException | NumberFormatException ex) {
            return null;
        }
    }

    /**
     * เขียนจำนวนที่นั่งว่างใหม่ลง trip_schedules.csv (คอลัมน์ available_seats)
     * แก้เฉพาะแถวที่มีใน map และค่าเปลี่ยนจริง แถวอื่น (รวมแถวที่อ่านไม่ได้) คงไว้ตามเดิมทุกตัวอักษร
     *
     * @param availableByTripId รหัสเที่ยวรถ -> จำนวนที่นั่งว่าง
     */
    public void writeAvailableSeats(Map<String, Integer> availableByTripId) {
        Path file = dir.resolve(DataFiles.TRIP_SCHEDULES.getFileName());
        if (!Files.exists(file)) {
            return;
        }
        DataFiles.withLock(dir, () -> {
            List<String> lines = readLines(file);
            if (lines.isEmpty()) {
                return null;
            }
            boolean changed = false;
            List<String> body = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] c = line.split(",", -1);
                Integer seats = c.length >= DataFiles.TRIP_COLUMNS
                        ? availableByTripId.get(c[DataFiles.TRIP_ID].trim()) : null;
                if (seats != null && !c[DataFiles.TRIP_SEATS].trim().equals(String.valueOf(seats))) {
                    c[DataFiles.TRIP_SEATS] = String.valueOf(seats);
                    line = String.join(",", c);
                    changed = true;
                }
                body.add(line);
            }
            if (changed) {
                try {
                    DataFiles.writeCsv(file, lines.get(0), body);
                } catch (IOException e) {
                    throw new DataException("บันทึกไฟล์ trip_schedules.csv ไม่สำเร็จ: " + e.getMessage(), e);
                }
            }
            return null;
        });
    }

    // ================= ภายใน =================

    /** อ่านแถวข้อมูล (ข้ามหัวตารางและบรรทัดว่าง) โดยตัดช่องว่างและ BOM ออกจากทุกช่องแล้ว */
    private List<String[]> readRows(String fileName) {
        List<String> lines = readLines(dir.resolve(fileName));
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).trim().isEmpty()) {
                continue;
            }
            String[] c = lines.get(i).split(",", -1);
            for (int j = 0; j < c.length; j++) {
                c[j] = DataFiles.stripBom(c[j]).trim();
            }
            rows.add(c);
        }
        return rows;
    }

    private static List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return Collections.emptyList();
        } catch (IOException e) {
            throw new DataException("อ่านไฟล์ " + file.getFileName() + " ไม่สำเร็จ: " + e.getMessage(), e);
        }
    }
}
