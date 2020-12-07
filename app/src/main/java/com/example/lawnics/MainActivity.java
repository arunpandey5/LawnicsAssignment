package com.example.lawnics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextureView captureArea;
    ImageView captureButton, photoGallery;
    private int REQUEST_CODE_PERMISSIONS = 101;
    private String[] REQUIRED_PERMISSIONS = new String[] {"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
    StorageReference storageReference;
    DatabaseReference databaseReference;
    String imageUrl,imageName,date,time,page,imageType,actualImageName;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureButton = findViewById(R.id.capture);
        captureArea = findViewById(R.id.captureArea);
        photoGallery = findViewById(R.id.imagesList);
        storageReference = FirebaseStorage.getInstance().getReference();
        calendar = Calendar.getInstance();
        photoGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,PhotoList.class);
                startActivity(intent);
            }
        });
        date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        time = simpleDateFormat.format(calendar.getTime());

        //        getSupportActionBar().hide();
        if(allPermissionGranted()){
            startCamera();
        }else{
            ActivityCompat.requestPermissions(this,REQUIRED_PERMISSIONS,REQUEST_CODE_PERMISSIONS);
        }

    }
    private void startCamera() {
        CameraX.unbindAll();
        Rational aspectRatio = new Rational(captureArea.getWidth(), captureArea.getHeight());
        Size screen = new Size(captureArea.getWidth(),captureArea.getHeight());
        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);
        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) captureArea.getParent();
                        parent.removeView(captureArea);
                        parent.addView(captureArea);
                        captureArea.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                }
        );
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualImageName = "Lawnics"+String.valueOf(System.currentTimeMillis());
                File file = new File(Environment.getExternalStorageDirectory() , actualImageName);
                imageCapture.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {
                        String message = "Pic captured at "+ file.getAbsolutePath();
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                        //photoGallery.setImageResource();
                        Uri uri = Uri.fromFile(file);
                        uploadImageToFirebase(actualImageName,uri);
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic captured failed "+ message;
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        if (cause != null){
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });
        CameraX.bindToLifecycle(this,preview,imageCapture);
    }
    private void uploadImageToFirebase(final String actualImageName, Uri uri) {
        final StorageReference image = storageReference.child("pictures/" + actualImageName);
        image.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageName = actualImageName;
                        imageUrl = uri.toString();
                        page = "Page-1";
                        imageType = "JPG";
                        PhotoModel photoModel = new PhotoModel(imageUrl,imageName,date,time,page,imageType);
                        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://lawnics-f6b93-default-rtdb.firebaseio.com/").child("ImageList").push();
                        databaseReference.setValue(photoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Data Added to firebase....", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                Toast.makeText(MainActivity.this, "Image Uploaded successfully..", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void updateTransform() {
        Matrix matrix = new Matrix();
        float w = captureArea.getMeasuredWidth();
        float h = captureArea.getMeasuredHeight();

        float cX = w / 2f;
        float cY = h / 2f;

        int rotationDgr;
        int rotation = (int) captureArea.getRotation();
        switch (rotation){
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }
        matrix.postRotate((float)rotationDgr, cX, cY);
        captureArea.setTransform(matrix);

    }

    private boolean allPermissionGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }

        }
        return true;
    }
}