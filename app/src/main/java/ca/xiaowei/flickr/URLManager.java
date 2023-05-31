package ca.xiaowei.flickr;

import android.net.Uri;

import java.net.URL;

public class URLManager {

    private static final String TAG = URLManager.class.getSimpleName();
    //Key:
//b6395da02b7b99dccae5051360c27ff1
    //    public static final String API_KEY = "178069b03af 62f5735258c0a10a14d6e";
    public static final String API_KEY = "b6395da02b7b99dccae5051360c27ff1";
    public static final String PREF_SEARCH_QUERY ="searchQuery";

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String METHOD_GETRECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";

    private static volatile URLManager instance = null;
    private URLManager() {

    }

    public static URLManager getInstance() {
        if (instance == null) {
            synchronized (URLManager.class) {
                if (instance == null) {
                    instance = new URLManager();
                }
            }
        }
        return instance;
    }

    public static String getItemUrl(String query, int page) {
        String url;
        if (query != null) {
            url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_SEARCH)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("text", query)
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();
        } else {
            url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GETRECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();
        }
        return url;
    }
}
