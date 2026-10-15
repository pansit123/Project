package busbooking.ui.booking;

import busbooking.data.BookingService;
import busbooking.data.DataException;
import busbooking.data.TripRepository;
import busbooking.model.SeatLayout;
import busbooking.model.Trip;
import busbooking.ui.UiTheme;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SelectSeatGUI extends JFrame {

    // ผังที่นั่ง: แถวละ 4 ที่ (D C | ทางเดิน | B A) ตามจำนวนที่นั่งของรถ — กติกาชื่อที่นั่งอยู่ใน SeatLayout
    private static final String[] SEAT_LANES = SeatLayout.LANES;
    private static final int AISLE_AFTER_LANE = SeatLayout.AISLE_AFTER_LANE;
    private static final int SEAT_W = 45, SEAT_H = 35, GAP_X = 6, GAP_Y = 6, AISLE = 30;

    private static final Color GREEN = new Color(34, 139, 34);    // ว่าง
    private static final Color BLUE = new Color(135, 206, 250);   // เลือกอยู่
    private static final Color GRAY = new Color(128, 128, 128);   // จองแล้ว
    private static final Color NAVY = new Color(0, 51, 153);

    private final String userId;
    private final BookingService service = new BookingService();

    private final List<Trip> trips = new ArrayList<>();
    private final Set<String> selected = new LinkedHashSet<>();

    private final JComboBox<String> tripBox = new JComboBox<>();
    private final JLabel routeLabel = new JLabel("กรุณาเลือกเที่ยวรถ");
    private final JPanel seatPanel = new JPanel(null);
    private final JLabel summary = new JLabel("เลือกที่นั่ง");

    public SelectSeatGUI(String userId) {
        super("เลือกเที่ยวรถและที่นั่ง");
        this.userId = userId;
        setSize(900, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        loadTrips();

        tripBox.addActionListener(e -> refreshSeats());
        refreshSeats();
    }

    private void buildUI() {
        Container cp = getContentPane();
        cp.setLayout(null);
        cp.setBackground(new Color(230, 242, 255));

        JPanel header = new JPanel(null);
        header.setBackground(NAVY);
        header.setBounds(0, 0, 900, 45);
        JLabel title = new JLabel("  เลือกรถและที่นั่ง (SelectSeat)");
        title.setFont(new Font("Tahoma", Font.BOLD, 16));
        title.setForeground(Color.WHITE);
        title.setBounds(10, 8, 400, 30);
        header.add(title);
        cp.add(header);

        tripBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tripBox.setBounds(8, 55, 610, 30);
        cp.add(tripBox);

        routeLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        routeLabel.setBounds(8, 90, 610, 30);
        cp.add(routeLabel);

        JLabel seatTitle = new JLabel("กรุณาเลือกที่นั่ง (หน้ารถอยู่ทางซ้าย)");
        seatTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
        seatTitle.setBounds(8, 122, 400, 25);
        cp.add(seatTitle);

        // รถคันใหญ่มีหลายคอลัมน์ เลื่อนดูด้านข้างได้
        seatPanel.setOpaque(false);
        JScrollPane seatScroll = new JScrollPane(seatPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        seatScroll.setBounds(8, 150, 610, 4 * (SEAT_H + GAP_Y) + AISLE + 30);
        seatScroll.getViewport().setBackground(Color.WHITE);
        seatScroll.getHorizontalScrollBar().setUnitIncrement(SEAT_W);
        cp.add(seatScroll);

        addLegend(cp, 30, 405, GREEN, "AVAILABLE");
        addLegend(cp, 150, 405, BLUE, "SELECTED");
        addLegend(cp, 270, 405, GRAY, "BOOKED");

        summary.setFont(new Font("Tahoma", Font.BOLD, 15));
        summary.setVerticalAlignment(SwingConstants.TOP);
        summary.setBounds(640, 60, 240, 200);
        cp.add(summary);

        JButton book = new JButton("สร้างการจอง (createBooking)");
        book.setBackground(NAVY);
        book.setForeground(Color.WHITE);
        book.setFont(new Font("Tahoma", Font.BOLD, 12));
        book.setBounds(650, 385, 220, 60);
        book.addActionListener(e -> book());
        cp.add(book);
    }

    private void addLegend(Container cp, int x, int y, Color color, String name) {
        JPanel box = new JPanel();
        box.setBackground(color);
        box.setBounds(x, y, 20, 15);
        cp.add(box);

        JLabel label = new JLabel(name);
        label.setFont(new Font("Tahoma", Font.PLAIN, 12));
        label.setBounds(x + 30, y - 3, 90, 20);
        cp.add(label);
    }

    /** โหลดเที่ยวรถที่ยังไม่ออก ใส่ลงใน tripBox (เรียงตามวันเวลาออกรถ) */
    private void loadTrips() {
        trips.clear();
        tripBox.removeAllItems();
        TripRepository repo = new TripRepository();
        try {
            service.refreshAvailableSeats();   // ให้ตัวเลขในไฟล์ตรงกับการจองล่าสุด
        } catch (DataException ex) {
            System.out.println("อัปเดตที่นั่งว่างไม่สำเร็จ: " + ex.getMessage());
        }
        try {
            for (Trip t : repo.loadTrips()) {
                if (departure(t).isAfter(LocalDateTime.now())) {
                    trips.add(t);
                }
            }
        } catch (DataException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "อ่านข้อมูลเที่ยวรถไม่ได้", JOptionPane.ERROR_MESSAGE);
            return;
        }
        trips.sort(Comparator.comparing(SelectSeatGUI::departure));
        for (Trip t : trips) {
            tripBox.addItem(tripLabel(t));
        }
        if (repo.getSkippedTrips() > 0) {
            JOptionPane.showMessageDialog(this, "ข้ามเที่ยวรถที่ข้อมูลไม่ถูกต้อง " + repo.getSkippedTrips()
                            + " รายการ\nกรุณาให้ Admin ตรวจไฟล์ trip_schedules.csv",
                    "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String tripLabel(Trip t) {
        return t.getTripId() + " | " + routeName(t) + " | " + t.getTravelDate() + " " + t.getDepartureTime()
                + " | " + UiTheme.money(t.getPrice())
                + " | ว่าง " + service.getAvailableSeats(t) + "/" + t.getBusType().getTotalSeats();
    }

    private static LocalDateTime departure(Trip t) {
        return LocalDateTime.of(t.getTravelDate(), t.getDepartureTime());
    }

    private static String routeName(Trip t) {
        return t.getRoute().getOrigin() + " → " + t.getRoute().getDestination();
    }

    // วาดผังที่นั่งใหม่ทุกครั้งที่เปลี่ยนเที่ยวรถ หรือหลังจองเสร็จ
    private void refreshSeats() {
        selected.clear();
        seatPanel.removeAll();

        int idx = tripBox.getSelectedIndex();
        if (idx < 0) {
            routeLabel.setText("ไม่มีเที่ยวรถ");
            summary.setText("<html>ไม่มีเที่ยวรถในอนาคต<br>กรุณาให้ Admin เพิ่มเที่ยวรถ</html>");
            seatPanel.setPreferredSize(new Dimension(0, 0));
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        Trip trip = trips.get(idx);
        routeLabel.setText(routeName(trip) + "   (" + trip.getBusType().getBusTypeName() + " "
                + trip.getBusType().getTotalSeats() + " ที่นั่ง)");
        Set<String> booked = new HashSet<>();
        for (String s : service.getBookedSeats(trip.getRoute().getRouteId(), trip.getTravelDate(),
                trip.getDepartureTime())) {
            booked.add(s.toUpperCase());
        }

        int seats = trip.getBusType().getTotalSeats();
        int columns = (seats + SEAT_LANES.length - 1) / SEAT_LANES.length;
        for (int i = 0; i < seats; i++) {
            int lane = i % SEAT_LANES.length;
            int col = i / SEAT_LANES.length;
            String seat = SeatLayout.seatName(i);
            boolean taken = booked.contains(seat);

            JToggleButton btn = new JToggleButton(seat);
            btn.setFont(new Font("Tahoma", Font.BOLD, 11));
            btn.setMargin(new Insets(0, 0, 0, 0));
            int aisle = lane > AISLE_AFTER_LANE ? AISLE : 0;
            btn.setBounds(10 + col * (SEAT_W + GAP_X), 10 + lane * (SEAT_H + GAP_Y) + aisle, SEAT_W, SEAT_H);
            btn.setEnabled(!taken);
            btn.setOpaque(true);
            btn.setFocusPainted(false);
            btn.setBackground(taken ? GRAY : GREEN);
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> toggleSeat(btn, seat, trip));
            seatPanel.add(btn);
        }
        seatPanel.setPreferredSize(new Dimension(20 + columns * (SEAT_W + GAP_X),
                20 + SEAT_LANES.length * (SEAT_H + GAP_Y) + AISLE));

        updateSummary(trip);
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void toggleSeat(JToggleButton btn, String seat, Trip trip) {
        if (btn.isSelected()) {
            selected.add(seat);
            btn.setBackground(BLUE);
            btn.setForeground(Color.BLACK);
        } else {
            selected.remove(seat);
            btn.setBackground(GREEN);
            btn.setForeground(Color.WHITE);
        }
        updateSummary(trip);
    }

    private void updateSummary(Trip trip) {
        double total = selected.size() * trip.getPrice();
        summary.setText("<html>ที่นั่งว่าง: " + service.getAvailableSeats(trip) + "/"
                + trip.getBusType().getTotalSeats()
                + "<br><br>ที่นั่งที่เลือก: " + selected.size()
                + (selected.isEmpty() ? "" : "<br>(" + String.join(", ", selected) + ")")
                + "<br><br>ราคาสุทธิ: " + UiTheme.money(total) + "</html>");
    }

    private void book() {
        int idx = tripBox.getSelectedIndex();
        if (idx < 0) {
            return;
        }
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณาเลือกที่นั่งก่อน");
            return;
        }

        Trip trip = trips.get(idx);
        new CreateBookingGUI(userId, trip.getRoute().getRouteId(), routeName(trip),
                trip.getTravelDate(), trip.getDepartureTime(),
                new ArrayList<>(selected), trip.getPrice(),
                service, this::afterBooking).setVisible(true);
    }

    /** หลังจองเสร็จ/จองไม่สำเร็จ: อัปเดตจำนวนที่นั่งว่างในรายการเที่ยวรถ แล้ววาดผังใหม่ */
    private void afterBooking() {
        int idx = tripBox.getSelectedIndex();
        if (idx >= 0) {
            ((DefaultComboBoxModel<String>) tripBox.getModel()).removeElementAt(idx);
            tripBox.insertItemAt(tripLabel(trips.get(idx)), idx);
            tripBox.setSelectedIndex(idx);   // ทำให้ refreshSeats ทำงานผ่าน listener
        } else {
            refreshSeats();
        }
    }
}
