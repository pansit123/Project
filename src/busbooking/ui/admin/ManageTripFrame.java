package busbooking.ui.admin;

import busbooking.data.BookingService;
import busbooking.data.DataException;
import busbooking.data.DataFiles;
import busbooking.data.TripRepository;
import busbooking.model.BusType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class ManageTripFrame extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Trip ID", "Route ID", "Bus Type ID", "วันที่", "เวลา", "ราคา", "ที่นั่งว่าง"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = AdminUi.table(model, 90, 95, 115, 125, 75, 90, 95);
    private final JTextField id = new JTextField(), date = new JTextField(), time = new JTextField(),
            price = new JTextField(), seats = new JTextField();
    private final JComboBox<String> routes = new JComboBox<>(), busTypes = new JComboBox<>();
    private final Path schedulePath = DataFiles.TRIP_SCHEDULES;
    private final BookingService service = new BookingService();
    private Map<String, BusType> busTypeById = new LinkedHashMap<>();
    private boolean dataValid = true;

    public ManageTripFrame() {
        setTitle("จัดการเที่ยวรถ - Trip");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        buildUI();
        loadReferences();
        refreshAvailableSeats();
        loadTrips();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        root.add(AdminUi.title("จัดการเที่ยวรถ (Trip)"), BorderLayout.NORTH);

        // ที่นั่งว่างคำนวณให้อัตโนมัติ = ที่นั่งของประเภทรถ - ที่นั่งที่ถูกจอง (แก้เองไม่ได้)
        seats.setEditable(false);
        seats.setToolTipText("คำนวณอัตโนมัติจากประเภทรถและการจอง");
        busTypes.addActionListener(e -> previewSeats());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        AdminUi.addFormRow(form, 0, "Trip ID", id);
        AdminUi.addFormRow(form, 1, "Route ID", routes);
        AdminUi.addFormRow(form, 2, "Bus Type ID", busTypes);
        AdminUi.addFormRow(form, 3, "วันที่ (YYYY-MM-DD)", date);
        AdminUi.addFormRow(form, 4, "เวลา (HH:mm)", time);
        AdminUi.addFormRow(form, 5, "ราคา", price);
        AdminUi.addFormRow(form, 6, "ที่นั่งว่าง (อัตโนมัติ)", seats);

        JButton add = AdminUi.button("เพิ่มเที่ยวรถ", AdminUi.GREEN);
        JButton edit = AdminUi.button("แก้ไข", AdminUi.BLUE);
        JButton delete = AdminUi.button("ลบ", AdminUi.RED);
        JButton back = AdminUi.button("กลับหน้าหลัก", AdminUi.GRAY);

        JPanel left = AdminUi.box();
        left.add(AdminUi.heading("ข้อมูลเที่ยวรถ"), BorderLayout.NORTH);
        left.add(form, BorderLayout.CENTER);
        left.add(AdminUi.buttonColumn(add, edit, delete, back), BorderLayout.SOUTH);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JPanel right = AdminUi.box();
        right.add(AdminUi.heading("รายการเที่ยวรถ"), BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        center.setResizeWeight(0.35);
        center.setDividerSize(8);
        center.setBorder(null);
        center.setDividerLocation(440);
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

    private String value(int r, int c) { return model.getValueAt(r, c).toString(); }
    private void warning(String message) { AdminUi.warn(this, message); }

    /** รายการเส้นทางและประเภทรถสำหรับเลือกในฟอร์ม (อ่านผ่าน TripRepository) */
    private void loadReferences() {
        routes.removeAllItems();
        busTypes.removeAllItems();
        try {
            TripRepository repo = new TripRepository();
            for (String routeId : repo.loadRoutes().keySet()) routes.addItem(routeId);
            busTypeById = repo.loadBusTypes();
            for (String busTypeId : busTypeById.keySet()) busTypes.addItem(busTypeId);
        } catch (DataException ex) {
            warning(ex.getMessage());
        }
        if (routes.getItemCount() == 0) warning("ยังไม่มีเส้นทาง กรุณาเพิ่มเส้นทางก่อน");
        if (busTypes.getItemCount() == 0) warning("ยังไม่มีประเภทรถ กรุณาเพิ่มประเภทรถก่อน");
    }

    /** คำนวณคอลัมน์ที่นั่งว่างในไฟล์ใหม่ (การจองที่หมดเวลาจะคืนที่นั่ง) ก่อนแสดงตาราง */
    private void refreshAvailableSeats() {
        try {
            service.refreshAvailableSeats();
        } catch (DataException ex) {
            System.out.println("อัปเดตที่นั่งว่างไม่สำเร็จ: " + ex.getMessage());
        }
    }

    private void loadTrips() {
        model.setRowCount(0);
        dataValid = true;
        if (!Files.exists(schedulePath)) {
            return;   // ยังไม่มีไฟล์ = ยังไม่มีเที่ยวรถ เพิ่มใหม่แล้วจะสร้างไฟล์ให้
        }
        try (BufferedReader reader = Files.newBufferedReader(schedulePath, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header == null || !DataFiles.stripBom(header).trim().equals(DataFiles.TRIP_SCHEDULES_HEADER)) {
                throw new IOException("หัวคอลัมน์ไม่ตรงกับรูปแบบที่กำหนด");
            }
            String line;
            int lineNumber = 1;
            Set<String> seen = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != DataFiles.TRIP_COLUMNS) throw new IOException("คอลัมน์ไม่ครบที่บรรทัด " + lineNumber);
                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
                if (!seen.add(parts[0].toLowerCase(Locale.ROOT))) throw new IOException("Trip ID ซ้ำที่บรรทัด " + lineNumber);
                if (parts[0].isEmpty() || !comboContains(routes, parts[1]) || !busTypeById.containsKey(parts[2]))
                    throw new IOException("Trip/Route/Bus Type ID ไม่ถูกต้องที่บรรทัด " + lineNumber);
                LocalDate.parse(parts[3]);
                LocalTime.parse(parts[4]);
                if (Double.parseDouble(parts[5]) < 0 || Integer.parseInt(parts[6]) < 0)
                    throw new IOException("ราคา/ที่นั่งติดลบที่บรรทัด " + lineNumber);
                model.addRow(parts);
            }
        } catch (IOException | DateTimeParseException | NumberFormatException ex) {
            model.setRowCount(0);
            dataValid = false;
            AdminUi.error(this, "อ่าน trip_schedules.csv ไม่สำเร็จ: " + ex.getMessage()
                    + "\nกรุณาแก้ไฟล์แล้วเปิดหน้านี้ใหม่ (ระหว่างนี้จะแก้ไขข้อมูลไม่ได้)");
        }
    }

    private boolean comboContains(JComboBox<String> combo, String id) {
        for (int i = 0; i < combo.getItemCount(); i++) if (combo.getItemAt(i).equals(id)) return true;
        return false;
    }

    /** เลือกประเภทรถใหม่ในฟอร์ม -> แสดงจำนวนที่นั่งของรถคันนั้นไว้ก่อน (ค่าจริงคำนวณตอนบันทึก) */
    private void previewSeats() {
        BusType bt = busTypeById.get((String) busTypes.getSelectedItem());
        if (bt != null && table.getSelectedRow() < 0) seats.setText(String.valueOf(bt.getTotalSeats()));
    }

    private String[] formData() {
        String tripId = id.getText().trim();
        String routeId = (String) routes.getSelectedItem();
        String busTypeId = (String) busTypes.getSelectedItem();
        String d = date.getText().trim(), t = time.getText().trim();
        String p = price.getText().trim();
        if (tripId.isEmpty() || routeId == null || busTypeId == null || d.isEmpty() || t.isEmpty() || p.isEmpty()) {
            warning("กรุณากรอกข้อมูลให้ครบ และตรวจสอบ Route/Bus Type"); return null;
        }
        if (tripId.contains(",") || tripId.contains("\n") || tripId.contains("\r")) {
            warning("Trip ID ห้ามมีจุลภาคหรือขึ้นบรรทัดใหม่"); return null;
        }
        try {
            LocalDate travelDate = LocalDate.parse(d);
            LocalTime departTime = LocalTime.parse(t);
            double priceValue = Double.parseDouble(p);
            if (!Double.isFinite(priceValue) || priceValue < 0) throw new NumberFormatException();
            // ที่นั่งว่าง = ที่นั่งทั้งหมดของรถ - ที่นั่งที่ถูกจองในเส้นทาง/วัน/เวลานี้
            int available = Math.max(0, busTypeById.get(busTypeId).getTotalSeats()
                    - service.countBookedSeats(routeId, travelDate, departTime));
            return new String[]{tripId, routeId, busTypeId, travelDate.toString(), departTime.toString(),
                    Double.toString(priceValue), Integer.toString(available)};
        } catch (DateTimeParseException | NumberFormatException ex) {
            warning("วันที่ใช้ YYYY-MM-DD, เวลา HH:mm และราคาต้องเป็นตัวเลขไม่ติดลบ");
            return null;
        }
    }

    private boolean duplicate(String id, int except) {
        for (int i = 0; i < model.getRowCount(); i++)
            if (i != except && value(i, 0).equalsIgnoreCase(id)) return true;
        return false;
    }

    // การจองผูกกับ เส้นทาง + วันที่ + เวลา (ไม่ได้เก็บ trip_id) ถ้ามี 2 เที่ยวซ้ำกันใน 3 ค่านี้
    // ทั้งสองเที่ยวจะใช้ผังที่นั่งร่วมกัน ที่นั่งที่จองในเที่ยวหนึ่งจะขึ้นว่าไม่ว่างในอีกเที่ยว
    private String sameSlotTrip(String[] data, int except) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != except && value(i, 1).equals(data[1]) && value(i, 3).equals(data[3])
                    && value(i, 4).equals(data[4])) {
                return value(i, 0);
            }
        }
        return null;
    }

    private boolean checkSlotFree(String[] data, int except) {
        String other = sameSlotTrip(data, except);
        if (other != null) {
            warning("มีเที่ยวรถ " + other + " ในเส้นทาง วันที่ และเวลาเดียวกันอยู่แล้ว");
            return false;
        }
        return true;
    }

    private boolean saveTrips() {
        if (!dataValid) { warning("ข้อมูล CSV เดิมไม่ถูกต้อง กรุณาแก้ไฟล์แล้วเปิดหน้าต่างใหม่"); return false; }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            StringJoiner joiner = new StringJoiner(",");
            for (int j = 0; j < DataFiles.TRIP_COLUMNS; j++) joiner.add(value(i, j));
            lines.add(joiner.toString());
        }
        try {
            DataFiles.writeCsv(schedulePath, DataFiles.TRIP_SCHEDULES_HEADER, lines);
            // ตารางในหน้านี้อาจเป็นตัวเลขเก่า (มีคนจอง/ยกเลิกระหว่างเปิดหน้านี้ค้างไว้) คำนวณที่นั่งว่างใหม่ทุกครั้งหลังบันทึก
            refreshAvailableSeats();
            loadTrips();
            return true;
        } catch (IOException | DataException ex) {
            AdminUi.error(this, "บันทึก CSV ไม่สำเร็จ: " + ex.getMessage());
            return false;
        }
    }

    private void addTrip() {
        if (!dataValid) { warning("แก้ไข CSV เดิมก่อน"); return; }
        String[] data = formData();
        if (data == null) return;
        if (duplicate(data[0], -1)) { warning("Trip ID ซ้ำ"); return; }
        if (!checkSlotFree(data, -1)) return;
        model.addRow(data);
        if (saveTrips()) { AdminUi.success(this, "เพิ่มเที่ยวรถแล้ว"); clear(); }
        else loadTrips();
    }

    // bookings.csv ไม่ได้เก็บ trip_id แต่เก็บ เส้นทาง + วันที่ + เวลา
    // จึงถาม BookingService ด้วยข้อมูลของแถวนั้น (นับเฉพาะที่นั่งที่ยังไม่ยกเลิก/ไม่หมดเวลา)
    private boolean hasActiveBookings(int row) {
        return service.countBookedSeats(
                value(row, 1), LocalDate.parse(value(row, 3)), LocalTime.parse(value(row, 4))) > 0;
    }

    private void editTrip() {
        if (!dataValid) { warning("แก้ไข CSV เดิมก่อน"); return; }
        int view = table.getSelectedRow();
        if (view < 0) { warning("กรุณาเลือกเที่ยวรถ"); return; }
        int row = table.convertRowIndexToModel(view);
        String[] data = formData();
        if (data == null) return;
        if (duplicate(data[0], row)) { warning("Trip ID ซ้ำ"); return; }
        if (!checkSlotFree(data, row)) return;
        // ถ้ามีคนจองแล้ว ห้ามเปลี่ยนเส้นทาง/วัน/เวลา เพราะการจองผูกกับ 3 ค่านี้
        // และห้ามเปลี่ยนประเภทรถ เพราะรถคันใหม่อาจไม่มีเลขที่นั่งที่ถูกจองไว้แล้ว
        boolean tripKeyChanged = !value(row, 1).equals(data[1]) || !value(row, 2).equals(data[2])
                || !value(row, 3).equals(data[3]) || !value(row, 4).equals(data[4]);
        if (tripKeyChanged && hasActiveBookings(row)) {
            warning("ไม่สามารถเปลี่ยนเส้นทาง ประเภทรถ วันที่ หรือเวลาได้ เพราะมีรายการจองของเที่ยวรถนี้อยู่");
            return;
        }
        for (int i = 0; i < DataFiles.TRIP_COLUMNS; i++) model.setValueAt(data[i], row, i);
        if (saveTrips()) { AdminUi.success(this, "แก้ไขเที่ยวรถแล้ว"); clear(); }
        else loadTrips();
    }

    private void deleteTrip() {
        if (!dataValid) { warning("แก้ไข CSV เดิมก่อน"); return; }
        int view = table.getSelectedRow();
        if (view < 0) { warning("กรุณาเลือกเที่ยวรถ"); return; }
        int row = table.convertRowIndexToModel(view);
        if (hasActiveBookings(row)) {
            warning("ไม่สามารถลบเที่ยวรถได้ เพราะมีรายการจองอยู่");
            return;
        }
        if (!AdminUi.confirm(this, "ลบเที่ยวรถ " + value(row, 0) + " ?")) return;
        model.removeRow(row);
        if (saveTrips()) { AdminUi.success(this, "ลบเที่ยวรถแล้ว"); clear(); }
        else loadTrips();
    }

    private void clear() {
        table.clearSelection();
        id.setText(""); date.setText(""); time.setText(""); price.setText(""); seats.setText("");
        previewSeats();
        id.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageTripFrame::new);
    }
}
