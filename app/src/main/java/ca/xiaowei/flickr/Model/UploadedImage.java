package ca.xiaowei.flickr.Model;

import android.net.Uri;

public class UploadedImage {

    private Uri imageUri;
    private String title;

    public UploadedImage(String imageUri, String title) {
        this.imageUri = Uri.parse(imageUri);
        this.title = title;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
