package login;
class User {
    private String userId;
    private String fullName;
    private String email;
    private String password;
    private String phone;

    public User(String userId, String fullName, String email, String password, String phone) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public boolean login(String inputEmail, String inputPassword) {
        return this.email.equals(inputEmail) && this.password.equals(inputPassword);
    }
}