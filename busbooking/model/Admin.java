
package busbooking.model;

public class Admin {

    private String adminId;
    private String username;
    private String password;

    public Admin(String adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean login(String username, String password) {
        return this.username.equals(username)
                && this.password.equals(password);
    }

    public void manageBus() {
        System.out.println("Manage Bus");
    }

    public void manageRoute() {
        System.out.println("Manage Route");
    }

    public void manageTrip() {
        System.out.println("Manage Trip");
    }
}