package com.example.image_search;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class PreviewImageActivity extends AppCompatActivity {
    ImageView imageView;
    Button backBtn, nextBtn, cropBtn;

    String sourceImagePath;
    String imagePath;
    long milis;
    final int LAUNCH_CROP_ACTIVITY = 1;

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

        //for cropped image name
        milis = System.currentTimeMillis();

        // show image
        showImage();

        cropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePath = sourceImagePath;

                Intent mIntent = new Intent(PreviewImageActivity.this, CropImageActivity.class);
                mIntent.putExtra("IMAGE_PATH", imagePath);
                mIntent.putExtra("MILIS", milis);

                startActivityForResult(mIntent, LAUNCH_CROP_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_CROP_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK) {
                imagePath = data.getStringExtra("result");
                
                showImage();
            }
        }
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
