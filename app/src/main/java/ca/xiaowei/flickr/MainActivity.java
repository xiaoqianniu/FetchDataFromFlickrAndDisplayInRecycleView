package ca.xiaowei.flickr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//	PhotoShow
//Key:
//b6395da02b7b99dccae5051360c27ff1
//
//Secret:
//b36b56655659540e
public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CustomRecycleViewAdapter customRecycleViewAdapter;
    ArrayList<Photo> listOfPhotos;
    ImageView head_imageView, author_portrait;
    TextView author_name;


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
        customRecycleViewAdapter = new CustomRecycleViewAdapter(this, listOfPhotos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(customRecycleViewAdapter);

        head_imageView = findViewById(R.id.header_imageView);
        author_portrait = findViewById(R.id.author_portrait);
        author_name = findViewById(R.id.author_name);
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

                    System.out.println("author name ///////////"+ authorName);

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
}