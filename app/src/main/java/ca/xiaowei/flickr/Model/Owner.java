package ca.xiaowei.flickr.Model;

import java.io.Serializable;

public class Owner implements Serializable {
    private String username;
    private String authorName;
    private String authorPortraitUrl;

    private String displayName;

    public Owner(String username,String authorName,String authorPortraitUrl) {
        this.username = username;
        this.authorName = authorName;
        this.authorPortraitUrl = authorPortraitUrl;
    }
    public Owner(String displayName,String authorPortraitUrl){
        this.displayName = displayName;
        this.authorPortraitUrl = authorPortraitUrl;
    }

    public String getDisplayName() {
        return displayName;
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
