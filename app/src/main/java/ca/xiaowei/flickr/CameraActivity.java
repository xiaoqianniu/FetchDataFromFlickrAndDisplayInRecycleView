package ca.xiaowei.flickr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.Manifest;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private String currentPhotoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surfaceView);
        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Camera permission is already granted
            setupCamera();
        }

        // Initialize the camera objects
        camera = getCameraInstance();

        // Set up the camera preview
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);

    }

    private void setupCamera() {
        camera = getCameraInstance();
        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder); // Set the camera preview display
                camera.startPreview(); // Start the camera preview
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder); // Set the camera preview display
            camera.startPreview(); // Start the camera preview
        } catch (IOException e) {
            // Handle camera preview exceptions
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Release the camera resources
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open(); // Open the camera
        } catch (Exception e) {
            // Handle camera access exceptions
            e.printStackTrace();
        }
        return camera;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted
                setupCamera();
            } else {
                // Camera permission denied
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                // Handle accordingly (e.g., show a message or disable camera functionality)
            }
        }
    }


    private void capturePhoto() {
        if (camera != null) {
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    File pictureFile = createPictureFile();
                    if (pictureFile != null) {
                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                            // Notify the media scanner to scan the captured photo
                            scanPhotoFile(pictureFile);
                            // Return the photo path to the MainActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("photoPath", pictureFile.getAbsolutePath());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                            // Handle file save error
                        }
                    } else {
                        // Handle file creation error
                    }
                }
            });
        }
    }

    private File createPictureFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file creation error
        }
        return imageFile;
    }

    private void scanPhotoFile(File photoFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

}