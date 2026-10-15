package busbooking;

import busbooking.data.DataException;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        installErrorHandler();
        busbooking.ui.UiTheme.installDefaults();   // ฟอนต์กลางของทั้งโปรแกรม ตั้งที่นี่ที่เดียว
        SwingUtilities.invokeLater(Main::showLauncher);
    }

    /** หน้าแรก: เลือกว่าจะเข้าแบบผู้โดยสาร หรือ Admin (หน้า Admin กด "กลับ" แล้วกลับมาที่นี่) */
    public static void showLauncher() {
        JFrame f = new JFrame("Bus Booking - เลือกประเภทผู้ใช้");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(440, 280);
        f.setLocationRelativeTo(null);
        JPanel p = new JPanel(new GridLayout(3, 1, 12, 12));
        p.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        JLabel title = new JLabel("ระบบจองตั๋วรถบัส", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        JButton user = new JButton("เข้าสู่ระบบผู้โดยสาร / สมัครสมาชิก");
        JButton admin = new JButton("เข้าสู่ระบบผู้ดูแล (Admin)");
        user.setFont(new Font("Tahoma", Font.PLAIN, 16));
        admin.setFont(new Font("Tahoma", Font.PLAIN, 16));
        user.addActionListener(e -> {
            new busbooking.ui.member.LoginUI().setVisible(true);
            f.dispose();
        });
        admin.addActionListener(e -> {
            new busbooking.ui.admin.AdminLoginFrame();
            f.dispose();
        });
        p.add(title);
        p.add(user);
        p.add(admin);
        f.setContentPane(p);
        f.setVisible(true);
    }

    /** ปิดทุกหน้าต่างที่เปิดอยู่ (ใช้ตอนออกจากระบบ กันไม่ให้หน้าของผู้ใช้คนเดิมค้างอยู่) */
    public static void closeAllWindows() {
        for (Window w : Window.getWindows()) {
            w.dispose();
        }
    }

    /**
     * ตัวจัดการ error กลาง: exception ใดที่ไม่มีใครจับ (ทั้งในปุ่ม/หน้าจอ และ thread อื่น)
     * จะขึ้นกล่องข้อความแจ้งผู้ใช้แทนการเงียบหาย/ค้าง และพิมพ์รายละเอียดลง console ไว้ดีบัก
     */
    private static void installErrorHandler() {
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            ex.printStackTrace();
            DataException data = findCause(ex, DataException.class);
            String message;
            String title;
            if (data != null) {
                title = "อ่าน/บันทึกข้อมูลไม่สำเร็จ";
                message = data.getMessage()
                        + "\n\nกรุณาปิดไฟล์ CSV ที่เปิดค้างไว้ (เช่นใน Excel) แล้วลองใหม่";
            } else if (findCause(ex, OutOfMemoryError.class) != null) {
                title = "หน่วยความจำไม่พอ";
                message = "หน่วยความจำไม่พอ กรุณาปิดโปรแกรมแล้วเปิดใหม่";
            } else {
                title = "เกิดข้อผิดพลาด";
                message = "เกิดข้อผิดพลาดที่ไม่คาดคิด:\n" + ex.getClass().getSimpleName()
                        + (ex.getMessage() == null ? "" : ": " + ex.getMessage());
            }
            Runnable show = () -> JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            if (SwingUtilities.isEventDispatchThread()) {
                show.run();
            } else {
                SwingUtilities.invokeLater(show);
            }
        });
    }

    private static <T extends Throwable> T findCause(Throwable ex, Class<T> type) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (type.isInstance(t)) {
                return type.cast(t);
            }
        }
        return null;
    }
}
