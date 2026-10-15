package login;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


public class SelectSeatGUI extends JFrame implements ActionListener {
    Container cp;
    JLabel lblTitle,lblTrip, lblBusType, lblSeatCount, lblTotalPrice;
    JLabel lblAvailable, lblSelected, lblBooked;
    JButton btnBooking;
    JButton boxAvailable, boxSelected, boxBooked;
    JComboBox<String> tripBox;

    int pricePerSeat = 990;
    int selectedCount = 0;

    ArrayList<String[]> trips = new ArrayList<>();
    ArrayList<String[]> bookings = new ArrayList<>();
    String selectedTripID = "";

    Color COLOR_AVAILABLE = new Color(34, 139, 34);
    Color COLOR_SELECTED = new Color(135, 206, 250);
    Color COLOR_BOOKED = new Color(128, 128, 128);
    Color COLOR_HEADER = new Color(0, 51, 153);

    public SelectSeatGUI() {
        loadTrips();
        loadBookings();
        Initial();
        setComponent();
        Finally();
    }

    public void Initial() {
        cp = this.getContentPane();
        cp.setLayout(null);
        cp.setBackground(new Color(230, 242, 255));
    }

    public void loadTrips() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("trips.csv"));
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                trips.add(line.split(","));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBookings() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("bookings.csv"));
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                bookings.add(line.split(","));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setComponent() {
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBackground(COLOR_HEADER);
        headerPanel.setBounds(0, 0, 850, 45);

        lblTitle = new JLabel("  เลือกรถและที่นั่ง (SelectSeat)");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(10, 8, 300, 30);

        headerPanel.add(lblTitle);
        cp.add(headerPanel);

        // เลือกเที่ยวรถ
        tripBox = new JComboBox<>();
        for (String[] trip : trips) {
            tripBox.addItem(trip[0] + " : " + trip[1] + " → " + trip[2]);
        }
        tripBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tripBox.setBounds(8, 55, 400, 30);
        cp.add(tripBox);
        if (trips.size() > 0) {
            selectedTripID = trips.get(0)[0];
            lblTrip = new JLabel(trips.get(0)[1] + " → " + trips.get(0)[2]);
        } else {
            lblTrip = new JLabel("รอ CSV");
        }
        lblTrip.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTrip.setBounds(8, 90, 400, 30);
        cp.add(lblTrip);
        tripBox.addActionListener(e -> {
            int index = tripBox.getSelectedIndex();
            if (index >= 0) {
                selectedTripID = trips.get(index)[0];
                lblTrip.setText(trips.get(index)[1] + " → " + trips.get(index)[2]);
                selectedCount = 0;
                lblSeatCount.setText("ที่นั่งที่เลือก: 0");
                lblTotalPrice.setText("ราคาสุทธิ: 0 บาท");
                updateSeats();
            }
        });

        // ข้อมูล
        lblBusType = new JLabel("กรุณาเลือกที่นั่ง", JLabel.CENTER);
        lblBusType.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblBusType.setBounds(115, 125, 200, 25);
        cp.add(lblBusType);

        // ที่นั่ง
        int startX = 33;
        int startY = 152;
        int btnWidth = 45;
        int btnHeight = 35;
        int dist = 6;

        String[] rowLetters = {"D", "B", "A"};
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 8; col++) {
                JToggleButton btn = new JToggleButton();
                btn.setFont(new Font("Tahoma", Font.BOLD, 11));
                int y = startY + row * (btnHeight + dist);
                if (row == 2) {
                    y += 30;
                }
                btn.setBounds(startX + col * (btnWidth + dist), y, btnWidth, btnHeight);
                String seatName = rowLetters[row] + (col + 1);
                btn.setText(seatName);
                btn.setHorizontalAlignment(SwingConstants.CENTER);
                btn.setVerticalAlignment(SwingConstants.CENTER);
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                btn.setFocusPainted(false);
                boolean booked = false;
                for (String[] booking : bookings) {
                    if (booking[1].equals(selectedTripID) && booking[2].equals(seatName) && booking[4].equals("BOOKED")) {
                        booked = true;
                        break;
                    }
                }
                if (booked) {
                    btn.setBackground(COLOR_BOOKED);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(false);
                } else {
                    btn.setBackground(COLOR_AVAILABLE);
                    btn.setForeground(Color.WHITE);
                    btn.addActionListener(this);
                }
                cp.add(btn);
            }
        }
        // สถานะที่นั่ง
        boxAvailable = new JButton();
        boxAvailable.setBackground(COLOR_AVAILABLE);
        boxAvailable.setBounds(50, 350, 20, 15);
        boxAvailable.setEnabled(false);
        boxAvailable.setOpaque(true);
        boxAvailable.setBorderPainted(false);

        lblAvailable = new JLabel("AVAILABLE");
        lblAvailable.setBounds(80, 347, 80, 20);

        boxSelected = new JButton();
        boxSelected.setBackground(COLOR_SELECTED);
        boxSelected.setBounds(170, 350, 20, 15);
        boxSelected.setEnabled(false);
        boxSelected.setOpaque(true);
        boxSelected.setBorderPainted(false);

        lblSelected = new JLabel("SELECTED");
        lblSelected.setBounds(200, 347, 80, 20);

        boxBooked = new JButton();
        boxBooked.setBackground(COLOR_BOOKED);
        boxBooked.setBounds(290, 350, 20, 15);
        boxBooked.setEnabled(false);
        boxBooked.setOpaque(true);
        boxBooked.setBorderPainted(false);

        lblBooked = new JLabel("BOOKED");
        lblBooked.setBounds(320, 347, 80, 20);

        cp.add(boxAvailable);
        cp.add(lblAvailable);
        cp.add(boxSelected);
        cp.add(lblSelected);
        cp.add(boxBooked);
        cp.add(lblBooked);

        // ข้อมูลการเลือก
        lblSeatCount = new JLabel("ที่นั่งที่เลือก: 0");
        lblSeatCount.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblSeatCount.setBounds(620, 65, 190, 25);

        lblTotalPrice = new JLabel("ราคาสุทธิ: 0 บาท");
        lblTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblTotalPrice.setBounds(620, 100, 190, 25);

        btnBooking = new JButton("สร้างการจอง (createBooking)");
        btnBooking.setBackground(COLOR_HEADER);
        btnBooking.setForeground(Color.WHITE);
        btnBooking.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnBooking.setBounds(608, 365, 220, 60);
        btnBooking.addActionListener(this);

        cp.add(lblSeatCount);
        cp.add(lblTotalPrice);
        cp.add(btnBooking);
    }

    public void updateSeats() {
        Component[]components = cp.getComponents();
        for (Component component : components) {
            if (component instanceof JToggleButton) {
                JToggleButton btn = (JToggleButton) component;
                String seatName = btn.getText();
                boolean booked = false;
                for (String[] booking : bookings) {
                    if (booking[1].equals(selectedTripID) && booking[2].equals(seatName) && booking[4].equals("BOOKED")) {
                        booked = true;
                        break;
                    }
                }
                if (booked) {
                    btn.setBackground(COLOR_BOOKED);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(false);
                    btn.setSelected(false);
                } else {
                    btn.setBackground(COLOR_AVAILABLE);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(true);
                    btn.setSelected(false);
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnBooking) {
        if (selectedCount == 0) {
            JOptionPane.showMessageDialog(
                this,
                "กรุณาเลือกที่นั่งอย่างน้อย 1 ที่นั่ง",
                "แจ้งเตือน",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            ArrayList<String> selectedSeats = new ArrayList<>();

            for (Component c : cp.getComponents()) {
                if (c instanceof JToggleButton) {
                    JToggleButton btn = (JToggleButton) c;

                    if (btn.isSelected()) {
                        selectedSeats.add(btn.getText());
                    }
                }
            }

            new CreateBookingGUI(
                selectedTripID,
                trips.get(tripBox.getSelectedIndex())[1],trips.get(tripBox.getSelectedIndex())[2],
                selectedSeats,
                selectedCount,
                selectedCount * pricePerSeat
            );

            this.dispose();
        }
    } else if (e.getSource() instanceof JToggleButton) {
        JToggleButton btn = (JToggleButton) e.getSource();

        if (btn.isSelected()) {
            btn.setBackground(COLOR_SELECTED);
            btn.setForeground(Color.BLACK);
            selectedCount++;
        } else {
            btn.setBackground(COLOR_AVAILABLE);
            btn.setForeground(Color.WHITE);
            selectedCount--;
        }

        lblSeatCount.setText("จำที่นั่งที่ท่านเลือก: " + selectedCount);
        lblTotalPrice.setText("ราคาสุทธิ: " + (selectedCount * pricePerSeat) + " บาท");
    }
}
    public void Finally() {
        this.setTitle("จองที่นั่งรถทัวร์");
        this.setSize(850, 480);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        new SelectSeatGUI();
    }
}