package ca.xiaowei.flickr.Utils;

public interface ApiCallbackInterface {

    void onSuccess(String responseData);

    void onFailure(String errorMessage);
}
