package busbooking.ui;
import busbooking.data.*;
import busbooking.model.Booking;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.*;
import java.util.List;
public class CreateBookingGUI extends JFrame {
    public CreateBookingGUI(String userId,String routeId,String route,LocalDate date,LocalTime time,List<String> seats,double price,BookingService service,Runnable refresh) {
        super("ยืนยันการจอง");setSize(480,350);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel p=new JPanel(new GridLayout(7,1,5,5));p.setBorder(BorderFactory.createEmptyBorder(15,25,15,25));
        p.add(new JLabel("เส้นทาง: "+route));p.add(new JLabel("วันที่: "+date+" เวลา: "+time));
        p.add(new JLabel("ที่นั่ง: "+String.join(", ",seats)));
        p.add(new JLabel("รวม "+(seats.size()*price)+" บาท"));
        p.add(new JLabel("สถานะเริ่มต้น: PENDING (ชำระภายใน 15 นาที)"));
        JButton confirm=new JButton("ยืนยันและไปชำระเงิน");JButton cancel=new JButton("กลับไปเลือกที่นั่ง");
        confirm.addActionListener(e->{
            try {
                List<Booking> bookings=service.createBooking(userId,routeId,date,time,seats,price);
                List<String> ids=new ArrayList<>();for(Booking b:bookings)ids.add(b.getBookingId());
                JOptionPane.showMessageDialog(this,"สร้างการจองสำเร็จ: "+String.join(", ",ids));
                dispose();refresh.run();new PaymentFrame(service,userId,ids).setVisible(true);
            }catch(BookingException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"จองไม่สำเร็จ",JOptionPane.ERROR_MESSAGE);refresh.run();}
        });
        cancel.addActionListener(e->dispose());p.add(confirm);p.add(cancel);setContentPane(p);
    }
}
