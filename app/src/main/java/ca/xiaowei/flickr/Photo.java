package ca.xiaowei.flickr;

public class Photo {
    private String id;
    private String owner;
    private String secret;
    private String server;
    private String farm;
    private String title;
    private boolean isPublic;
    private boolean isFriend;
    private boolean isFamily;
public  Photo(String id, String owner){
    this.id = id;
    this.owner = owner;

}
    public Photo(String id, String secret, String server, String farm) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
    }
    public Photo(String id,String owner, String secret, String server,String farm,
                 String title,boolean isPublic,boolean isFriend,boolean isFamily){
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.isPublic = isPublic;
        this.isFriend = isFriend;
        this.isFamily = isFamily;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getServer() {
        return server;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getFarm() {
        return farm;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setFamily(boolean family) {
        isFamily = family;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", secret='" + secret + '\'' +
                ", server='" + server + '\'' +
                ", farm=" + farm +
                ", title='" + title + '\'' +
                ", isPublic=" + isPublic +
                ", isFriend=" + isFriend +
                ", isFamily=" + isFamily +
                '}';
    }

    public String getImageUrl() {
        System.out.println("farm: " + farm);
        System.out.println("server: " + server);
        System.out.println("id: " + id);
        System.out.println("secret: " + secret);
//        You can use these parameters to get the full URL of the photo:
//        http://farm{farm}.static.flickr.com/{server}/{id}_{secret}.jpg
        return "https://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
    public String getImageUrlTwo() {
        return "https://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
    public String getImageUrlThree() {
        return "https://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
    public String getImageUrlFour() {
        return "https://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
}
