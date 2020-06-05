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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewImageActivity extends AppCompatActivity {
    ImageView imageView;
    Button backBtn, nextBtn, cropBtn;

    String sourceImagePath;
    String imagePath;
    long milis;
    final int LAUNCH_CROP_ACTIVITY = 1;
    final int LAUNCH_SEARCH_RESULT_ACTIVITY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark));

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(PreviewImageActivity.this, CaptureImageActivity.class);
                startActivity(mIntent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(PreviewImageActivity.this, SearchResultActivity.class);
                mIntent.putExtra("IMAGE_PATH", imagePath);

                startActivityForResult(mIntent, LAUNCH_SEARCH_RESULT_ACTIVITY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_CROP_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
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

        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            Bitmap imageBitmap;

            if (orientation == 6) {
                imageBitmap = rotate(myBitmap);
                replaceImageFile(imagePath, imageBitmap);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
            } else {
                imageBitmap = myBitmap;
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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

    private File replaceImageFile(String imagePath, Bitmap bitmap) {
        File file = new File(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, bos);
        byte[] bitmapdata = bos.toByteArray();
        //write the bytes in file

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
