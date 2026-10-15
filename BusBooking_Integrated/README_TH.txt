BUS BOOKING - INTEGRATED VERSION

เปิดโฟลเดอร์ BusBooking_Integrated ใน VS Code (Java Extension Pack)
รันคลาส busbooking.Main หรือใช้ run.bat บน Windows (ต้องติดตั้ง JDK)

Admin: ใช้ข้อมูลเข้าสู่ระบบเดิมใน busbooking/ui/AdminLoginFrame.java
User ตัวอย่าง: somchai@email.com / password123
User ใหม่: สมัครสมาชิกในหน้า RegisterFormUI; ข้อมูลเก็บ users_database.txt

ข้อมูลร่วม:
 busbooking/data/routes.csv, bustypes.csv, trip_schedules.csv
 busbooking/data/bookings.csv (9 คอลัมน์), payments.csv, tickets.csv
 bookings_legacy.csv เป็นข้อมูล Admin เก่าที่สำรองไว้ ไม่ได้นำมาแสดงในยอดจองใหม่

เมื่อทดสอบจองให้เลือกเที่ยวรถในอนาคต (ข้อมูลตัวอย่าง 1-2 พ.ย. 2026)
โปรแกรมนี้เป็นตัวอย่างสำหรับงานเรียน: เก็บรหัสผ่านเป็นข้อความธรรมดา ไม่เหมาะกับระบบจริง
จำนวนที่นั่งหน้าเลือกที่นั่งรองรับได้สูงสุด 24 ที่นั่งต่อเที่ยวในเวอร์ชันนี้
ห้ามเปิดจากโฟลเดอร์ย่อย เพราะไฟล์ CSV ใช้ path เทียบจากโฟลเดอร์โปรเจกต์


UPDATE 2026-10-15: Updated MUAY login/register to phone+password users.csv. Successful login opens UserHomeFrame, with phone as booking user ID. Copied login/images/comsci.jpg. KRIT/SENSE/Admin uploaded versions are unchanged from previous originals. Main buttons explicitly use Tahoma and javac uses UTF-8.
