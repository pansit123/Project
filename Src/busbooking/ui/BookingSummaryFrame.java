package busbooking.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BookingSummaryFrame extends JFrame {
    private JTable bookingTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JLabel lblBooked;
    private JLabel lblCancelled;

    public BookingSummaryFrame() {
        setTitle("สรุปรายการจองตั๋ว - Booking Summary");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        createUI();
        loadBookings();
        setVisible(true);
    }

    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        JLabel title = new JLabel("รายการจองตั๋วทั้งหมด", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));
        add(title, BorderLayout.NORTH);

        String[] columns = {"รหัสการจอง", "รหัสเที่ยวรถ", "ที่นั่ง", "ชื่อผู้โดยสาร", "สถานะ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        bookingTable.setFont(new Font("Tahoma", Font.PLAIN, 15));
        bookingTable.setRowHeight(30);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setAutoCreateRowSorter(true);
        bookingTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 15));
        add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        lblTotal = new JLabel("จำนวนการจองทั้งหมด: 0");
        lblBooked = new JLabel("จองสำเร็จ: 0");
        lblCancelled = new JLabel("ยกเลิกแล้ว: 0");
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblBooked.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblCancelled.setFont(new Font("Tahoma", Font.PLAIN, 15));
        summaryPanel.add(lblTotal);
        summaryPanel.add(lblBooked);
        summaryPanel.add(lblCancelled);
        bottomPanel.add(summaryPanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("รีเฟรช");
        JButton btnClose = new JButton("ปิดหน้าต่าง");
        btnRefresh.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnClose.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnRefresh.addActionListener(e -> loadBookings());
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClose);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private Path findDataFile(String filename) {
        Path[] paths = {
            Paths.get("busbooking", "data", filename),
            Paths.get("src", "busbooking", "data", filename),
            Paths.get("Src", "busbooking", "data", filename)
        };
        for (Path path : paths) {
            if (Files.isRegularFile(path)) return path;
        }
        return paths[0];
    }

    private Set<String> loadTripIds() throws IOException {
        Path path = findDataFile("trip_schedules.csv");
        if (!Files.isRegularFile(path)) {
            throw new IOException("ไม่พบไฟล์ trip_schedules.csv: " + path.toAbsolutePath());
        }
        Set<String> tripIds = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !header.replace("\uFEFF", "").trim().equals(
                    "trip_id,route_id,bus_type_id,travel_date,departure_time,price,available_seats")) {
                throw new IOException("หัวตาราง trip_schedules.csv ไม่ถูกต้อง");
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length != 7 || fields[0].trim().isEmpty()) {
                    throw new IOException("ข้อมูลเที่ยวรถไม่ถูกต้องที่บรรทัด " + lineNumber);
                }
                tripIds.add(fields[0].trim().toUpperCase(Locale.ROOT));
            }
        }
        return tripIds;
    }

    private void loadBookings() {
        Path filePath = findDataFile("bookings.csv");
        if (!Files.isRegularFile(filePath)) {
            JOptionPane.showMessageDialog(this,
                    "ไม่พบไฟล์ bookings.csv\n" + filePath.toAbsolutePath(),
                    "ไม่พบข้อมูล", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> bookings = new ArrayList<>();
        Set<String> missingTripIds = new LinkedHashSet<>();
        int bookedCount = 0;
        int cancelledCount = 0;
        try {
            Set<String> tripIds = loadTripIds();
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                String header = reader.readLine();
                if (header == null || !header.replace("\uFEFF", "").trim().equals(
                        "booking_id,trip_id,seat,customer_name,status")) {
                    throw new IOException("หัวตาราง bookings.csv ไม่ถูกต้อง");
                }
                String line;
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.trim().isEmpty()) continue;
                    String[] values = line.split(",", -1);
                    if (values.length != 5) {
                        throw new IOException("จำนวนคอลัมน์ไม่ถูกต้องที่บรรทัด " + lineNumber);
                    }
                    String tripId = values[1].trim();
                    if (!tripIds.contains(tripId.toUpperCase(Locale.ROOT))) {
                        missingTripIds.add(tripId.isEmpty() ? "(ว่าง)" : tripId);
                    }
                    String status = values[4].trim();
                    if (status.equalsIgnoreCase("BOOKED")) bookedCount++;
                    else if (status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("CANCELED")) cancelledCount++;
                    bookings.add(values);
                }
            }

            tableModel.setRowCount(0);
            for (String[] booking : bookings) tableModel.addRow(booking);
            lblTotal.setText("จำนวนการจองทั้งหมด: " + bookings.size());
            lblBooked.setText("จองสำเร็จ: " + bookedCount);
            lblCancelled.setText("ยกเลิกแล้ว: " + cancelledCount);

            if (!missingTripIds.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "พบรายการจองที่อ้างอิงเที่ยวรถที่ไม่มีใน trip_schedules.csv\n"
                        + "Trip ID: " + String.join(", ", missingTripIds)
                        + "\n\nกรุณาตรวจสอบข้อมูลกับฝั่ง User",
                        "ตรวจพบข้อมูลไม่ตรงกัน", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "อ่านหรือตรวจสอบข้อมูลไม่สำเร็จ:\n" + ex.getMessage(),
                    "เกิดข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookingSummaryFrame::new);
    }
}
