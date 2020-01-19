package app.music.musicstore.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userName;
    private String displayName;
    private String password;

    public LoggedInUser(String userId, String displayName) {
        this.userName = userId;
        this.displayName = displayName;
    }

    public String getUserId() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;};
}
