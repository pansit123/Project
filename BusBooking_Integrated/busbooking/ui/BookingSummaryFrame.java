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
        tableModel.setRowCount(0);
        int total = 0, booked = 0, cancelled = 0;
        Path path = findDataFile("bookings.csv");
        try {
            if (Files.exists(path)) {
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {
                    String[] c = lines.get(i).split(",", -1);
                    if (c.length < 9) continue;
                    String status = c[7].trim();
                    tableModel.addRow(new Object[]{c[0], c[2] + " / " + c[3] + " " + c[4], c[5], c[1], status});
                    total++;
                    if (status.equals("PAID")) booked++;
                    if (status.equals("CANCELLED")) cancelled++;
                }
            }
            lblTotal.setText("จำนวนการจองทั้งหมด: " + total);
            lblBooked.setText("ชำระเงินแล้ว: " + booked);
            lblCancelled.setText("ยกเลิกแล้ว: " + cancelled);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "อ่านข้อมูลการจองไม่ได้: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookingSummaryFrame::new);
    }
}
