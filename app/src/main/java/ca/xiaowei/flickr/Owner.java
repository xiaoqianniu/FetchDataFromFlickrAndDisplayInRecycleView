package ca.xiaowei.flickr;

public class Owner {
    private String username;
    private String authorName;
    private String authorPortraitUrl;

    public Owner(String username,String authorName, String authorPortraitUrl) {
        this.username = username;
        this.authorName = authorName;
        this.authorPortraitUrl = authorPortraitUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorPortraitUrl() {
        return authorPortraitUrl;
    }
}
