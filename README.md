# Part 4 — Payment / Ticket / Booking History

ส่วนงานคนที่ 4 จากมินิโปรเจกต์ระบบจองตั๋วรถทัวร์ (Java Swing + CSV)

## สิ่งที่รวมไว้
- `PaymentFrame.java`, `PaymentPanel.java`: หน้าชำระเงินแบบจำลอง
- `TicketFrame.java`: แสดง/พิมพ์ตั๋ว
- `BookingHistoryFrame.java`: ประวัติการจอง ชำระเงินภายหลัง ดูตั๋ว และยกเลิกตามเงื่อนไข
- `BookingService.java`: กฎธุรกิจส่วนกลางสำหรับชำระเงิน ออกตั๋ว ประวัติ และยกเลิก
- `model/`, `CSVManager.java`, `BookingException.java`: คลาสโมเดลและตัวช่วยที่หน้าจอจำเป็นต้องใช้
- CSV ที่ให้มาเป็นไฟล์ template มีเฉพาะหัวตาราง ไม่มีข้อมูลสมาชิก/การจองจริง

## หมายเหตุการเชื่อมต่อ
นี่คือชุดซอร์สเฉพาะงานคนที่ 4 พร้อมคลาสที่จำเป็นต่อการคอมไพล์ ไม่ใช่แอปแยกที่มีหน้า Login/Main สำหรับเริ่มโปรแกรมเอง
คนที่ 1/3 ต้องเรียกหน้าประวัติ/ชำระเงินด้วย `userId` และ `bookingId` ที่ถูกต้อง เช่น:

```java
new busbooking.ui.BookingHistoryFrame(userId).setVisible(true);
new busbooking.ui.PaymentFrame(userId, bookingIds).setVisible(true);
```

ควรนำโฟลเดอร์ `src/busbooking/` ไปผสานกับโปรเจกต์หลักของทีม โดยหลีกเลี่ยงการแทนที่ `bookings.csv` ของทีมด้วย template ว่างนี้ เพราะข้อมูลจริงของทีมจะถูกเขียนทับได้

## คอมไพล์ตรวจสอบ
จากโฟลเดอร์นี้ ใช้ JDK 17 ขึ้นไป:

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')
```

ไฟล์ทดสอบ `tests/BookingServiceTest.java` ใช้สำหรับตรวจสอบ service; หากต้องการรัน ให้คอมไพล์ `src` และ `tests` พร้อมกันแล้วรัน `tests.BookingServiceTest`
