package busbooking.ui;

import busbooking.data.BookingException;
import busbooking.data.BookingService;
import busbooking.model.Booking;
import busbooking.model.Ticket;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * หน้าแสดงตั๋วโดยสาร (1 ที่นั่ง = 1 ใบ) พร้อมปุ่ม "พิมพ์ตั๋ว"
 * เปิดหลังชำระเงินสำเร็จ หรือจากปุ่ม "ดูตั๋ว" ในหน้าประวัติ
 */
public class TicketFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final BookingService service;
    private final List<Ticket> tickets;

    public TicketFrame(BookingService service, List<Ticket> tickets) {
        super("ตั๋วโดยสาร");
        this.service = service;
        this.tickets = tickets;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UiTheme.BG);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("ตั๋วของคุณ", JLabel.CENTER);
        title.setFont(UiTheme.FONT_TITLE);
        title.setForeground(UiTheme.NAVY);
        title.setBorder(BorderFactory.createEmptyBorder(12, 0, 8, 0));
        add(title, BorderLayout.NORTH);

        // รายการตั๋วทั้งหมด เรียงลงมาแนวตั้ง
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(UiTheme.BG);
        list.setBorder(BorderFactory.createEmptyBorder(4, 16, 4, 16));
        for (Ticket t : tickets) {
            list.add(buildCard(t));
            list.add(javax.swing.Box.createVerticalStrut(10));
        }
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.getViewport().setBackground(UiTheme.BG);
        add(sp, BorderLayout.CENTER);

        // ปุ่มด้านล่าง
        JButton btnPrint = UiTheme.button("พิมพ์ตั๋ว", UiTheme.AMBER);
        JButton btnClose = UiTheme.button("ปิด", UiTheme.NAVY);
        btnPrint.addActionListener(e -> printAll());
        btnClose.addActionListener(e -> dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        bottom.setBackground(UiTheme.BG);
        bottom.add(btnPrint);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        setSize(480, 520);
        setLocationRelativeTo(null);
    }

    /** การ์ดตั๋ว 1 ใบ */
    private JPanel buildCard(Ticket t) {
        Booking b = service.findBooking(t.getBookingId());
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UiTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.AMBER, 2),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        card.add(line("เลขที่ตั๋ว: " + t.getTicketNumber(), true));
        if (b != null) {
            card.add(line("เส้นทาง: " + service.getRouteName(b.getRouteId()), false));
            card.add(line("วันเดินทาง: " + b.getTravelDate().format(DATE_FMT)
                    + "   เวลาออก: " + b.getDepartTime(), false));
            card.add(line("ที่นั่ง: " + b.getSeatNo() + "   ราคา: " + UiTheme.money(b.getPrice()), false));
        }
        return card;
    }

    private JLabel line(String text, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(bold ? UiTheme.FONT_BOLD : UiTheme.FONT);
        l.setForeground(bold ? UiTheme.NAVY : java.awt.Color.DARK_GRAY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    /** พิมพ์ (จำลอง) = เขียนไฟล์ .txt ของทุกใบลงโฟลเดอร์ tickets/ */
    private void printAll() {
        try {
            StringBuilder sb = new StringBuilder("บันทึกตั๋วแล้ว:\n");
            for (Ticket t : tickets) {
                File f = service.printTicket(t);
                sb.append(f.getPath()).append('\n');
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "พิมพ์ตั๋ว",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (BookingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "พิมพ์ไม่สำเร็จ",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
