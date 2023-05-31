package ca.xiaowei.flickr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

}