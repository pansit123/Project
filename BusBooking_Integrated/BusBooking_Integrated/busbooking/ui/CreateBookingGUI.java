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
        super("ยืนยันการจอง");setSize(470,410);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setResizable(false);
        Container p=getContentPane();p.setLayout(null);p.setBackground(new Color(230,242,255));
        String[] details={"เที่ยวรถ: "+routeId,"จำนวนที่นั่ง: "+seats.size()+" ที่นั่ง","ต้นทาง - ปลายทาง: "+route,"ที่นั่ง: "+String.join(", ",seats),"วันที่: "+date+" เวลา: "+time,"ราคาสุทธิ: "+(seats.size()*price)+" บาท","สถานะ: PENDING (ชำระภายใน 15 นาที)"};
        int[] yy={10,50,85,120,155,195,230};
        for(int i=0;i<details.length;i++){JLabel label=new JLabel(details[i]);label.setFont(new Font("Tahoma",Font.BOLD,15));label.setBounds(30,yy[i],420,30);p.add(label);}
        JButton confirm=new JButton("ยืนยันและไปชำระเงิน");JButton cancel=new JButton("กลับไปเลือกที่นั่ง");
        confirm.setBounds(30,295,235,45);cancel.setBounds(280,295,155,45);
        confirm.setFont(new Font("Tahoma",Font.BOLD,14));cancel.setFont(new Font("Tahoma",Font.PLAIN,13));
        confirm.setBackground(new Color(0,51,153));confirm.setForeground(Color.WHITE);
        confirm.addActionListener(e->{
            try {
                List<Booking> bookings=service.createBooking(userId,routeId,date,time,seats,price);
                List<String> ids=new ArrayList<>();for(Booking b:bookings)ids.add(b.getBookingId());
                JOptionPane.showMessageDialog(this,"สร้างการจองสำเร็จ: "+String.join(", ",ids));
                dispose();refresh.run();new PaymentFrame(service,userId,ids).setVisible(true);
            }catch(BookingException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"จองไม่สำเร็จ",JOptionPane.ERROR_MESSAGE);refresh.run();}
        });
        cancel.addActionListener(e->dispose());p.add(confirm);p.add(cancel);
    }
}
