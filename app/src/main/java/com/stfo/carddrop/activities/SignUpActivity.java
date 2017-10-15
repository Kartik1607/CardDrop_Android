package com.stfo.carddrop.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.stfo.carddrop.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kartik on 10/14/2017.
 */

public class SignUpActivity extends Activity  implements View.OnClickListener{
    private ImageView iv_Card;
    private TextView tv_Retake;
    private Button button_Scan;

    private View content;

    private  File imageFile = null;
    private  File imageFileCurrent = null;


    private enum IMAGE_STATUS {
        NOT_TAKEN, TAKEN
    };
    private IMAGE_STATUS STATUS;

    private final int CAMERA_REQUEST = 0;
    private final int EXTERNAL_STORAGE_REQUEST = 1;
    private final int IMAGE_REQUEST = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
    }

    private void init() {
        content = findViewById(android.R.id.content);
        iv_Card = (ImageView) findViewById(R.id.iv_Card);
        tv_Retake = (TextView) findViewById(R.id.tv_retake);
        button_Scan = (Button) findViewById(R.id.button_Scan);
        STATUS = IMAGE_STATUS.NOT_TAKEN;
        button_Scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() != R.id.button_Scan) {
            return;
        }
        if(STATUS == IMAGE_STATUS.NOT_TAKEN) {
            checkPermissionAndTakeImage();
        } else {
            //TODO : SEND IMAGE TO NEXT ACTIVITY
            Intent intent = new Intent(this, SignUpDetails.class);
            startActivity(intent);
        }
    }

    private void checkPermissionAndTakeImage() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(cameraPermission == PackageManager.PERMISSION_GRANTED
                && storagePermission == PackageManager.PERMISSION_GRANTED) {
            takeImage();
            return;
        }

        if(cameraPermission == PackageManager.PERMISSION_DENIED) {
            requestPermission(CAMERA_REQUEST, Manifest.permission.CAMERA);
        }

        if(storagePermission == PackageManager.PERMISSION_DENIED) {
            requestPermission(EXTERNAL_STORAGE_REQUEST, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestPermission(int requestCode, String permission) {
        ActivityCompat.requestPermissions(this,
                new String[]{permission},
                requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_REQUEST || requestCode == EXTERNAL_STORAGE_REQUEST) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionAndTakeImage();
            }
            else {
                Snackbar.make(content, "Permission required to access camera.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Grant", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkPermissionAndTakeImage();
                            }
                        }).show();

            }
            return;
        }
    }

    private void takeImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            imageFile = createImageFile();
        } catch (IOException e) {
            Log.e(SignUpActivity.class.toString(), e.getStackTrace().toString());
        }
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this,"com.stfo.carddrop.fileprovider",imageFile));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            Glide.with(this).load(imageFile).centerCrop().into(iv_Card);
            tv_Retake.setVisibility(View.VISIBLE);
            button_Scan.setText("Next");
            STATUS = IMAGE_STATUS.TAKEN;
            imageFileCurrent = imageFile;
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        return image;
    }

    public void onClickRetake(View v) {
        checkPermissionAndTakeImage();
    }
}
