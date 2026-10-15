package login;

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
        if (this.username.equals(inputUsername) && this.password.equals(inputPassword)) {
            System.out.println("Admin Login สำเร็จ: ยินดีต้อนรับคุณ " + this.username);
            return true;
        } else {
            System.out.println("Admin Login ไม่สำเร็จ: ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง");
            return false;
        }
    }
}