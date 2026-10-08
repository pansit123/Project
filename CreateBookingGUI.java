import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

public class CreateBookingGUI extends JFrame {

    public CreateBookingGUI(String tripID, String origin, String destination,ArrayList<String> selectedSeats, int selectedCount, int totalPrice) {
        Container cp = this.getContentPane();
        cp.setLayout(null);
        cp.setBackground(new Color(230, 242, 255));

        JLabel lblTrip = new JLabel("เที่ยวรถ: " + tripID);
        lblTrip.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTrip.setBounds(30, 10, 400, 30);
        cp.add(lblTrip);

        JLabel lblCount = new JLabel("จำนวนที่นั่ง: " + selectedCount + " ที่นั่ง");
        lblCount.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblCount.setBounds(30, 50, 300, 30);
        cp.add(lblCount);

        JLabel lblOrigin = new JLabel("ต้นทาง: " + origin);
        lblOrigin.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblOrigin.setBounds(30, 70, 400, 30);
        cp.add(lblOrigin);

        JLabel lblSeats = new JLabel("ที่นั่ง: " + String.join(", ", selectedSeats));
        lblSeats.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblSeats.setBounds(30, 100, 400, 30);
        cp.add(lblSeats);

        JLabel lblDestination = new JLabel("ปลายทาง: " + destination);
        lblDestination.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblDestination.setBounds(30, 140, 400, 30);
        cp.add(lblDestination);

        JLabel lblPrice = new JLabel("ราคารวม: " + totalPrice + " บาท");
        lblPrice.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblPrice.setBounds(30, 170, 300, 30);
        cp.add(lblPrice);

        this.setTitle("สร้างการจอง");
        this.setSize(500, 300);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}