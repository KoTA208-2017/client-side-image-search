package com.example.image_search.preview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.image_search.crop.CropImageActivity;
import com.example.image_search.R;
import com.example.image_search.SearchResultActivity;
import com.example.image_search.capture.CaptureImageActivity;

import java.io.File;
import java.io.IOException;

public class PreviewImageActivity extends AppCompatActivity implements PreviewImageContract.View, Button.OnClickListener {
    PreviewImagePresenter previewPresenter;

    ImageView imageView;
    Button backBtn, nextBtn, cropBtn;
    Intent mIntent;

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

        initView();

        Intent intent = getIntent();
        sourceImagePath = intent.getStringExtra("IMAGE_PATH");
        imagePath = sourceImagePath;

        //for cropped image name
        milis = System.currentTimeMillis();

        previewPresenter = new PreviewImagePresenter();

        // show image
        showImage();
    }

    private void initView() {
        imageView = findViewById(R.id.imageView);
        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        cropBtn = findViewById(R.id.cropBtn);

        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        cropBtn.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backBtn:
                mIntent = new Intent(PreviewImageActivity.this, CaptureImageActivity.class);
                startActivity(mIntent);
                break;
            case R.id.nextBtn:
                mIntent = new Intent(PreviewImageActivity.this, SearchResultActivity.class);
                mIntent.putExtra("IMAGE_PATH", imagePath);

                startActivityForResult(mIntent, LAUNCH_SEARCH_RESULT_ACTIVITY);
                break;
            case R.id.cropBtn:
                imagePath = sourceImagePath;

                mIntent = new Intent(PreviewImageActivity.this, CropImageActivity.class);
                mIntent.putExtra("IMAGE_PATH", imagePath);
                mIntent.putExtra("MILIS", milis);

                startActivityForResult(mIntent, LAUNCH_CROP_ACTIVITY);
                break;
        }
    }


    @Override
    public void showImage() {
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
                imageBitmap = previewPresenter.rotate(myBitmap);
                previewPresenter.replaceImageFile(imagePath, imageBitmap);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
            } else {
                imageBitmap = myBitmap;
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }

            imageView.setImageBitmap(imageBitmap);
        }
    }
}
