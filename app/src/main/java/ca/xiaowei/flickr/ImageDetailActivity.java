package ca.xiaowei.flickr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ca.xiaowei.flickr.Model.Photo;

public class ImageDetailActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView;
    TextView dataText;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        initialize();

        getMyIntent();
    }

    private void initialize() {
        imageView = findViewById(R.id.detail_image);
        dataText = findViewById(R.id.detail_data);
        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(this);
    }

    private void getMyIntent() {

        Photo photo = (Photo) getIntent().getSerializableExtra("onePhoto");

        // Use the photo object to access its properties
        String imageUrl = photo.getImageUrl();
        String id = photo.getId();
        String owner = photo.getOwner();
        String title = photo.getTitle();
        String secret = photo.getSecret();
        String server = photo.getServer();
        String farm = photo.getFarm();

        // Set the text in TextViews
        dataText.setText("ID:" + id + "\n" + "Owner:" + owner + "\n" + "Title: " + title + "\n" + "Secret: " + secret + "\n" + "Server: " + server +
                "\n" + "Farm: " + farm + "\n");

        // Load the image using the image URL

        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.placeholderimage)
                .into(imageView);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_button) {
            goToBack();
        }
    }

    private void goToBack() {
        Intent intent = new Intent(ImageDetailActivity.this, MainActivity.class);
        startActivity(intent);

    }
}