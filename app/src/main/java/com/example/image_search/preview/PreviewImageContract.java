package com.example.image_search.preview;

import android.graphics.Bitmap;

import java.io.File;

public interface PreviewImageContract {
    interface View {
        void showImage();
    }

    interface Interactor {
        Bitmap rotate(Bitmap sourceBitmap);
        File replaceImageFile(String imagePath, Bitmap bitmap);
    }

}
