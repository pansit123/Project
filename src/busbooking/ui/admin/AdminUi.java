package busbooking.ui.admin;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * ฟอนต์ / สี / ปุ่ม / แถวฟอร์ม / ตาราง / กล่องข้อความ ที่หน้า Admin ทุกหน้าใช้ร่วมกัน
 * (เดิมแต่ละหน้ามีเมธอด createButton, addFormRow ฯลฯ ของตัวเองที่เกือบเหมือนกัน)
 */
final class AdminUi {

    private AdminUi() { }

    static final Font FONT = new Font("Tahoma", Font.PLAIN, 15);
    static final Font FONT_LABEL = new Font("Tahoma", Font.PLAIN, 16);
    static final Font FONT_BOLD = new Font("Tahoma", Font.BOLD, 15);
    static final Font FONT_HEADING = new Font("Tahoma", Font.BOLD, 16);
    static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 28);

    static final Color TITLE_COLOR = new Color(0, 90, 160);
    static final Color BORDER = new Color(180, 180, 180);
    static final Color GREEN = new Color(40, 180, 70);
    static final Color BLUE = new Color(30, 120, 200);
    static final Color RED = new Color(220, 50, 50);
    static final Color GRAY = new Color(100, 100, 100);

    /** หัวข้อใหญ่กลางหน้า */
    static JLabel title(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(FONT_TITLE);
        l.setForeground(TITLE_COLOR);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return l;
    }

    /** หัวข้อของกล่อง เช่น "ข้อมูลเส้นทาง" */
    static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING);
        l.setForeground(new Color(50, 50, 50));
        l.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        return l;
    }

    /** กล่องพื้นขาวมีเส้นขอบ (ใช้ครอบฟอร์ม/ตาราง) */
    static JPanel box() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER), BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        return p;
    }

    /** ปุ่มสีพื้น ตัวอักษรขาว */
    static JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(250, 42));
        return b;
    }

    /** ปุ่มเรียงลงมาแนวตั้ง */
    static JPanel buttonColumn(JButton... buttons) {
        JPanel p = new JPanel(new GridLayout(buttons.length, 1, 0, 8));
        p.setBackground(Color.WHITE);
        for (JButton b : buttons) {
            p.add(b);
        }
        return p;
    }

    /** แถวฟอร์ม 1 แถว: ป้ายชื่อทางซ้าย ช่องกรอกทางขวา (panel ต้องใช้ GridBagLayout) */
    static void addFormRow(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(7, 0, 7, 10);
        JLabel l = new JLabel(label);
        l.setFont(FONT_LABEL);
        panel.add(l, c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(7, 0, 7, 0);
        field.setFont(FONT);
        field.setPreferredSize(new Dimension(220, 38));
        panel.add(field, c);
    }

    /** ตารางแบบอ่านอย่างเดียว เลือกได้ทีละแถว เรียงตามคอลัมน์ได้ */
    static JTable table(javax.swing.table.TableModel model, int... widths) {
        JTable t = new JTable(model);
        t.setFont(FONT);
        t.setRowHeight(34);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setAutoCreateRowSorter(true);
        t.getTableHeader().setFont(FONT_BOLD);
        t.getTableHeader().setPreferredSize(new Dimension(0, 35));
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        return t;
    }

    static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
    }

    static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
    }

    static void success(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
    }

    static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "ยืนยัน",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
