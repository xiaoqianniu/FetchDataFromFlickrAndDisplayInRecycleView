package ca.xiaowei.flickr;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.xiaowei.flickr.DB.DBOpenHelper;
import ca.xiaowei.flickr.Model.Owner;
import ca.xiaowei.flickr.Model.Photo;
import ca.xiaowei.flickr.Model.UploadedImage;
import ca.xiaowei.flickr.Utils.APIClient;
import ca.xiaowei.flickr.Utils.ApiCallbackInterface;
import ca.xiaowei.flickr.Utils.CircleTransform;
import ca.xiaowei.flickr.Utils.JSONParser;
import ca.xiaowei.flickr.Utils.URLManager;
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
    Button saveDbBtn, cameraBtn, uploadButton;
    Spinner gridSpinner;
    private Dialog imageDialog;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private Dialog dialog;
    private List<UploadedImage> uploadedImagesList = new ArrayList<>();
    private Uri selectedImageUri;
    private boolean imageSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        checkInternetConnection();

        setupSpinner();
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
        cameraBtn = findViewById(R.id.btnCamera);
        cameraBtn.setOnClickListener(this);
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);
        imageDialog = new Dialog(this);
        imageDialog.setContentView(R.layout.dialog_large_image);
        gridSpinner = findViewById(R.id.grid_choose);
    }

    private void checkInternetConnection() {
        // Check internet availability
        if (NetworkUtils.isInternetAvailable(this)) {
            // Call the API to fetch the photos
            fetchPhotosFromAPI();
            Toast.makeText(this, "Internet connection works well", Toast.LENGTH_SHORT).show();
        } else {
            // Load the photos from the local database
            fetchPhotosFromCache();
            Toast.makeText(this, "Internet connection has problem", Toast.LENGTH_SHORT).show();
        }
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
 //                   savePhotosToDB(listOfPhotos);
                    savePhotosToCache();
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
    private void fetchPhotosFromCache() {
        // Load the photos from the cache
        List<Photo> cachedPhotos = loadPhotosFromCache();

        // If there are cached photos available, display them
        if (cachedPhotos != null && !cachedPhotos.isEmpty()) {
            displayPhotos(cachedPhotos);
        } else {
            Toast.makeText(this, "No cached photos available", Toast.LENGTH_SHORT).show();
        }
    }
    private void displayPhotos(List<Photo> photos) {

        CustomRecycleViewAdapter adapter = new CustomRecycleViewAdapter(photos);
        recyclerView.setAdapter(adapter);
    }
    private void savePhotosToCache() {
        // Convert the photos to a JSON array
        JSONArray jsonArray = new JSONArray();
        for (Photo photo : listOfPhotos) {
            JSONObject photoJson = new JSONObject();
            try {
                photoJson.put("id", photo.getId());
                photoJson.put("url", photo.getImageUrl());
                jsonArray.put(photoJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Store the JSON array in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PhotoCache", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CachedPhotos", jsonArray.toString());
        editor.apply();
    }

    private List<Photo> loadPhotosFromCache() {
        List<Photo> cachedPhotos = new ArrayList<>();

        // Retrieve the cached photos from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("PhotoCache", Context.MODE_PRIVATE);
        String cachedPhotosJson = sharedPreferences.getString("CachedPhotos", null);

        // If there are cached photos available, parse the JSON string and create Photo objects
        if (cachedPhotosJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(cachedPhotosJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject photoJson = jsonArray.getJSONObject(i);
                    // Assuming you have a Photo class with appropriate fields and constructor
                    Photo photo = new Photo(photoJson.getString("id"), photoJson.getString("url"));
                    cachedPhotos.add(photo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cachedPhotos;
    }
    private void fetchPhotosFromDB() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this);

        List<Photo> photoList = dbOpenHelper.getAllPhotos();

        StringBuilder dataBuilder = new StringBuilder();
        for (Photo photo : photoList) {

        }

        Toast.makeText(this, "Data read successfully", Toast.LENGTH_SHORT).show();
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
        } else if (v.getId() == R.id.btnCamera) {
            goToCamera();
        } else if (v.getId() == R.id.uploadButton) {
            uploadPhoto();
        }
    }

    private void savePhotosToDB(List<Photo> listOfPhotos) {
        DBOpenHelper dbHelper = new DBOpenHelper(this);

        for (Photo photo : listOfPhotos) {
            dbHelper.addPhoto(photo);
        }

        Toast.makeText(MainActivity.this, "Saved to SQLite database successfully", Toast.LENGTH_SHORT).show();

    }

    private void goToCamera() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    private void uploadPhoto() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.upload_image);

        ImageView imageView = dialog.findViewById(R.id.imageView);
        EditText titleEditText = dialog.findViewById(R.id.titleEditText);
        Button selectImageButton = dialog.findViewById(R.id.selectImageButton);
        Button submitButton = dialog.findViewById(R.id.submitButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImageUri != null) {
                    String title = titleEditText.getText().toString();
                    // Create a new UploadedImage instance with the image URI and title
                    UploadedImage uploadedImage = new UploadedImage(selectedImageUri.toString(), title);

                    // Add the uploadedImage to the uploadedImagesList
                    uploadedImagesList.add(uploadedImage);
                    // Notify the adapter that the dataset has changed
                    customRecycleViewAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Upload image successfully", Toast.LENGTH_SHORT).show();
                    if (imageSelected) {
                        head_imageView.setImageURI(selectedImageUri);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // The user has successfully selected an image
            selectedImageUri = data.getData();
            imageSelected = true;
            ImageView dialogImageView = dialog.findViewById(R.id.imageView);

            dialogImageView.setImageURI(selectedImageUri);
        }
    }

    private void setupSpinner() {
        Spinner gridSpinner = findViewById(R.id.grid_choose);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grid, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gridSpinner.setAdapter(adapter);
        gridSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Call the appropriate method based on the selected item
                switch (position) {
                    case 0:
                        setSimpleVerticalLayoutManager();
                        break;
                    case 1:
                        setSimpleHorizontalLayoutManager();
                        break;
                    case 2:
                        setTwoColumnLayoutManager();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
    }

    private void setSimpleVerticalLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setSimpleHorizontalLayoutManager() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setTwoColumnLayoutManager() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
    }
}