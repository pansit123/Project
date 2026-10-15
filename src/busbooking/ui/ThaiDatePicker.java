package busbooking.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * ช่องเลือกวันที่แบบปฏิทิน แสดงปีเป็น พ.ศ. เช่น "15/10/2569"
 * คลิกที่ช่องหรือปุ่มปฏิทิน -> มีปฏิทินเด้งขึ้นมาให้เลือกเดือน / ปี / วัน
 * (พิมพ์เองไม่ได้ เพื่อให้รูปแบบวันที่ถูกต้องเสมอ)
 */
public class ThaiDatePicker extends JPanel {

    private static final int BE_OFFSET = 543;   // ปี พ.ศ. = ปี ค.ศ. + 543
    private static final String[] MONTHS = {"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
            "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    private static final String[] WEEKDAYS = {"อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"};
    private static final Color ACCENT = new Color(41, 158, 215);
    private static final Color BORDER = new Color(200, 205, 210);
    private static final Font FONT = new Font("Tahoma", Font.PLAIN, 12);

    private final JTextField field = new JTextField();
    private final JPopupMenu popup = new JPopupMenu();
    private final JComboBox<String> monthBox = new JComboBox<>(MONTHS);
    private final JComboBox<Integer> yearBox = new JComboBox<>();
    private final JPanel dayGrid = new JPanel(new GridLayout(0, 7, 2, 2));

    private final LocalDate maxDate;
    private LocalDate selected;
    private boolean updating;   // กันไม่ให้ listener ของ combo ทำงานตอนโค้ดตั้งค่าเอง

    /**
     * @param placeholder ข้อความแนะนำเมื่อยังไม่ได้เลือก
     * @param maxDate     วันล่าสุดที่เลือกได้ (เช่น วันนี้ สำหรับวันเกิด)
     * @param yearsBack   ให้เลือกย้อนหลังได้กี่ปี
     */
    public ThaiDatePicker(String placeholder, LocalDate maxDate, int yearsBack) {
        super(new BorderLayout());
        this.maxDate = maxDate;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(BORDER, 1));

        field.setEditable(false);
        field.setBackground(Color.WHITE);
        field.setToolTipText(placeholder);
        field.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        field.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(field, BorderLayout.CENTER);

        JButton calendarButton = new JButton(new CalendarIcon());
        calendarButton.setToolTipText("เลือกจากปฏิทิน");
        calendarButton.setFocusPainted(false);
        calendarButton.setContentAreaFilled(false);
        calendarButton.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 8));
        calendarButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        calendarButton.addActionListener(e -> showPopup());
        add(calendarButton, BorderLayout.EAST);

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPopup();
            }
        });

        for (int y = maxDate.getYear(); y >= maxDate.getYear() - yearsBack; y--) {
            yearBox.addItem(y + BE_OFFSET);
        }
        buildPopup();
    }

    /** วันที่ที่เลือก (null ถ้ายังไม่ได้เลือก) */
    public LocalDate getDate() {
        return selected;
    }

    public void setDate(LocalDate date) {
        selected = date;
        field.setText(date == null ? "" : format(date));
    }

    /** ข้อความในช่อง เช่น "15/10/2569" (ว่างถ้ายังไม่ได้เลือก) */
    public String getText() {
        return field.getText();
    }

    public void clear() {
        setDate(null);
    }

    public static String format(LocalDate d) {
        return String.format("%02d/%02d/%d", d.getDayOfMonth(), d.getMonthValue(), d.getYear() + BE_OFFSET);
    }

    // ================= POPUP =================

    private void buildPopup() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton prev = navButton("<", -1);
        JButton next = navButton(">", 1);
        monthBox.setFont(FONT);
        yearBox.setFont(FONT);
        monthBox.addActionListener(e -> { if (!updating) refreshDays(); });
        yearBox.addActionListener(e -> { if (!updating) refreshDays(); });

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        header.setBackground(Color.WHITE);
        header.add(prev);
        header.add(monthBox);
        header.add(yearBox);
        header.add(next);
        panel.add(header, BorderLayout.NORTH);

        dayGrid.setBackground(Color.WHITE);
        panel.add(dayGrid, BorderLayout.CENTER);

        popup.setBorder(BorderFactory.createLineBorder(BORDER));
        popup.add(panel);
    }

    private JButton navButton(String text, int delta) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 12));
        b.setMargin(new Insets(2, 6, 2, 6));
        b.setFocusPainted(false);
        b.addActionListener(e -> showMonth(currentMonth().plusMonths(delta)));
        return b;
    }

    private void showPopup() {
        if (!isEnabled()) {
            return;
        }
        // ยังไม่เคยเลือก: เปิดที่เดือนปัจจุบันของ maxDate
        showMonth(YearMonth.from(selected != null ? selected : maxDate));
        popup.show(this, 0, getHeight());
    }

    private YearMonth currentMonth() {
        int beYear = (Integer) yearBox.getSelectedItem();
        return YearMonth.of(beYear - BE_OFFSET, monthBox.getSelectedIndex() + 1);
    }

    /** เลื่อนไปเดือนที่กำหนด (ไม่เกินช่วงปีที่มีให้เลือก) */
    private void showMonth(YearMonth ym) {
        int first = yearBox.getItemAt(yearBox.getItemCount() - 1) - BE_OFFSET;
        if (ym.isAfter(YearMonth.from(maxDate))) {
            ym = YearMonth.from(maxDate);
        } else if (ym.getYear() < first) {
            ym = YearMonth.of(first, 1);
        }
        updating = true;
        monthBox.setSelectedIndex(ym.getMonthValue() - 1);
        yearBox.setSelectedItem(ym.getYear() + BE_OFFSET);
        updating = false;
        refreshDays();
    }

    private void refreshDays() {
        dayGrid.removeAll();
        for (String w : WEEKDAYS) {
            JLabel l = new JLabel(w, SwingConstants.CENTER);
            l.setFont(new Font("Tahoma", Font.BOLD, 12));
            l.setForeground(w.equals("อา") ? new Color(200, 60, 60) : new Color(90, 90, 90));
            dayGrid.add(l);
        }

        YearMonth ym = currentMonth();
        int blanks = ym.atDay(1).getDayOfWeek().getValue() % 7;   // อาทิตย์ = ช่องแรก
        for (int i = 0; i < blanks; i++) {
            dayGrid.add(new JLabel());
        }
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            LocalDate date = ym.atDay(day);
            JButton b = new JButton(String.valueOf(day));
            b.setFont(FONT);
            b.setMargin(new Insets(2, 2, 2, 2));
            b.setPreferredSize(new Dimension(36, 28));
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(true);
            b.setBorder(BorderFactory.createLineBorder(BORDER));
            b.setEnabled(!date.isAfter(maxDate));
            boolean isSelected = date.equals(selected);
            b.setBackground(isSelected ? ACCENT : Color.WHITE);
            b.setForeground(isSelected ? Color.WHITE : new Color(60, 60, 60));
            if (b.isEnabled()) {
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            b.addActionListener(e -> {
                setDate(date);
                popup.setVisible(false);
            });
            dayGrid.add(b);
        }
        dayGrid.revalidate();
        dayGrid.repaint();
        popup.pack();
    }

    /** ไอคอนปฏิทินเล็กๆ วาดเอง (ไม่ต้องพึ่งไฟล์รูป) */
    private static class CalendarIcon implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ACCENT);
            g2.drawRoundRect(x, y + 2, 15, 13, 3, 3);
            g2.fillRect(x, y + 2, 16, 4);
            g2.fillRect(x + 3, y, 2, 4);
            g2.fillRect(x + 11, y, 2, 4);
            for (int r = 0; r < 2; r++) {
                for (int col = 0; col < 3; col++) {
                    g2.fillRect(x + 3 + col * 4, y + 8 + r * 3, 2, 2);
                }
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}
