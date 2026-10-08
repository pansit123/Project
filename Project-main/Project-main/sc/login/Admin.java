package login;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Admin {
    private String adminId;
    private String username;
    private String password;

    public Admin(String adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }

    public boolean login(String inputUsername, String inputPassword) {
        // ตรวจสอบจากไฟล์ admin_database.txt
        try (BufferedReader br = new BufferedReader(new FileReader("admin_database.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split("\\|");
                if (data.length >= 2) {
                    String savedUser = data[0].trim();
                    String savedPass = data[1].trim();

                    if (savedUser.equals(inputUsername) && savedPass.equals(inputPassword)) {
                        System.out.println("Admin Login สำเร็จ: ยินดีต้อนรับคุณ " + savedUser);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            // ถ้ายังไม่มีไฟล์ ให้ใช้ค่าเริ่มต้นสำรองด้านล่าง
        }

        // กรณีสำรอง (Hardcode พื้นฐาน)
        if (this.username != null && this.username.equals(inputUsername) && this.password.equals(inputPassword)) {
            return true;
        }

        return false;
    }
}