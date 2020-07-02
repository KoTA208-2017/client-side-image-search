package com.example.image_search.UI.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.image_search.R;
import com.example.image_search.contract.CropImageContract;
import com.example.image_search.domain.presenter.CropImagePresenter;
import com.theartofdev.edmodo.cropper.CropImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class CropImageActivity extends AppCompatActivity implements CropImageContract.View {
    private CropImagePresenter cropPresenter;

    CropImageView cropImageView;
    Button doneBtn, cancelBtn;

    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark));

        initView();

        Intent intent = getIntent();
        final long milis;

        milis = intent.getLongExtra("MILIS", 1);
        imagePath = intent.getStringExtra("IMAGE_PATH");
        cropImageView.setImageUriAsync(Uri.fromFile(new File(imagePath)));
        cropPresenter = new CropImagePresenter(this);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap cropped = cropImageView.getCroppedImage();
                cropPresenter.onCropListener(milis, cropped, CropImageActivity.this);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to preview image activity
                intentToPreviewActivity(null);
            }
        });
    }

    private void initView() {
        cropImageView = findViewById(R.id.cropImageView);
        cancelBtn = findViewById(R.id.cancelBtn);
        doneBtn = findViewById(R.id.doneBtn);
    }

    // open preview activity
    @Override
    public void intentToPreviewActivity(File file) {
        Intent returnIntent = new Intent();

        if (file != null) {
            returnIntent.putExtra("result", file.getAbsolutePath());
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            returnIntent.putExtra("result", false);
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }

        finish();
    }

}
