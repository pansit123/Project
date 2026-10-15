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
    private final JPanel seatPanel = new JPanel(null);
    private final JLabel summary = new JLabel("เลือกที่นั่ง",SwingConstants.CENTER);
    private final Set<String> selected = new LinkedHashSet<>();
    public SelectSeatGUI(String userId) {
        super("เลือกเที่ยวรถและที่นั่ง");this.userId=userId;
        setSize(850,480);setResizable(false);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        loadData();
        Container cp=getContentPane();cp.setLayout(null);cp.setBackground(new Color(230,242,255));
        JPanel header=new JPanel(null);header.setBackground(new Color(0,51,153));header.setBounds(0,0,850,45);
        JLabel title=new JLabel("  เลือกรถและที่นั่ง (SelectSeat)");title.setFont(new Font("Tahoma",Font.BOLD,16));title.setForeground(Color.WHITE);title.setBounds(10,8,400,30);header.add(title);cp.add(header);
        tripBox.setFont(new Font("Tahoma",Font.PLAIN,14));tripBox.setBounds(8,55,510,30);cp.add(tripBox);
        JLabel routeLabel=new JLabel("กรุณาเลือกเที่ยวรถ");routeLabel.setFont(new Font("Tahoma",Font.BOLD,16));routeLabel.setBounds(8,90,570,30);cp.add(routeLabel);
        tripBox.addActionListener(e->{int i=tripBox.getSelectedIndex();routeLabel.setText(i>=0?routeNames.getOrDefault(trips.get(i)[1],trips.get(i)[1]):"ไม่มีเที่ยวรถ");});
        JLabel seatTitle=new JLabel("กรุณาเลือกที่นั่ง",SwingConstants.CENTER);seatTitle.setFont(new Font("Tahoma",Font.BOLD,16));seatTitle.setBounds(115,125,200,25);cp.add(seatTitle);
        seatPanel.setOpaque(false);seatPanel.setBounds(33,152,405,155);cp.add(seatPanel);
        Color green=new Color(34,139,34),blue=new Color(135,206,250),gray=new Color(128,128,128);
        legend(cp,50,350,green,"AVAILABLE");legend(cp,170,350,blue,"SELECTED");legend(cp,290,350,gray,"BOOKED");
        summary.setFont(new Font("Tahoma",Font.BOLD,15));summary.setHorizontalAlignment(SwingConstants.LEFT);summary.setBounds(610,65,235,100);cp.add(summary);
        JButton book=new JButton("สร้างการจอง (createBooking)");book.setBackground(new Color(0,51,153));book.setForeground(Color.WHITE);book.setFont(new Font("Tahoma",Font.BOLD,12));book.setBounds(608,365,220,60);book.addActionListener(e->book());cp.add(book);
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
        String[] rows={"D","B","A"};
        for(int i=0;i<24;i++){
            int row=i/8,col=i%8;
            String seat=rows[row]+(col+1);
            JToggleButton btn=new JToggleButton(seat);
            btn.setFont(new Font("Tahoma",Font.BOLD,11));btn.setMargin(new Insets(0,0,0,0));
            btn.setBounds(col*51,row*41+(row==2?30:0),45,35);
            boolean blocked=i>=seats||booked.contains(seat);
            btn.setEnabled(!blocked);btn.setOpaque(true);btn.setFocusPainted(false);
            btn.setBackground(blocked?new Color(128,128,128):new Color(34,139,34));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e->{if(btn.isSelected()){selected.add(seat);btn.setBackground(new Color(135,206,250));btn.setForeground(Color.BLACK);}else{selected.remove(seat);btn.setBackground(new Color(34,139,34));btn.setForeground(Color.WHITE);}updateSummary(t);});
            seatPanel.add(btn);
        }
        updateSummary(t);seatPanel.revalidate();seatPanel.repaint();
    }
    private void updateSummary(String[] t){summary.setText("<html>ที่นั่งที่เลือก: "+selected.size()+"<br><br>ราคาสุทธิ: "+String.format("%.2f",selected.size()*Double.parseDouble(t[5]))+" บาท</html>");}
    private void legend(Container cp,int x,int y,Color color,String name){
        JPanel box=new JPanel();box.setBackground(color);box.setBounds(x,y,20,15);cp.add(box);
        JLabel label=new JLabel(name);label.setFont(new Font("Tahoma",Font.PLAIN,12));label.setBounds(x+30,y-3,90,20);cp.add(label);
    }
    private void book(){
        int idx=tripBox.getSelectedIndex();if(idx<0)return;
        if(selected.isEmpty()){JOptionPane.showMessageDialog(this,"กรุณาเลือกที่นั่งก่อน");return;}
        String[] t=trips.get(idx);
        new CreateBookingGUI(userId,t[1],routeNames.getOrDefault(t[1],t[1]),LocalDate.parse(t[3]),LocalTime.parse(t[4]),new ArrayList<>(selected),Double.parseDouble(t[5]),service,()->refreshSeats()).setVisible(true);
    }
}
