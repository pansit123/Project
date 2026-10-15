package busbooking.model;

public class Admin {

    private String adminId;
    private String username;
    private String password;

    // Constructor
   
    public Admin(String adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }

    // Getter
  
    public String getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Login

    public boolean login(String username, String password) {

        return this.username.equals(username)
                && this.password.equals(password);
    }
}
