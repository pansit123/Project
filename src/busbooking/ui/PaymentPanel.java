package busbooking.ui;

import busbooking.data.BookingService;
import busbooking.model.PaymentMethod;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * ส่วนเลือกวิธีชำระเงิน (จำลอง) : พร้อมเพย์ (QR ปลอม) หรือบัตร (กรอกเลข 16 หลัก)
 * ใช้เป็น panel ย่อยใน BookingFrame — เลขบัตรอ่านจากช่องกรอกเท่านั้น ไม่ถูกบันทึกลงไฟล์
 */
public class PaymentPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String CARD_QR = "qr";
    private static final String CARD_CARD = "card";

    private final JRadioButton rdoPromptPay = new JRadioButton("พร้อมเพย์ (สแกน QR)", true);
    private final JRadioButton rdoCard = new JRadioButton("บัตรเครดิต/เดบิต");
    private final JTextField txtCard = new JTextField(18);
    private final CardLayout detailLayout = new CardLayout();
    private final JPanel detail = new JPanel(detailLayout);

    public PaymentPanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(UiTheme.CARD);
        setBorder(BorderFactory.createTitledBorder("เลือกวิธีชำระเงิน"));

        // แถวปุ่มเลือกวิธีจ่าย
        JPanel choices = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        choices.setOpaque(false);
        ButtonGroup g = new ButtonGroup();
        for (JRadioButton r : new JRadioButton[]{rdoPromptPay, rdoCard}) {
            r.setOpaque(false);
            r.setFont(UiTheme.FONT);
            g.add(r);
            choices.add(r);
        }
        add(choices, BorderLayout.NORTH);

        // ส่วนรายละเอียด: สลับระหว่าง QR กับช่องกรอกบัตร
        detail.setOpaque(false);
        detail.add(buildQrPanel(), CARD_QR);
        detail.add(buildCardPanel(), CARD_CARD);
        add(detail, BorderLayout.CENTER);

        rdoPromptPay.addActionListener(e -> detailLayout.show(detail, CARD_QR));
        rdoCard.addActionListener(e -> detailLayout.show(detail, CARD_CARD));
    }

    /** QR จำลอง: วาดลายสี่เหลี่ยมให้ดูเหมือน QR (สแกนไม่ได้จริง) */
    private JPanel buildQrPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        p.setOpaque(false);
        JPanel qr = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                int cell = 7;
                gr.setColor(Color.BLACK);
                for (int r = 0; r < 17; r++) {
                    for (int c = 0; c < 17; c++) {
                        boolean finder = (r < 5 && c < 5) || (r < 5 && c > 11) || (r > 11 && c < 5);
                        if (finder || ((r * 7 + c * 13 + r * c) % 3 == 0)) {
                            gr.fillRect(c * cell, r * cell, cell, cell);
                        }
                    }
                }
            }
        };
        qr.setBackground(Color.WHITE);
        qr.setPreferredSize(new java.awt.Dimension(17 * 7, 17 * 7));
        p.add(qr);
        JLabel hint = new JLabel("<html>QR จำลอง (ไม่ใช่การชำระจริง)<br>กดปุ่ม \"ชำระเงิน\" เพื่อยืนยัน</html>");
        hint.setFont(UiTheme.FONT);
        p.add(hint);
        return p;
    }

    private JPanel buildCardPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        p.setOpaque(false);
        JLabel l = new JLabel("เลขบัตร 16 หลัก:");
        l.setFont(UiTheme.FONT);
        txtCard.setFont(UiTheme.FONT);
        p.add(l);
        p.add(txtCard);
        return p;
    }

    /** วิธีชำระเงินที่ผู้ใช้เลือก */
    public PaymentMethod getSelectedMethod() {
        return rdoCard.isSelected() ? PaymentMethod.CARD : PaymentMethod.PROMPTPAY;
    }

    public String getCardNumber() {
        return txtCard.getText();
    }

    /** ตรวจข้อมูลที่กรอก คืนข้อความ error (ภาษาไทย) หรือ null ถ้าผ่าน */
    public String validateInput() {
        if (getSelectedMethod() == PaymentMethod.CARD
                && !BookingService.isValidCardNumber(txtCard.getText())) {
            return "เลขบัตรต้องเป็นตัวเลข 16 หลัก";
        }
        return null;
    }
}
