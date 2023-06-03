package ca.xiaowei.flickr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.type.Date;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.xiaowei.flickr.DB.DBOpenHelper;
import ca.xiaowei.flickr.Model.Owner;
import ca.xiaowei.flickr.Model.Photo;
import ca.xiaowei.flickr.Utils.APIClient;
import ca.xiaowei.flickr.Utils.ApiCallbackInterface;
import ca.xiaowei.flickr.Utils.CircleTransform;
import ca.xiaowei.flickr.Utils.JSONParser;
import ca.xiaowei.flickr.Utils.URLManager;

//	PhotoShow
//Key:
//b6395da02b7b99dccae5051360c27ff1
//
//Secret:
//b36b56655659540e
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recyclerView;
    CustomRecycleViewAdapter customRecycleViewAdapter;
    ArrayList<Photo> listOfPhotos;
    ImageView head_imageView, author_portrait;
    TextView author_name;
    Button saveDbBtn, fetchFromDbBtn, cameraBtn;
    private Dialog imageDialog;
    private static final int PHOTO_CAPTURE_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        fetchPhotosFromAPI();

    }

    private void initializeViews() {
        listOfPhotos = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        customRecycleViewAdapter = new CustomRecycleViewAdapter(this, listOfPhotos, imageDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(customRecycleViewAdapter);

        head_imageView = findViewById(R.id.header_imageView);
        author_portrait = findViewById(R.id.author_portrait);
        author_name = findViewById(R.id.author_name);
        saveDbBtn = findViewById(R.id.saveToDB);
        saveDbBtn.setOnClickListener(this);
        fetchFromDbBtn = findViewById(R.id.fetchFromDB);
        fetchFromDbBtn.setOnClickListener(this);
        cameraBtn = findViewById(R.id.btnCamera);
        cameraBtn.setOnClickListener(this);
        imageDialog = new Dialog(this);
        imageDialog.setContentView(R.layout.dialog_large_image);
    }

    private void fetchPhotosFromAPI() {
        String apiUrl = URLManager.getItemUrl(null, 1); // Replace null with your search query if needed
        APIClient apiClient = new APIClient(this);
        apiClient.fetchData(apiUrl, new ApiCallbackInterface() {
            @Override
            public void onSuccess(String response) {

                JSONParser jsonParser = new JSONParser();
                ArrayList<Photo> fetchedPhotos = jsonParser.processJSONData(response);

                if (fetchedPhotos != null) {

                    listOfPhotos.clear();
                    listOfPhotos.addAll(fetchedPhotos);

                    System.out.println("listOfPhotos size: " + listOfPhotos.size());

                    customRecycleViewAdapter.notifyDataSetChanged();

                    if (!listOfPhotos.isEmpty()) {

                        Photo firstPhoto = listOfPhotos.get(0);
                        String head_url = firstPhoto.getImageUrl();
                        Picasso.get()
                                .load(head_url)
                                .placeholder(R.drawable.placeholderimage)
                                .into(head_imageView);
                        String ownerID = firstPhoto.getOwner();
                        fetchOwnerInfo(ownerID);
                    }

                } else {
                    System.out.println("No photos fetched");
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the failure/error case and display an appropriate message
                System.out.println("///////////////////////////////////////fail");
            }
        });
    }


    private void fetchOwnerInfo(String ownerID) {
        String apiUrl = URLManager.getOwnerInfoUrl(ownerID);
        // Make an API request to fetch owner information using the owner ID
        // You can use the same API client and callback interface as in fetchPhotosFromAPI()
        APIClient apiClient = new APIClient(this);
        apiClient.fetchData(apiUrl, new ApiCallbackInterface() {
            @Override
            public void onSuccess(String response) {
                JSONParser jsonParser = new JSONParser();

                Owner owner = jsonParser.parseOwnerInfo(response);

                if (owner != null) {
                    // Retrieve the author name and author portrait URL from the parsed Owner object
                    String authorName = owner.getDisplayName();
                    String authorPortraitUrl = owner.getAuthorPortraitUrl();

                    System.out.println("author name ///////////" + authorName);

                    // Update the corresponding views with the retrieved information
                    author_name.setText(authorName);
                    if (authorPortraitUrl != null) {
                        Picasso.get()
                                .load(authorPortraitUrl)
                                .placeholder(R.drawable.placeholderimage)
                                .transform(new CircleTransform())
                                .into(author_portrait);

                    } else {
                        author_portrait.setImageResource(R.drawable.placeholderimage);
                        System.out.println("success to parse owner information");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to parse owner information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, "Failed to fetch owner information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveToDB) {
            savePhotosToDB(listOfPhotos);
        } else if (v.getId() == R.id.fetchFromDB) {
            FetchPhotosFromDB();

        } else if (v.getId() == R.id.btnCamera) {
            goToCamera();
        }
    }

    private void savePhotosToDB(List<Photo> listOfPhotos) {
        DBOpenHelper dbHelper = new DBOpenHelper(this);

        for (Photo photo : listOfPhotos) {
            dbHelper.addPhoto(photo);
        }

        Toast.makeText(MainActivity.this, "Saved to SQLite database successfully", Toast.LENGTH_SHORT).show();

    }

    private void FetchPhotosFromDB() {
//todo:need to modify this fuction
        DBOpenHelper dbHelper = new DBOpenHelper(this);
        List<Photo> fetchedPhotos = dbHelper.getAllPhotos();
        System.out.println("fetchedPhotos:" + fetchedPhotos);
        if (fetchedPhotos != null) {
            listOfPhotos.clear();
            for (Photo photo : fetchedPhotos) {
                String imageUrl = "https://farm" + photo.getFarm() + ".static.flickr.com/" + photo.getServer() + "/" + photo.getId() + "_" + photo.getSecret() + ".jpg";
                Photo newPhoto = new Photo(photo.getId(), photo.getServer(), photo.getSecret(), photo.getFarm());
                newPhoto.setImageUrl(imageUrl);
                listOfPhotos.add(newPhoto);
            }
            customRecycleViewAdapter.notifyDataSetChanged();

        }
    }

    private void goToCamera() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
}