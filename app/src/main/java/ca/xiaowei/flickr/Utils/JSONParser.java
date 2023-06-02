package ca.xiaowei.flickr.Utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ca.xiaowei.flickr.Model.Owner;
import ca.xiaowei.flickr.Model.Photo;

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

                // Create a Photo object and add it to your list
                Photo photo = new Photo(id, owner, secret, server, farm, title);
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
            String displayName = (authorRealName != null && !authorRealName.isEmpty()) ? authorRealName : authorName;
            // Create and return the Owner object
            return new Owner(displayName, authorPortraitUrl);
        } catch (JSONException e) {
            e.printStackTrace();
            return null; // Return null to indicate parsing failure
        }
    }

    private String getOwnerPortraitUrl(JSONObject personObject) throws JSONException {
        String iconServer = personObject.getString("iconserver");
        String iconFarm = personObject.getString("iconfarm");
        String nsid = personObject.getString("nsid");

        if (!iconServer.equals("0")) {
            return "https://farm" + iconFarm + ".staticflickr.com/" + iconServer + "/buddyicons/" + nsid + ".jpg";
        } else {
            return "https://www.flickr.com/images/buddyicon.gif"; // A default placeholder image if the owner has no portrait
        }
    }
}
