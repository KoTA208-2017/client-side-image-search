package com.example.image_search.contract;

import android.graphics.Bitmap;

import java.io.File;

public interface CropImageContract {
    interface View {
        void intentToPreviewActivity(File file);
    }

    interface Presenter {
        void onCropListener(long milis, Bitmap bitmap, android.content.Context context);
    }

    interface Interactor {
        File createTempFile(long milis, Bitmap bitmap, android.content.Context context);
    }

}
