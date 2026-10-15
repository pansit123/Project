package busbooking.data;

import busbooking.model.Booking;
import busbooking.model.BookingStatus;
import busbooking.model.Payment;
import busbooking.model.PaymentMethod;
import busbooking.model.PaymentStatus;
import busbooking.model.Ticket;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * ศูนย์รวมตรรกะของระบบ "จองตั๋วและชำระเงิน" (ส่วนของคนที่ 4)
 *
 * หน้าที่:
 *   - สร้างการจอง / ยกเลิกการจอง / ดูประวัติการจอง
 *   - ชำระเงิน (จำลอง) / ออกตั๋ว / พิมพ์ตั๋ว
 *   - ตอบคำถามเรื่องที่นั่ง (ที่นั่งไหนถูกจองแล้ว) ให้หน้าค้นหาของคนที่ 3
 *   - สรุปยอดจองรายวัน ให้หน้า Admin ของคนที่ 2
 *
 * การเก็บข้อมูล: ไฟล์ CSV 3 ไฟล์ อ่าน/เขียนผ่าน busbooking.data.CSVManager (ของคนที่ 2)
 *   bookings.csv  - การจอง (1 แถว = 1 ที่นั่ง)
 *   payments.csv  - การชำระเงิน
 *   tickets.csv   - ตั๋ว
 *
 * หมายเหตุ: CSVManager แยกคอลัมน์ด้วย split(",") ดังนั้นค่าทุกช่องต้องไม่มีเครื่องหมายคอมมา
 * (คลาสนี้ตรวจให้ที่ createBooking) และทุกเมธอดอ่าน-แก้-เขียนไฟล์ใหม่ทุกครั้ง
 * เหมาะกับแอปที่ผู้ใช้ทีละคน ไม่ได้รองรับการเขียนไฟล์พร้อมกันหลายโปรเซส
 */
public class BookingService {

    // ชื่อไฟล์ข้อมูล
    public static final String BOOKINGS_FILE = "bookings.csv";
    public static final String PAYMENTS_FILE = "payments.csv";
    public static final String TICKETS_FILE = "tickets.csv";
    /** ไฟล์เส้นทางของคนที่ 2 (ใช้แค่เอาชื่อเส้นทางมาแสดง ถ้าไม่มีไฟล์ก็แสดงเป็น routeId แทน) */
    public static final String ROUTES_FILE = "routes.csv";

    /** การจองที่ยังไม่ชำระ จะถือว่าหมดเวลาเมื่อเกินกี่นาทีหลังจอง (ที่นั่งจะว่างให้คนอื่น) */
    public static final int PENDING_MINUTES = 15;

    /** โฟลเดอร์ที่เก็บไฟล์ CSV ("." = โฟลเดอร์ที่รันโปรแกรม ซึ่งตรงกับที่ส่วนอื่นของโปรเจกต์ใช้) */
    private final String dir;

    public BookingService() {
        this(".");
    }

    /** ระบุโฟลเดอร์เอง (ใช้ตอนทดสอบ จะได้ไม่ไปปนกับข้อมูลจริง) */
    public BookingService(String dir) {
        this.dir = dir;
        File d = new File(dir);
        if (!d.exists()) {
            d.mkdirs();
        }
    }

    // =====================================================================
    //  1) ที่นั่ง (ให้คนที่ 3 เรียกใช้ตอนวาดผังที่นั่ง)
    // =====================================================================

    /**
     * คืนเลขที่นั่งที่ "ถูกจองไปแล้ว" ของเที่ยวหนึ่งๆ
     * นับทั้งสถานะ รอชำระเงิน และ สำเร็จ (ที่ยกเลิกแล้วไม่นับ เพราะที่นั่งกลับมาว่าง)
     */
    public Set<String> getBookedSeats(String routeId, LocalDate date, LocalTime time) {
        Set<String> seats = new LinkedHashSet<>();
        for (Booking b : loadBookings()) {
            if (isSameTrip(b, routeId, date, time) && holdsSeat(b)) {
                seats.add(b.getSeatNo());
            }
        }
        return seats;
    }

    /** ที่นั่งนี้ยังว่างไหม (เทียบแบบไม่สนตัวพิมพ์เล็ก/ใหญ่ เช่น a3 = A3) */
    public boolean isSeatAvailable(String routeId, LocalDate date, LocalTime time, String seatNo) {
        return !takenSeatsUpper(loadBookings(), routeId, date, time)
                .contains(seatNo.trim().toUpperCase());
    }

    /** จำนวนที่นั่งที่ถูกจองไปแล้วของเที่ยวนี้ (ให้ Admin ใช้คำนวณที่ว่าง = ที่นั่งทั้งหมด - ค่านี้) */
    public int countBookedSeats(String routeId, LocalDate date, LocalTime time) {
        return getBookedSeats(routeId, date, time).size();
    }

    /**
     * สรุปยอดจองของวันหนึ่ง ให้หน้า Admin ของคนที่ 2
     * คืน Map: key = "routeId|เวลาออกรถ" (เช่น "R01|08:00"), value = จำนวนที่นั่งที่ถูกจอง
     * เที่ยวที่ไม่มีคนจองจะไม่อยู่ใน Map (ตรงกับที่อาจารย์บอกว่าเที่ยวที่ไม่มีคนไม่ต้องแสดง)
     */
    public Map<String, Integer> getBookingSummary(LocalDate date) {
        Map<String, Integer> summary = new TreeMap<>();
        for (Booking b : loadBookings()) {
            if (b.getTravelDate().equals(date) && holdsSeat(b)) {
                String key = b.getRouteId() + "|" + b.getDepartTime();
                Integer old = summary.get(key);
                summary.put(key, old == null ? 1 : old + 1);
            }
        }
        return summary;
    }

    // =====================================================================
    //  2) สร้างการจอง  (createBooking)
    // =====================================================================

    /**
     * สร้างการจอง สถานะเริ่มต้น "รอชำระเงิน" (PENDING) โดยสร้าง 1 แถวต่อ 1 ที่นั่ง
     *
     * ขั้นตอน: ตรวจข้อมูล -> ตรวจว่าที่นั่งยังว่าง -> บันทึกลง bookings.csv
     * ถ้าตรวจไม่ผ่านจะโยน BookingException และไม่เขียนอะไรลงไฟล์เลย
     *
     * @param pricePerSeat ราคาต่อ 1 ที่นั่ง (ได้มาจากหน้าค้นหาของคนที่ 3)
     * @return รายการจองที่สร้างใหม่
     */
    public List<Booking> createBooking(String userId, String routeId, LocalDate date,
                                       LocalTime time, List<String> seatNos,
                                       double pricePerSeat) throws BookingException {
        // --- ตรวจข้อมูลเบื้องต้น ---
        if (isBlank(userId)) {
            throw new BookingException("กรุณาเข้าสู่ระบบก่อนจองตั๋ว");
        }
        if (isBlank(routeId) || date == null || time == null) {
            throw new BookingException("ข้อมูลเที่ยวรถไม่ครบ (เส้นทาง/วันที่/เวลา)");
        }
        if (seatNos == null || seatNos.isEmpty()) {
            throw new BookingException("กรุณาเลือกที่นั่งอย่างน้อย 1 ที่นั่ง");
        }
        if (pricePerSeat < 0) {
            throw new BookingException("ราคาต้องไม่ติดลบ");
        }
        noComma(userId, "รหัสผู้ใช้");
        noComma(routeId, "รหัสเส้นทาง");
        if (LocalDateTime.of(date, time).isBefore(LocalDateTime.now())) {
            throw new BookingException("เที่ยวรถนี้ออกเดินทางไปแล้ว");
        }

        // --- จัดรูปแบบที่นั่ง และตรวจว่าไม่ซ้ำกันเอง ---
        List<String> seats = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String s : seatNos) {
            String seat = (s == null) ? "" : s.trim();
            if (seat.isEmpty()) {
                throw new BookingException("เลขที่นั่งต้องไม่ว่าง");
            }
            noComma(seat, "เลขที่นั่ง");
            if (!seen.add(seat.toUpperCase())) {
                throw new BookingException("เลือกที่นั่ง " + seat + " ซ้ำกัน");
            }
            seats.add(seat);
        }

        // --- ตรวจว่าที่นั่งยังว่างจริง (กันจองซ้ำ) ---
        List<Booking> all = loadBookings();
        Set<String> taken = takenSeatsUpper(all, routeId.trim(), date, time);
        for (String seat : seats) {
            if (taken.contains(seat.toUpperCase())) {
                throw new BookingException("ที่นั่ง " + seat + " ถูกจองแล้ว กรุณาเลือกที่นั่งใหม่");
            }
        }

        // --- สร้างรายการจอง แล้วบันทึกครั้งเดียว ---
        LocalDateTime now = LocalDateTime.now().withNano(0);
        List<Booking> created = new ArrayList<>();
        for (String seat : seats) {
            String id = nextId("BK", bookingIds(all));
            Booking b = new Booking(id, userId.trim(), routeId.trim(), date, time,
                    seat, pricePerSeat, BookingStatus.PENDING, now);
            all.add(b);       // เพิ่มเข้า list เพื่อให้รหัสถัดไปรันต่อเนื่อง
            created.add(b);
        }
        saveBookings(all);
        return created;
    }

    // =====================================================================
    //  3) ชำระเงิน (จำลอง)  (processPayment / getPaymentStatus)
    // =====================================================================

    /**
     * ชำระเงินของการจอง 1 รายการ (จำลอง: ถือว่าสำเร็จเสมอถ้าการจองอยู่ในสถานะรอชำระเงิน)
     * ผลลัพธ์: เพิ่มแถวใน payments.csv และเปลี่ยนสถานะการจองเป็น "สำเร็จ" (PAID)
     */
    public Payment processPayment(String bookingId, PaymentMethod method) throws BookingException {
        if (method == null) {
            throw new BookingException("กรุณาเลือกวิธีชำระเงิน");
        }
        List<Booking> all = loadBookings();
        Booking b = findIn(all, bookingId);
        if (b == null) {
            throw new BookingException("ไม่พบการจองรหัส " + bookingId);
        }
        if (b.getStatus() == BookingStatus.PAID) {
            throw new BookingException("การจองนี้ชำระเงินแล้ว");
        }
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("การจองนี้ถูกยกเลิกแล้ว ไม่สามารถชำระเงินได้");
        }
        if (hasDeparted(b)) {
            throw new BookingException("เที่ยวรถนี้ออกเดินทางไปแล้ว");
        }
        if (isExpired(b)) {
            throw new BookingException("หมดเวลาชำระเงิน (เกิน " + PENDING_MINUTES + " นาที) กรุณาจองใหม่");
        }

        // บันทึกการชำระเงินก่อน แล้วค่อยเปลี่ยนสถานะการจอง
        List<Payment> payments = loadPayments();
        Payment p = new Payment(nextId("PM", paymentIds(payments)), b.getBookingId(), method,
                b.getPrice(), PaymentStatus.SUCCESS, LocalDateTime.now().withNano(0));
        payments.add(p);
        savePayments(payments);

        b.setStatus(BookingStatus.PAID);
        saveBookings(all);
        return p;
    }

    /** สถานะการชำระเงินล่าสุดของการจองนี้ (คืน null ถ้ายังไม่เคยชำระ) */
    public PaymentStatus getPaymentStatus(String bookingId) {
        PaymentStatus result = null;
        for (Payment p : loadPayments()) {
            if (p.getBookingId().equals(bookingId)) {
                result = p.getStatus();   // แถวหลังสุดคือล่าสุด
            }
        }
        return result;
    }

    // =====================================================================
    //  4) ออกตั๋ว / พิมพ์ตั๋ว  (issueTicket / printTicket)
    // =====================================================================

    /**
     * ออกตั๋วให้การจองที่ "ชำระเงินสำเร็จแล้ว" เท่านั้น
     * ถ้าการจองนี้เคยออกตั๋วแล้ว จะคืนตั๋วใบเดิม (ไม่ออกซ้ำ)
     */
    public Ticket issueTicket(String bookingId) throws BookingException {
        Booking b = findBooking(bookingId);
        if (b == null) {
            throw new BookingException("ไม่พบการจองรหัส " + bookingId);
        }
        if (b.getStatus() != BookingStatus.PAID) {
            throw new BookingException("ออกตั๋วได้เฉพาะการจองที่ชำระเงินสำเร็จแล้ว");
        }
        Ticket existing = findTicketByBooking(bookingId);
        if (existing != null) {
            return existing;
        }

        List<Ticket> tickets = loadTickets();
        String ticketId = nextId("TK", ticketIds(tickets));
        // เลขตั๋ว = SBT-วันที่เดินทาง(yyMMdd)-ลำดับตั๋ว เช่น SBT-261018-0001
        String number = "SBT-" + b.getTravelDate().format(DateTimeFormatter.ofPattern("yyMMdd"))
                + "-" + ticketId.substring(2);
        Ticket t = new Ticket(ticketId, bookingId, number, LocalDateTime.now().withNano(0));
        tickets.add(t);
        saveTickets(tickets);
        return t;
    }

    /**
     * ชำระเงิน + ออกตั๋ว สำหรับการจองหลายรายการในครั้งเดียว (ผู้ใช้จองหลายที่นั่ง)
     * ถ้ารายการใดล้มเหลวจะโยน BookingException ทันที (รายการก่อนหน้าที่สำเร็จแล้วยังคงอยู่)
     */
    public List<Ticket> payBookings(List<String> bookingIds, PaymentMethod method)
            throws BookingException {
        List<Ticket> tickets = new ArrayList<>();
        for (String id : bookingIds) {
            processPayment(id, method);
            tickets.add(issueTicket(id));
        }
        return tickets;
    }

    /**
     * พิมพ์ตั๋ว: เขียนตั๋วเป็นไฟล์ข้อความ (.txt) ไว้ในโฟลเดอร์ tickets/
     * (จำลองการพิมพ์ ไม่ได้ต่อเครื่องพิมพ์จริง) คืนไฟล์ที่สร้าง
     */
    public File printTicket(Ticket ticket) throws BookingException {
        Booking b = findBooking(ticket.getBookingId());
        if (b == null) {
            throw new BookingException("ไม่พบการจองของตั๋วใบนี้");
        }
        File folder = new File(dir, "tickets");
        folder.mkdirs();
        File out = new File(folder, ticket.getTicketNumber() + ".txt");

        StringBuilder sb = new StringBuilder();
        sb.append("========== ตั๋วโดยสารรถทัวร์ ==========\n");
        sb.append("เลขตั๋ว        : ").append(ticket.getTicketNumber()).append('\n');
        sb.append("รหัสการจอง     : ").append(b.getBookingId()).append('\n');
        sb.append("ผู้โดยสาร      : ").append(b.getUserId()).append('\n');
        sb.append("เส้นทาง        : ").append(getRouteName(b.getRouteId())).append('\n');
        sb.append("วันที่เดินทาง   : ").append(b.getTravelDate()).append('\n');
        sb.append("เวลาออกรถ      : ").append(b.getDepartTime()).append('\n');
        sb.append("ที่นั่ง         : ").append(b.getSeatNo()).append('\n');
        sb.append("ราคา           : ").append(String.format("%.2f", b.getPrice())).append(" บาท\n");
        sb.append("ออกตั๋วเมื่อ    : ").append(ticket.getIssuedAt().format(Booking.STAMP)).append('\n');
        sb.append("======================================\n");
        try {
            Files.write(out.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new BookingException("บันทึกไฟล์ตั๋วไม่สำเร็จ: " + e.getMessage());
        }
        return out;
    }

    // =====================================================================
    //  5) ยกเลิกการจอง / ดูประวัติ  (cancelBooking / viewBookingHistory)
    // =====================================================================

    /**
     * ยกเลิกการจอง -> สถานะเป็น "ยกเลิก" และที่นั่งกลับมาว่าง
     * ถ้าเคยชำระเงินแล้ว จะเปลี่ยนสถานะการชำระเงินเป็น "คืนเงินแล้ว" (จำลอง)
     *
     * เงื่อนไข: ต้องเป็นเจ้าของการจอง, ยังไม่เคยถูกยกเลิก, และรถยังไม่ออก
     */
    public void cancelBooking(String bookingId, String userId) throws BookingException {
        List<Booking> all = loadBookings();
        Booking b = findIn(all, bookingId);
        if (b == null) {
            throw new BookingException("ไม่พบการจองรหัส " + bookingId);
        }
        if (userId == null || !b.getUserId().equals(userId.trim())) {
            throw new BookingException("คุณไม่มีสิทธิ์ยกเลิกการจองนี้");
        }
        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("การจองนี้ถูกยกเลิกไปแล้ว");
        }
        if (hasDeparted(b)) {
            throw new BookingException("เที่ยวรถออกเดินทางไปแล้ว ไม่สามารถยกเลิกได้");
        }

        boolean wasPaid = (b.getStatus() == BookingStatus.PAID);
        b.setStatus(BookingStatus.CANCELLED);
        saveBookings(all);

        if (wasPaid) {   // จำลองการคืนเงิน
            List<Payment> payments = loadPayments();
            for (Payment p : payments) {
                if (p.getBookingId().equals(bookingId) && p.getStatus() == PaymentStatus.SUCCESS) {
                    p.setStatus(PaymentStatus.REFUNDED);
                }
            }
            savePayments(payments);
        }
    }

    /** ประวัติการจองของผู้ใช้ (ล่าสุดอยู่บนสุด) */
    public List<Booking> viewBookingHistory(String userId) {
        List<Booking> mine = new ArrayList<>();
        for (Booking b : loadBookings()) {
            if (b.getUserId().equals(userId)) {
                mine.add(b);
            }
        }
        Collections.sort(mine, new Comparator<Booking>() {
            @Override
            public int compare(Booking x, Booking y) {
                int c = y.getBookingDate().compareTo(x.getBookingDate());
                return c != 0 ? c : y.getBookingId().compareTo(x.getBookingId());
            }
        });
        return mine;
    }

    // =====================================================================
    //  6) ค้นหาข้อมูล
    // =====================================================================

    public Booking findBooking(String bookingId) {
        return findIn(loadBookings(), bookingId);
    }

    /** ตั๋วของการจองนี้ (คืน null ถ้ายังไม่ได้ออกตั๋ว) */
    public Ticket findTicketByBooking(String bookingId) {
        for (Ticket t : loadTickets()) {
            if (t.getBookingId().equals(bookingId)) {
                return t;
            }
        }
        return null;
    }

    /**
     * ชื่อเส้นทางสำหรับแสดงผล เช่น "กรุงเทพฯ - เชียงใหม่"
     * อ่านจาก routes.csv ของคนที่ 2 โดยสมมติว่าคอลัมน์คือ routeId,origin,destination,...
     * ถ้าไม่มีไฟล์หรือหาไม่เจอ จะคืน routeId กลับไปตรงๆ (โปรแกรมยังทำงานได้ตามปกติ)
     */
    public String getRouteName(String routeId) {
        for (String[] row : readRows(ROUTES_FILE, "routeId")) {
            if (row.length >= 3 && row[0].trim().equals(routeId)) {
                return row[1].trim() + " - " + row[2].trim();
            }
        }
        return routeId;
    }

    // =====================================================================
    //  7) ตัวช่วยตรวจข้อมูล (ให้หน้าจอเรียกใช้ได้)
    // =====================================================================

    /** เลขบัตรจำลองต้องเป็นตัวเลข 16 หลัก (ยอมให้มีช่องว่างหรือขีดคั่น) */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }
        String digits = cardNumber.replace(" ", "").replace("-", "");
        return digits.matches("\\d{16}");
    }

    // =====================================================================
    //  ส่วนภายใน: อ่าน/เขียนไฟล์ผ่าน CSVManager
    // =====================================================================

    /** การจองรอชำระที่เกินเวลา PENDING_MINUTES แล้ว (จ่ายไม่ได้ และไม่กันที่นั่งอีกต่อไป) */
    public static boolean isExpired(Booking b) {
        return b.getStatus() == BookingStatus.PENDING
                && b.getBookingDate().plusMinutes(PENDING_MINUTES).isBefore(LocalDateTime.now());
    }

    /** การจองนี้ยังกันที่นั่งอยู่ไหม = ไม่ถูกยกเลิก และไม่ใช่รอชำระที่หมดเวลา */
    private static boolean holdsSeat(Booking b) {
        return b.getStatus() != BookingStatus.CANCELLED && !isExpired(b);
    }

    /**
     * อ่านไฟล์ CSV เป็นแถวๆ โดยข้ามบรรทัดว่าง และข้ามแถวหัวตาราง
     * (ถ้ายังไม่มีไฟล์ จะคืนรายการว่าง โดยไม่ขึ้นข้อความ error ของ CSVManager)
     */
    private List<String[]> readRows(String fileName, String headerFirstCell) {
        List<String[]> rows = new ArrayList<>();
        File f = new File(dir, fileName);
        if (!f.exists()) {
            return rows;
        }
        for (String[] r : CSVManager.loadFromCSV(f.getPath())) {
            if (r.length == 0) {
                continue;
            }
            r[0] = r[0].replace("﻿", "");           // ตัดอักขระ BOM ที่ Excel อาจเติมไว้หน้าไฟล์
            if (r[0].trim().isEmpty() && r.length == 1) {
                continue;                                 // บรรทัดว่าง
            }
            if (r[0].trim().equals(headerFirstCell)) {
                continue;                                 // แถวหัวตาราง
            }
            rows.add(r);
        }
        return rows;
    }

    private List<Booking> loadBookings() {
        List<Booking> list = new ArrayList<>();
        for (String[] row : readRows(BOOKINGS_FILE, "bookingId")) {
            try {
                list.add(Booking.fromCsvRow(row));
            } catch (RuntimeException e) {
                // แถวเสียหาย: ข้ามไป ไม่ให้ทั้งโปรแกรมล้ม
                System.out.println("ข้ามแถวที่อ่านไม่ได้ใน " + BOOKINGS_FILE + ": " + String.join(",", row));
            }
        }
        return list;
    }

    private List<Payment> loadPayments() {
        List<Payment> list = new ArrayList<>();
        for (String[] row : readRows(PAYMENTS_FILE, "paymentId")) {
            try {
                list.add(Payment.fromCsvRow(row));
            } catch (RuntimeException e) {
                System.out.println("ข้ามแถวที่อ่านไม่ได้ใน " + PAYMENTS_FILE + ": " + String.join(",", row));
            }
        }
        return list;
    }

    private List<Ticket> loadTickets() {
        List<Ticket> list = new ArrayList<>();
        for (String[] row : readRows(TICKETS_FILE, "ticketId")) {
            try {
                list.add(Ticket.fromCsvRow(row));
            } catch (RuntimeException e) {
                System.out.println("ข้ามแถวที่อ่านไม่ได้ใน " + TICKETS_FILE + ": " + String.join(",", row));
            }
        }
        return list;
    }

    private void saveBookings(List<Booking> list) {
        List<String[]> rows = new ArrayList<>();
        rows.add(Booking.header());
        for (Booking b : list) {
            rows.add(b.toCsvRow());
        }
        CSVManager.saveToCSV(new File(dir, BOOKINGS_FILE).getPath(), rows);
    }

    private void savePayments(List<Payment> list) {
        List<String[]> rows = new ArrayList<>();
        rows.add(Payment.header());
        for (Payment p : list) {
            rows.add(p.toCsvRow());
        }
        CSVManager.saveToCSV(new File(dir, PAYMENTS_FILE).getPath(), rows);
    }

    private void saveTickets(List<Ticket> list) {
        List<String[]> rows = new ArrayList<>();
        rows.add(Ticket.header());
        for (Ticket t : list) {
            rows.add(t.toCsvRow());
        }
        CSVManager.saveToCSV(new File(dir, TICKETS_FILE).getPath(), rows);
    }

    // =====================================================================
    //  ส่วนภายใน: ตัวช่วยเล็กๆ
    // =====================================================================

    private static boolean isSameTrip(Booking b, String routeId, LocalDate date, LocalTime time) {
        return b.getRouteId().equals(routeId)
                && b.getTravelDate().equals(date)
                && b.getDepartTime().equals(time);
    }

    /** ที่นั่งที่ไม่ว่างของเที่ยวหนึ่ง (แปลงเป็นตัวพิมพ์ใหญ่เพื่อเทียบแบบไม่สนตัวพิมพ์) */
    private static Set<String> takenSeatsUpper(List<Booking> all, String routeId,
                                               LocalDate date, LocalTime time) {
        Set<String> taken = new LinkedHashSet<>();
        for (Booking b : all) {
            if (isSameTrip(b, routeId, date, time) && holdsSeat(b)) {
                taken.add(b.getSeatNo().toUpperCase());
            }
        }
        return taken;
    }

    private static boolean hasDeparted(Booking b) {
        return b.getDepartureDateTime().isBefore(LocalDateTime.now());
    }

    private static Booking findIn(List<Booking> list, String bookingId) {
        for (Booking b : list) {
            if (b.getBookingId().equals(bookingId)) {
                return b;
            }
        }
        return null;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** ค่าที่จะเขียนลง CSV ต้องไม่มีคอมมา (เพราะ CSVManager แยกคอลัมน์ด้วยคอมมา) */
    private static void noComma(String value, String fieldName) throws BookingException {
        if (value.contains(",")) {
            throw new BookingException(fieldName + "ต้องไม่มีเครื่องหมายคอมมา (,)");
        }
    }

    /** สร้างรหัสถัดไป เช่น prefix "BK" และมี BK0007 อยู่แล้ว -> "BK0008" */
    private static String nextId(String prefix, List<String> existingIds) {
        int max = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // รหัสรูปแบบอื่น ข้ามไป
                }
            }
        }
        return String.format("%s%04d", prefix, max + 1);
    }

    private static List<String> bookingIds(List<Booking> list) {
        List<String> ids = new ArrayList<>();
        for (Booking b : list) {
            ids.add(b.getBookingId());
        }
        return ids;
    }

    private static List<String> paymentIds(List<Payment> list) {
        List<String> ids = new ArrayList<>();
        for (Payment p : list) {
            ids.add(p.getPaymentId());
        }
        return ids;
    }

    private static List<String> ticketIds(List<Ticket> list) {
        List<String> ids = new ArrayList<>();
        for (Ticket t : list) {
            ids.add(t.getTicketId());
        }
        return ids;
    }
}
