package busbooking.ui.booking;

import busbooking.data.BookingException;
import busbooking.data.BookingService;
import busbooking.model.Booking;
import busbooking.model.BookingStatus;
import busbooking.model.Ticket;
import busbooking.model.User;
import busbooking.ui.UiTheme;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * หน้าชำระเงิน (งานคนที่ 4: ชำระเงิน -> ออกตั๋ว)
 *
 * รับรหัสการจองที่คนที่ 3 สร้างไว้แล้ว (สถานะ "รอชำระเงิน") มาจ่าย:
 *   new PaymentFrame(userId, bookingIds).setVisible(true);
 *
 * จ่ายสำเร็จ -> ออกตั๋ว -> เปิด TicketFrame
 * กด "ชำระภายหลัง" -> ปิดหน้านี้ การจองยังค้างรอชำระ (จ่ายได้จากหน้าประวัติการจอง ภายในเวลาที่กำหนด)
 */
public class PaymentFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final transient BookingService service;
    private final List<String> bookingIds;
    private final PaymentPanel paymentPanel = new PaymentPanel();
    private final JButton btnPay = UiTheme.button("ชำระเงิน", UiTheme.GREEN);
    private final JButton btnLater = UiTheme.button("ชำระภายหลัง", UiTheme.GRAY);

    public PaymentFrame(String userId, List<String> bookingIds) {
        this(new BookingService(), userId, bookingIds);
    }

    public PaymentFrame(BookingService service, String userId, List<String> bookingIds) {
        super("ชำระเงิน");
        this.service = service;
        this.bookingIds = new ArrayList<>(bookingIds);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UiTheme.BG);
        setLayout(new BorderLayout(0, 10));

        // โหลดรายการจองมาแสดง และตรวจว่าจ่ายได้จริง
        List<Booking> list = new ArrayList<>();
        for (String id : this.bookingIds) {
            Booking b = service.findBooking(id);
            if (b != null && b.getUserId().equals(userId)) {
                list.add(b);
            }
        }

        JLabel title = new JLabel("ชำระเงิน", JLabel.CENTER);
        title.setFont(UiTheme.FONT_TITLE);
        title.setForeground(UiTheme.NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        center.add(buildSummary(list), BorderLayout.NORTH);
        center.add(paymentPanel, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.setOpaque(false);
        bottom.add(btnPay);
        bottom.add(btnLater);
        add(bottom, BorderLayout.SOUTH);

        btnPay.addActionListener(e -> onPay());
        btnLater.addActionListener(e -> dispose());

        // ถ้าไม่มีรายการที่จ่ายได้ ปิดปุ่มจ่ายไว้
        boolean payable = !list.isEmpty() && list.size() == this.bookingIds.size();
        for (Booking b : list) {
            // รอชำระที่หมดเวลาแล้วก็จ่ายไม่ได้ (เดิมปุ่มยังกดได้ ผู้ใช้กรอกเลขบัตรเสร็จแล้วค่อยเจอ error)
            payable &= b.getStatus() == BookingStatus.PENDING && !BookingService.isExpired(b);
        }
        btnPay.setEnabled(payable);

        setSize(560, 560);
        setLocationRelativeTo(null);
    }

    /** กล่องสรุป: เส้นทาง วัน เวลา ที่นั่ง ยอดรวม และเวลาที่ต้องชำระภายใน */
    private JPanel buildSummary(List<Booking> list) {
        JPanel p = new JPanel(new GridLayout(0, 2, 8, 6));
        p.setBackground(UiTheme.CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("รายการที่ต้องชำระ"),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));
        if (list.isEmpty()) {
            addRow(p, "สถานะ", "ไม่พบรายการจอง");
            return p;
        }
        Booking first = list.get(0);
        List<String> seats = new ArrayList<>();
        double total = 0;
        for (Booking b : list) {
            seats.add(b.getSeatNo());
            total += b.getPrice();
        }
        addRow(p, "ผู้โดยสาร", User.nameOf(first.getUserId()));
        addRow(p, "เส้นทาง", service.getRouteName(first.getRouteId()));
        addRow(p, "วันเดินทาง", first.getTravelDate().format(DATE_FMT));
        addRow(p, "เวลาออก", first.getDepartTime().toString());
        // จองหลายที่นั่งจนยาวเกินช่อง: แสดงจำนวน + บางส่วน ชี้เมาส์ดูครบ
        String allSeats = String.join(", ", seats);
        JLabel seatLabel = addRow(p, "ที่นั่ง", allSeats.length() <= 22 ? allSeats
                : seats.size() + " ที่ (" + allSeats.substring(0, 14).replaceAll(",\\s*\\S*$", "") + " ...)");
        seatLabel.setToolTipText(allSeats);
        addRow(p, "ยอดชำระ", UiTheme.money(total));
        addRow(p, "ชำระภายใน", BookingService.PENDING_MINUTES + " นาทีหลังจอง");
        return p;
    }

    private JLabel addRow(JPanel p, String k, String v) {
        JLabel a = new JLabel(k);
        a.setFont(UiTheme.FONT);
        JLabel b = new JLabel(v);
        b.setFont(UiTheme.FONT_BOLD);
        p.add(a);
        p.add(b);
        return b;
    }

    /** กด "ชำระเงิน": ตรวจข้อมูลชำระ -> จ่ายทุกรายการ -> ออกตั๋ว -> เปิดตั๋ว */
    private void onPay() {
        String err = paymentPanel.validateInput();
        if (err != null) {
            JOptionPane.showMessageDialog(this, err, "ข้อมูลไม่ถูกต้อง", JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnPay.setEnabled(false);
        try {
            List<Ticket> tickets = service.payBookings(bookingIds, paymentPanel.getSelectedMethod());
            new TicketFrame(service, tickets).setVisible(true);
            dispose();
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "ชำระเงินไม่สำเร็จ",
                    JOptionPane.ERROR_MESSAGE);
            btnPay.setEnabled(true);
        }
    }
}
