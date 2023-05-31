package ca.xiaowei.flickr;

public interface ApiCallbackInterface {

    void onSuccess(String responseData);

    void onFailure(String errorMessage);
}
