package busbooking.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class ManageTripFrame extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Trip ID", "Route ID", "Bus Type ID", "วันที่", "เวลา", "ราคา", "ที่นั่งว่าง"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField id = new JTextField(), date = new JTextField(), time = new JTextField(),
            price = new JTextField(), seats = new JTextField();
    private final JComboBox<String> routes = new JComboBox<>(), busTypes = new JComboBox<>();
    private final Path schedulePath = locate("trip_schedules.csv");
    private boolean dataValid = true;

    public ManageTripFrame() {
        setTitle("จัดการเที่ยวรถ - Trip");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        loadReferences();
        loadTrips();
        setVisible(true);
    }

    private static Path locate(String filename) {
        for (String base : new String[]{"busbooking/data", "src/busbooking/data", "Src/busbooking/data"}) {
            Path p = Paths.get(base, filename);
            if (Files.exists(p)) return p;
        }
        return Paths.get("busbooking", "data", filename);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("จัดการเที่ยวรถ (Trip)", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 28));
        title.setForeground(new Color(0, 90, 160));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        addField(form, 0, "Trip ID", id);
        addField(form, 1, "Route ID", routes);
        addField(form, 2, "Bus Type ID", busTypes);
        addField(form, 3, "วันที่ (YYYY-MM-DD)", date);
        addField(form, 4, "เวลา (HH:mm)", time);
        addField(form, 5, "ราคา", price);
        addField(form, 6, "ที่นั่งว่าง", seats);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBackground(Color.WHITE);
        left.setBorder(BorderFactory.createLineBorder(new Color(190, 205, 225)));
        JLabel leftHeading = new JLabel("ข้อมูลเที่ยวรถ");
        leftHeading.setFont(new Font("Tahoma", Font.BOLD, 16));
        leftHeading.setForeground(new Color(0, 90, 160));
        leftHeading.setBorder(BorderFactory.createEmptyBorder(8, 10, 5, 10));
        left.add(leftHeading, BorderLayout.NORTH);
        left.add(form, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new GridLayout(4, 1, 0, 8));
        buttons.setBackground(Color.WHITE);
        JButton add = button("เพิ่มเที่ยวรถ", new Color(40, 180, 70));
        JButton edit = button("แก้ไข", new Color(30, 120, 200));
        JButton delete = button("ลบ", new Color(220, 50, 50));
        JButton back = button("กลับหน้าหลัก", new Color(100, 100, 100));
        for (JButton b : new JButton[]{add, edit, delete, back}) buttons.add(b);
        left.add(buttons, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createLineBorder(new Color(190, 205, 225)));
        JLabel rightHeading = new JLabel("รายการเที่ยวรถ");
        rightHeading.setFont(new Font("Tahoma", Font.BOLD, 16));
        rightHeading.setForeground(new Color(0, 90, 160));
        rightHeading.setBorder(BorderFactory.createEmptyBorder(8, 10, 5, 10));
        right.add(rightHeading, BorderLayout.NORTH);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {90, 95, 115, 125, 75, 90, 95};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        center.setResizeWeight(0.35);
        center.setDividerSize(8);
        center.setBorder(null);
        center.setDividerLocation(420);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        add.addActionListener(e -> addTrip());
        edit.addActionListener(e -> editTrip());
        delete.addActionListener(e -> deleteTrip());
        back.addActionListener(e -> dispose());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || table.getSelectedRow() < 0) return;
            int r = table.convertRowIndexToModel(table.getSelectedRow());
            id.setText(value(r, 0));
            routes.setSelectedItem(value(r, 1));
            busTypes.setSelectedItem(value(r, 2));
            date.setText(value(r, 3));
            time.setText(value(r, 4));
            price.setText(value(r, 5));
            seats.setText(value(r, 6));
        });
    }

    private void addField(JPanel p, int y, String name, JComponent input) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = y;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 4, 6, 8);
        JLabel label = new JLabel(name);
        label.setFont(new Font("Tahoma", Font.PLAIN, 15));
        p.add(label, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        input.setFont(new Font("Tahoma", Font.PLAIN, 15));
        input.setPreferredSize(new Dimension(220, 36));
        p.add(input, c);
    }

    private JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 15));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(250, 42));
        return b;
    }

    private String value(int r, int c) { return model.getValueAt(r, c).toString(); }
    private void warning(String message) { JOptionPane.showMessageDialog(this, message, "แจ้งเตือน", JOptionPane.WARNING_MESSAGE); }

    private void loadReferences() {
        loadIds(locate("routes.csv"), routes, "route_id");
        loadIds(locate("bustypes.csv"), busTypes, "bus_type_id");
    }

    private void loadIds(Path path, JComboBox<String> combo, String expectedHeader) {
        combo.removeAllItems();
        if (!Files.exists(path)) { warning("ไม่พบไฟล์ " + path.toAbsolutePath()); return; }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !header.replace("\uFEFF", "").split(",", -1)[0].trim().equals(expectedHeader)) {
                warning("หัวคอลัมน์ไฟล์ไม่ตรง: " + path.getFileName());
                return;
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (!parts[0].trim().isEmpty()) combo.addItem(parts[0].trim());
            }
        } catch (IOException ex) { warning("อ่าน " + path.getFileName() + " ไม่สำเร็จ: " + ex.getMessage()); }
    }

    private void loadTrips() {
        model.setRowCount(0);
        dataValid = true;
        if (!Files.exists(schedulePath)) {
            warning("ไม่พบ trip_schedules.csv ที่ " + schedulePath.toAbsolutePath());
            dataValid = false;
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(schedulePath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !header.replace("\uFEFF", "").trim().equals(
                    "trip_id,route_id,bus_type_id,travel_date,departure_time,price,available_seats")) {
                throw new IOException("หัวคอลัมน์ไม่ตรงกับรูปแบบที่กำหนด");
            }
            String line;
            int lineNumber = 1;
            Set<String> seen = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != 7) throw new IOException("คอลัมน์ไม่ครบที่บรรทัด " + lineNumber);
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
                if (!seen.add(parts[0].toLowerCase(Locale.ROOT))) throw new IOException("Trip ID ซ้ำที่บรรทัด " + lineNumber);
                if (parts[0].isEmpty() || !comboContains(routes, parts[1]) || !comboContains(busTypes, parts[2]))
                    throw new IOException("Trip/Route/Bus Type ID ไม่ถูกต้องที่บรรทัด " + lineNumber);
                LocalDate.parse(parts[3]);
                LocalTime.parse(parts[4]);
                if (Double.parseDouble(parts[5]) < 0 || Integer.parseInt(parts[6]) < 0)
                    throw new IOException("ราคา/ที่นั่งติดลบที่บรรทัด " + lineNumber);
                model.addRow(parts);
            }
        } catch (IOException | DateTimeParseException | NumberFormatException ex) {
            dataValid = false;
            warning("อ่าน trip_schedules.csv ไม่สำเร็จ: " + ex.getMessage() + "\nกรุณาแก้ไฟล์ก่อนบันทึก");
        }
    }

    private boolean comboContains(JComboBox<String> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) if (combo.getItemAt(i).equals(id)) return true;
        return false;
    }

    private String[] formData() {
        String tripId = id.getText().trim();
        String routeId = (String) routes.getSelectedItem();
        String busTypeId = (String) busTypes.getSelectedItem();
        String d = date.getText().trim(), t = time.getText().trim();
        String p = price.getText().trim(), s = seats.getText().trim();
        if (tripId.isEmpty() || routeId == null || busTypeId == null || d.isEmpty() || t.isEmpty() || p.isEmpty() || s.isEmpty()) {
            warning("กรุณากรอกข้อมูลให้ครบ และตรวจสอบ Route/Bus Type"); return null;
        }
        if (tripId.contains(",") || tripId.contains("\n") || tripId.contains("\r")) {
            warning("Trip ID ห้ามมีจุลภาคหรือขึ้นบรรทัดใหม่"); return null;
        }
        try {
            d = LocalDate.parse(d).toString();
            t = LocalTime.parse(t).toString();
            double priceValue = Double.parseDouble(p);
            int seatValue = Integer.parseInt(s);
            if (!Double.isFinite(priceValue) || priceValue < 0 || seatValue < 0) throw new NumberFormatException();
            return new String[]{tripId, routeId, busTypeId, d, t, Double.toString(priceValue), Integer.toString(seatValue)};
        } catch (DateTimeParseException | NumberFormatException ex) {
            warning("วันที่ใช้ YYYY-MM-DD, เวลา HH:mm, ราคาต้องไม่ติดลบ และที่นั่งต้องเป็นจำนวนเต็มไม่ติดลบ");
            return null;
        }
    }

    private boolean duplicate(String id, int except) {
        for (int i = 0; i < model.getRowCount(); i++)
            if (i != except && value(i, 0).equalsIgnoreCase(id)) return true;
        return false;
    }

    private boolean saveTrips() {
        if (!dataValid) { warning("ข้อมูล CSV เดิมไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้าต่างใหม่"); return false; }
        Path temp = null;
        try {
            Path parent = schedulePath.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            temp = Files.createTempFile(parent, "trip-schedules-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                writer.write("trip_id,route_id,bus_type_id,travel_date,departure_time,price,available_seats");
                writer.newLine();
                for (int i = 0; i < model.getRowCount(); i++) {
                    StringJoiner joiner = new StringJoiner(",");
                    for (int j = 0; j < 7; j++) joiner.add(value(i, j));
                    writer.write(joiner.toString());
                    writer.newLine();
                }
            }
            Files.move(temp, schedulePath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            warning("บันทึก CSV ไม่สำเร็จ: " + ex.getMessage());
            return false;
        } finally {
            if (temp != null) try { Files.deleteIfExists(temp); } catch (IOException ignored) { }
        }
    }

    private void addTrip() {
        if (!dataValid) { warning("แก้ไข CSV เดิมก่อน"); return; }
        String[] data = formData();
        if (data == null) return;
        if (duplicate(data[0], -1)) { warning("Trip ID ซ้ำ"); return; }
        model.addRow(data);
        if (saveTrips()) { JOptionPane.showMessageDialog(this, "เพิ่มเที่ยวรถแล้ว"); clear(); }
        else loadTrips();
    }

    private boolean hasActiveBookings(String tripId) throws IOException {
        Path bookingsPath = locate("bookings.csv");
        if (!Files.exists(bookingsPath)) return false;
        try (BufferedReader reader = Files.newBufferedReader(bookingsPath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !header.replace("\uFEFF", "").trim().equals(
                    "booking_id,trip_id,seat,customer_name,status")) {
                throw new IOException("หัวคอลัมน์ bookings.csv ไม่ถูกต้อง");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] fields = line.split(",", -1);
                if (fields.length != 5) throw new IOException("ข้อมูล bookings.csv ไม่ครบ 5 คอลัมน์");
                String status = fields[4].trim();
                if (fields[1].trim().equalsIgnoreCase(tripId)
                        && !status.equalsIgnoreCase("CANCELLED")
                        && !status.equalsIgnoreCase("CANCELED")) return true;
            }
        }
        return false;
    }

    private void editTrip() {
        if (!dataValid) { warning("แก้ไข CSV เดิมก่อน"); return; }
        int view = table.getSelectedRow();
        if (view < 0) { warning("กรุณาเลือกเที่ยวรถ"); return; }
        int row = table.convertRowIndexToModel(view);
        String[] data = formData();
        if (data == null) return;
        if (duplicate(data[0], row)) { warning("Trip ID ซ้ำ"); return; }
        String oldTripId = value(row, 0);
        if (!oldTripId.equalsIgnoreCase(data[0])) {
            try {
                if (hasActiveBookings(oldTripId)) {
                    warning("ไม่สามารถเปลี่ยน Trip ID ได้ เพราะมีรายการจองของเที่ยวรถนี้อยู่");
                    return;
                }
            } catch (IOException ex) {
                warning("ตรวจสอบรายการจองไม่สำเร็จ: " + ex.getMessage());
                return;
            }
        }
        for (int i = 0; i < 7; i++) model.setValueAt(data[i], row, i);
        if (saveTrips()) { JOptionPane.showMessageDialog(this, "แก้ไขเที่ยวรถแล้ว"); clear(); }
        else loadTrips();
    }

    private void deleteTrip() {
        if (!dataValid) { warning("แก้ไข CSV เดิมก่อน"); return; }
        int view = table.getSelectedRow();
        if (view < 0) { warning("กรุณาเลือกเที่ยวรถ"); return; }
        int row = table.convertRowIndexToModel(view);
        try {
            if (hasActiveBookings(value(row, 0))) {
                warning("ไม่สามารถลบเที่ยวรถได้ เพราะมีรายการจองอยู่");
                return;
            }
        } catch (IOException ex) {
            warning("ตรวจสอบรายการจองไม่สำเร็จ: " + ex.getMessage());
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "ลบเที่ยวรถ " + value(row, 0) + " ?", "ยืนยันการลบ",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        model.removeRow(row);
        if (saveTrips()) { JOptionPane.showMessageDialog(this, "ลบเที่ยวรถแล้ว"); clear(); }
        else loadTrips();
    }

    private void clear() {
        table.clearSelection();
        id.setText(""); date.setText(""); time.setText(""); price.setText(""); seats.setText("");
        id.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageTripFrame::new);
    }
}
