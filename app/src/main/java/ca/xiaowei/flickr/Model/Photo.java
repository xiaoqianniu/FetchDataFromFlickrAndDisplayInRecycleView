package ca.xiaowei.flickr.Model;

public class Photo {
    private String id;
    private String owner;
    private String secret;
    private String server;
    private String farm;
    private String title;
    private String imageUrl;

public  Photo(String id, String owner){
    this.id = id;
    this.owner = owner;

}
public Photo(){}
    public Photo(String id, String secret, String server, String farm) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
    }
    public Photo(String id,String owner, String secret, String server,String farm,
                 String title){
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
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

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", secret='" + secret + '\'' +
                ", server='" + server + '\'' +
                ", farm=" + farm +
                ", title='" + title +
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
