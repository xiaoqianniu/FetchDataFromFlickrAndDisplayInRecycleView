package ca.xiaowei.flickr;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JSONParser {

    ArrayList<Photo> listOfPhotos = new ArrayList<>();
    private InputStreamReader inputStreamReader;

    public ArrayList<Photo> processJSONFile(Context context, String fileName) {
        AssetManager assetManager = context.getResources().getAssets();
        try {
            inputStreamReader = new InputStreamReader(assetManager.open(fileName));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String oneLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((oneLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(oneLine);
            }
            bufferedReader.close();
            inputStreamReader.close();
            return processJSONData(stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Photo> processJSONData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject photosObject = jsonObject.getJSONObject("photos");
            JSONArray photoArray = photosObject.getJSONArray("photo");

            ArrayList<Photo> listOfPhotos = new ArrayList<>();

            for (int i = 0; i < photoArray.length(); i++) {
                JSONObject currentJSONPhotoObject = photoArray.getJSONObject(i);

                String id = currentJSONPhotoObject.getString("id");
                String owner = currentJSONPhotoObject.getString("owner");
                String secret = currentJSONPhotoObject.getString("secret");
                String server = currentJSONPhotoObject.getString("server");
                String farm = currentJSONPhotoObject.getString("farm");
                String title = currentJSONPhotoObject.getString("title");
                int isPublic = currentJSONPhotoObject.getInt("ispublic");
                int isFriend = currentJSONPhotoObject.getInt("isfriend");
                int isFamily = currentJSONPhotoObject.getInt("isfamily");

                // Create a Photo object and add it to your list
                Photo photo = new Photo(id, owner, secret, server, farm, title, isPublic == 1, isFriend == 1, isFamily == 1);
                listOfPhotos.add(photo);
                System.out.println("listofphots:"+listOfPhotos);
            }

            return listOfPhotos;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Owner parseOwnerInfo(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject personObject = jsonObject.getJSONObject("person");
            String authorName = personObject.getJSONObject("username").getString("_content");

            // Check if the owner has a real name and retrieve it if available
            String authorRealName = "";
            if (personObject.has("realname")) {
                authorRealName = personObject.getJSONObject("realname").getString("_content");
            }

            // Construct the author portrait URL using the iconserver and iconfarm values
            String iconServer = personObject.getString("iconserver");
            String iconFarm = personObject.getString("iconfarm");
            String authorPortraitUrl = "https://farm" + iconFarm + ".staticflickr.com/" + iconServer + "/buddyicons/" + personObject.getString("nsid") + ".jpg";

            // Create and return the Owner object
            return new Owner(authorName, authorRealName, authorPortraitUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null to indicate parsing failure
        }
    }
}
