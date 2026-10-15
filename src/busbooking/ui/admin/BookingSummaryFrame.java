package busbooking.ui.admin;

import busbooking.data.BookingService;
import busbooking.model.Booking;
import busbooking.model.BookingStatus;
import busbooking.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

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
        lblBooked = new JLabel("ชำระเงินแล้ว: 0");
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

    private void loadBookings() {
        tableModel.setRowCount(0);
        int booked = 0, cancelled = 0;
        List<Booking> bookings = new BookingService().getAllBookings();
        Map<String, String> names = User.loadNames();   // แสดงชื่อผู้โดยสารแทนเบอร์
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getRouteId() + " / " + b.getTravelDate() + " " + b.getDepartTime(),
                    b.getSeatNo(),
                    User.displayName(b.getUserId(), names),
                    b.getStatus().getLabel()});
            if (b.getStatus() == BookingStatus.PAID) booked++;
            if (b.getStatus() == BookingStatus.CANCELLED) cancelled++;
        }
        lblTotal.setText("จำนวนการจองทั้งหมด: " + bookings.size());
        lblBooked.setText("ชำระเงินแล้ว: " + booked);
        lblCancelled.setText("ยกเลิกแล้ว: " + cancelled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookingSummaryFrame::new);
    }
}
