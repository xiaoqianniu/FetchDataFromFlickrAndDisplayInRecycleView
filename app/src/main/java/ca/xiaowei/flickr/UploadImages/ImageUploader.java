package ca.xiaowei.flickr.UploadImages;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageUploader {
    private Context context;
    private RequestQueue requestQueue;

    public ImageUploader(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void uploadImage(String url, String filePath, final Response.Listener<String> successListener, final Response.ErrorListener errorListener) {
        // Read the image file into a byte array
        byte[] imageBytes = readFileToByteArray(filePath);

        // Encode the byte array to Base64 string
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Create the request body with the Base64 encoded image
        final Map<String, String> params = new HashMap<>();
        params.put("image", base64Image);

        // Create a multipart request
        StringRequest request = new StringRequest(Request.Method.POST, url, successListener, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        // Add the request to the request queue
        requestQueue.add(request);
    }

    private byte[] readFileToByteArray(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
