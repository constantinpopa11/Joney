package it.unitn.disi.joney;

public class UserProfileImage {
    String source;
    int userId;

    public UserProfileImage() {
    }

    public UserProfileImage(String source, int userId) {
        this.source = source;
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
