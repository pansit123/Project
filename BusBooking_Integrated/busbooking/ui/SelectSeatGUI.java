package busbooking.ui;
import busbooking.data.BookingService;
import java.awt.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
public class SelectSeatGUI extends JFrame {
    private final String userId;
    private final BookingService service = new BookingService();
    private final List<String[]> trips = new ArrayList<>();
    private final Map<String,String> routeNames = new HashMap<>();
    private final JComboBox<String> tripBox = new JComboBox<>();
    private final JPanel seatPanel = new JPanel(new GridLayout(3,8,6,6));
    private final JLabel summary = new JLabel("เลือกที่นั่ง",SwingConstants.CENTER);
    private final Set<String> selected = new LinkedHashSet<>();
    public SelectSeatGUI(String userId) {
        super("เลือกเที่ยวรถและที่นั่ง");this.userId=userId;
        setSize(880,520);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loadData();
        JPanel root = new JPanel(new BorderLayout(15,15));root.setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        JLabel title=new JLabel("เลือกเที่ยวรถและที่นั่ง (SelectSeat)",SwingConstants.CENTER);
        title.setFont(new Font("Tahoma",Font.BOLD,23));root.add(title,BorderLayout.NORTH);
        JPanel middle=new JPanel(new BorderLayout(10,12));
        middle.add(tripBox,BorderLayout.NORTH);middle.add(seatPanel,BorderLayout.CENTER);
        root.add(middle,BorderLayout.CENTER);
        JPanel bottom=new JPanel(new BorderLayout(10,10));
        JButton book=new JButton("สร้างการจอง");
        book.addActionListener(e -> book());
        bottom.add(summary,BorderLayout.NORTH);bottom.add(book,BorderLayout.SOUTH);
        root.add(bottom,BorderLayout.SOUTH);setContentPane(root);
        tripBox.addActionListener(e -> refreshSeats());refreshSeats();
    }
    private void loadData() {
        try {
            List<String> routes=Files.readAllLines(Paths.get("busbooking/data/routes.csv"),StandardCharsets.UTF_8);
            for(int i=1;i<routes.size();i++){String[] c=routes.get(i).split(",",-1);if(c.length>=3)routeNames.put(c[0],c[1]+" → "+c[2]);}
            List<String> lines=Files.readAllLines(Paths.get("busbooking/data/trip_schedules.csv"),StandardCharsets.UTF_8);
            for(int i=1;i<lines.size();i++){
                String[] c=lines.get(i).split(",",-1);if(c.length<7)continue;
                try {if(LocalDateTime.of(LocalDate.parse(c[3]),LocalTime.parse(c[4])).isBefore(LocalDateTime.now()))continue;}catch(Exception ex){continue;}
                trips.add(c);
                tripBox.addItem(c[0]+" | "+routeNames.getOrDefault(c[1],c[1])+" | "+c[3]+" "+c[4]+" | "+c[5]+" บาท");
            }
        }catch(Exception ex){JOptionPane.showMessageDialog(this,"อ่านข้อมูลเที่ยวรถไม่ได้: "+ex.getMessage());}
    }
    private void refreshSeats() {
        selected.clear();seatPanel.removeAll();
        int idx=tripBox.getSelectedIndex();
        if(idx<0){summary.setText("ไม่มีเที่ยวรถในอนาคต กรุณาให้ Admin เพิ่มเที่ยวรถ");seatPanel.revalidate();seatPanel.repaint();return;}
        String[] t=trips.get(idx);
        Set<String> booked=service.getBookedSeats(t[1],LocalDate.parse(t[3]),LocalTime.parse(t[4]));
        int seats=24;
        try{List<String> rows=Files.readAllLines(Paths.get("busbooking/data/bustypes.csv"),StandardCharsets.UTF_8);for(int i=1;i<rows.size();i++){String[] b=rows.get(i).split(",");if(b.length>=3&&b[0].equals(t[2])){seats=Math.min(24,Integer.parseInt(b[2]));break;}}}catch(Exception ignored){}
        for(int i=0;i<24;i++){
            String seat=""+(char)('A'+i/8)+(i%8+1);
            JToggleButton btn=new JToggleButton(seat);
            boolean blocked=i>=seats||booked.contains(seat);
            btn.setEnabled(!blocked);
            btn.setBackground(blocked?Color.LIGHT_GRAY:new Color(102,190,102));
            btn.addActionListener(e->{if(btn.isSelected())selected.add(seat);else selected.remove(seat);updateSummary(t);});
            seatPanel.add(btn);
        }
        updateSummary(t);seatPanel.revalidate();seatPanel.repaint();
    }
    private void updateSummary(String[] t){summary.setText("เลือก "+selected.size()+" ที่นั่ง | รวม "+String.format("%.2f",selected.size()*Double.parseDouble(t[5]))+" บาท | สีเทา = ไม่ว่าง");}
    private void book(){
        int idx=tripBox.getSelectedIndex();if(idx<0)return;
        if(selected.isEmpty()){JOptionPane.showMessageDialog(this,"กรุณาเลือกที่นั่งก่อน");return;}
        String[] t=trips.get(idx);
        new CreateBookingGUI(userId,t[1],routeNames.getOrDefault(t[1],t[1]),LocalDate.parse(t[3]),LocalTime.parse(t[4]),new ArrayList<>(selected),Double.parseDouble(t[5]),service,()->refreshSeats()).setVisible(true);
    }
}
