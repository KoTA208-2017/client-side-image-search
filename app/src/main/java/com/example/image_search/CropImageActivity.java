package com.example.image_search;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropImageActivity extends AppCompatActivity {
    CropImageView cropImageView;
    Button doneBtn, cancelBtn;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropImageView = findViewById(R.id.cropImageView);
        cancelBtn = findViewById(R.id.cancelBtn);
        doneBtn = findViewById(R.id.doneBtn);

        Intent intent = getIntent();
        final long milis;

        imagePath = intent.getStringExtra("IMAGE_PATH");
        cropImageView.setImageUriAsync(Uri.fromFile(new File(imagePath)));
        milis = intent.getLongExtra("MILIS", 1);
        
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = cropImage(milis);

                //intent to preview image activity
                backToPreviewActivity(file);
            }
        });
    }

    private File cropImage(long milis) {
        Bitmap cropped = cropImageView.getCroppedImage();
        
        if (cropped != null) {
            //create a file object using file path
            File file = createTempFile(milis, cropped);

            return file;
        }
        return null;
    }

    // open preview activity
    private void backToPreviewActivity(File file){
        Intent returnIntent = new Intent();

        if (file != null) {
            returnIntent.putExtra("result", file.getAbsolutePath());
            setResult(Activity.RESULT_OK, returnIntent);
        }

        finish();
    }

    private File createTempFile(long milis, Bitmap bitmap) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                , milis +"_image.jpg");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70, bos);

        //write the bytes in file
        byte[] bitmapdata = bos.toByteArray();

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
