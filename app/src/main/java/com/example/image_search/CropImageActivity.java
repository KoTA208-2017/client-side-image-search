package com.example.image_search;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CropImageActivity extends AppCompatActivity {

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("IMAGE_PATH");
    }
}
