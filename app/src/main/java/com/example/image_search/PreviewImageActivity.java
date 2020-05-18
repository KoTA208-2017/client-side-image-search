package com.example.image_search;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class PreviewImageActivity extends AppCompatActivity {
    ImageView imageView;
    Button backBtn, nextBtn, cropBtn;

    String sourceImagePath;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        Intent intent = getIntent();
        sourceImagePath = intent.getStringExtra("IMAGE_PATH");

        imageView = findViewById(R.id.imageView);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        cropBtn = findViewById(R.id.cropBtn);

        imagePath = sourceImagePath;

        // show image
        showImage();
    }

    private void showImage() {
        File imgFile = new  File(imagePath);

        //Check image orientation
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

        if(imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap imageBitmap;
            if(orientation == 6) {
                imageBitmap = rotate(myBitmap);
            } else {
                imageBitmap = myBitmap;
            }

            imageView.setImageBitmap(imageBitmap);
        }
    }

    private Bitmap rotate(Bitmap sourceBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap rotatedBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
        sourceBitmap.recycle();
        return rotatedBitmap;
    }

}
