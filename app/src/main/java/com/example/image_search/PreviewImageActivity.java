package com.example.image_search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class PreviewImageActivity extends AppCompatActivity {

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra("IMAGE_PATH");
    }
}
