package busbooking.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * ค่าสี / ฟอนต์ / ปุ่ม กลางของหน้าจอฝั่งการจองและชำระเงิน
 * รวมไว้ที่เดียวเพื่อให้ทุกหน้าหน้าตาเหมือนกัน และแก้ธีมได้จุดเดียว
 */
public final class UiTheme {

    private UiTheme() { }

    public static final Color NAVY = new Color(0x1E, 0x3A, 0x3A);
    public static final Color AMBER = new Color(0xD9, 0x8A, 0x2B);
    public static final Color BG = new Color(0xF6, 0xF4, 0xEF);
    public static final Color CARD = Color.WHITE;
    public static final Color GREEN = new Color(0x2E, 0x7D, 0x4F);
    public static final Color RED = new Color(0xB3, 0x3A, 0x3A);
    public static final Color GRAY = new Color(0x8A, 0x8A, 0x8A);

    public static final Font FONT = new Font("Tahoma", Font.PLAIN, 16);
    public static final Font FONT_BOLD = new Font("Tahoma", Font.BOLD, 16);
    public static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 22);

    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");

    /** แสดงเงินเป็น "1,234.00 บาท" */
    public static String money(double v) {
        return MONEY.format(v) + " บาท";
    }

    /** ตั้งฟอนต์เริ่มต้นให้ dialog / ตาราง / radio (เรียกครั้งเดียวตอนเปิดโปรแกรม) */
    public static void installDefaults() {
        String[] keys = {"OptionPane.messageFont", "OptionPane.buttonFont", "RadioButton.font",
                "Label.font", "Button.font", "Table.font", "TableHeader.font",
                "TextField.font", "ComboBox.font"};
        for (String k : keys) {
            UIManager.put(k, new FontUIResource(FONT));
        }
    }

    /** ปุ่มแบบแบน มีสีพื้น ตัวอักษรขาว และเปลี่ยนเป็นสีเทาตอนกดไม่ได้ */
    public static JButton button(String text, final Color color) {
        final JButton b = new JButton(text) {
            private static final long serialVersionUID = 1L;
            @Override
            public void setEnabled(boolean e) {
                super.setEnabled(e);
                setBackground(e ? color : GRAY);
            }
        };
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setBackground(color);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            protected void paintText(java.awt.Graphics g, javax.swing.AbstractButton btn,
                                     java.awt.Rectangle r, String t) {
                btn.setForeground(Color.WHITE);   // ปุ่มที่กดไม่ได้ยังต้องอ่านออก
                super.paintText(g, btn, r, t);
            }
        });
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
