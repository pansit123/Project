package busbooking.ui.booking;

import busbooking.data.BookingException;
import busbooking.data.BookingService;
import busbooking.model.Booking;
import busbooking.model.BookingStatus;
import busbooking.model.Ticket;
import busbooking.model.User;
import busbooking.ui.UiTheme;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * หน้าประวัติการจองของผู้ใช้ 1 คน (ใหม่สุดอยู่บน)
 * ปุ่ม: ชำระเงิน (เฉพาะรอชำระ) / ดูตั๋ว (เฉพาะสำเร็จ) / ยกเลิกการจอง / รีเฟรช
 *
 * คนที่ 1 เรียกจากเมนูหลักหลังล็อกอิน:
 *   new BookingHistoryFrame(userId).setVisible(true);
 */
public class BookingHistoryFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLS = {"รหัสจอง", "เส้นทาง", "วันเดินทาง", "เวลา", "ที่นั่ง", "ราคา", "สถานะ"};
    private static final int COL_STATUS = 6;

    private final BookingService service;
    private final String userId;
    private final List<Booking> rows = new ArrayList<>();   // ข้อมูลแถวปัจจุบัน (ตรงกับตาราง)

    private final DefaultTableModel model = new DefaultTableModel(COLS, 0) {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JButton btnPay = UiTheme.button("ชำระเงิน", UiTheme.GREEN);
    private final JButton btnTicket = UiTheme.button("ดูตั๋ว", UiTheme.NAVY);
    private final JButton btnCancel = UiTheme.button("ยกเลิกการจอง", UiTheme.RED);
    private final JButton btnRefresh = UiTheme.button("รีเฟรช", UiTheme.GRAY);

    public BookingHistoryFrame(String userId) {
        this(new BookingService(), userId);
    }

    public BookingHistoryFrame(BookingService service, String userId) {
        super("ประวัติการจอง");
        this.service = service;
        this.userId = userId;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UiTheme.BG);
        setLayout(new BorderLayout(0, 8));

        JLabel title = new JLabel("ประวัติการจองของ คุณ" + User.nameOf(userId), JLabel.CENTER);
        title.setFont(UiTheme.FONT_TITLE);
        title.setForeground(UiTheme.NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        table.setFont(UiTheme.FONT);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UiTheme.FONT_BOLD);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(1).setPreferredWidth(200);   // คอลัมน์เส้นทางให้กว้างกว่าปกติ
        table.getSelectionModel().addListSelectionListener(e -> updateButtons());
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        sp.getViewport().setBackground(Color.WHITE);
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setOpaque(false);
        bottom.add(btnPay);
        bottom.add(btnTicket);
        bottom.add(btnCancel);
        bottom.add(btnRefresh);
        add(bottom, BorderLayout.SOUTH);

        btnPay.addActionListener(e -> onPay());
        btnTicket.addActionListener(e -> onViewTicket());
        btnCancel.addActionListener(e -> onCancel());
        btnRefresh.addActionListener(e -> reload());

        reload();
        setSize(820, 480);
        setLocationRelativeTo(null);
    }

    /** โหลดประวัติใหม่จากไฟล์แล้ววาดตารางใหม่ */
    public final void reload() {
        rows.clear();
        rows.addAll(service.viewBookingHistory(userId));
        model.setRowCount(0);
        for (Booking b : rows) {
            model.addRow(new Object[]{
                    b.getBookingId(), service.getRouteName(b.getRouteId()),
                    b.getTravelDate().format(DATE_FMT), b.getDepartTime().toString(),
                    b.getSeatNo(), UiTheme.money(b.getPrice()), statusLabel(b)});
        }
        updateButtons();
    }

    /** ข้อความสถานะ: รอชำระที่หมดเวลาแสดงเป็น "หมดเวลาชำระ" */
    private static String statusLabel(Booking b) {
        return BookingService.isExpired(b) ? "หมดเวลาชำระ" : b.getStatus().getLabel();
    }

    /** แถวที่เลือกอยู่ (null ถ้ายังไม่เลือก) */
    private Booking selected() {
        int r = table.getSelectedRow();
        return (r < 0 || r >= rows.size()) ? null : rows.get(r);
    }

    /** เปิด/ปิดปุ่มตามสถานะของแถวที่เลือก */
    private void updateButtons() {
        Booking b = selected();
        btnPay.setEnabled(b != null && b.getStatus() == BookingStatus.PENDING
                && !BookingService.isExpired(b));
        btnTicket.setEnabled(b != null && b.getStatus() == BookingStatus.PAID);
        btnCancel.setEnabled(b != null && b.getStatus() != BookingStatus.CANCELLED);
    }

    /** ปุ่มชำระเงิน: เปิด PaymentFrame หน้าเดียวกับตอนจอง (ปิดแล้วกดรีเฟรชเพื่อดูสถานะใหม่) */
    private void onPay() {
        Booking b = selected();
        if (b == null) {
            return;
        }
        PaymentFrame frame = new PaymentFrame(service, userId, Collections.singletonList(b.getBookingId()));
        reloadWhenClosed(frame);
        frame.setVisible(true);
    }

    /** ปิดหน้าชำระเงินแล้ว โหลดตารางใหม่ให้สถานะตรงกับไฟล์ (ไม่ต้องกดรีเฟรชเอง) */
    private void reloadWhenClosed(JFrame frame) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (isDisplayable()) {
                    reload();
                }
            }
        });
    }

    private void onViewTicket() {
        Booking b = selected();
        if (b == null) {
            return;
        }
        Ticket t = service.findTicketByBooking(b.getBookingId());
        if (t == null) {
            JOptionPane.showMessageDialog(this, "ยังไม่มีตั๋วของรายการนี้", "ไม่พบตั๋ว",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new TicketFrame(service, Collections.singletonList(t)).setVisible(true);
    }

    private void onCancel() {
        Booking b = selected();
        if (b == null) {
            return;
        }
        String extra = b.getStatus() == BookingStatus.PAID ? "\n(ระบบจำลองการคืนเงินให้)" : "";
        int ok = JOptionPane.showConfirmDialog(this,
                "ยืนยันยกเลิกการจอง " + b.getBookingId() + " ที่นั่ง " + b.getSeatNo() + " ?" + extra,
                "ยกเลิกการจอง", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            service.cancelBooking(b.getBookingId(), userId);
            reload();
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ยกเลิกไม่ได้",
                    JOptionPane.ERROR_MESSAGE);
            reload();
        }
    }

    /** ระบายสีคอลัมน์สถานะ: เขียว=สำเร็จ ส้ม=รอชำระ แดง=ยกเลิก */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean focus, int r, int c) {
            Component comp = super.getTableCellRendererComponent(t, v, sel, focus, r, c);
            String s = String.valueOf(v);
            if (!sel) {
                if (s.equals(BookingStatus.PAID.getLabel())) {
                    comp.setForeground(UiTheme.GREEN);
                } else if (s.equals(BookingStatus.PENDING.getLabel())) {
                    comp.setForeground(UiTheme.AMBER);
                } else {
                    comp.setForeground(UiTheme.RED);
                }
            }
            comp.setFont(UiTheme.FONT_BOLD);
            return comp;
        }
    }
}
