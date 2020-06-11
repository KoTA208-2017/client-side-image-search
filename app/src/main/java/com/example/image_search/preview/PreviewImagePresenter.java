package com.example.image_search.preview;

import android.graphics.Bitmap;

import java.io.File;

public class PreviewImagePresenter implements PreviewImageContract.Interactor {
    private PreviewImageContract.Interactor previewInteractor;

    public PreviewImagePresenter() {
        this.previewInteractor = new PreviewImageInteractor();
    }

    @Override
    public Bitmap rotate(Bitmap sourceBitmap) {
        return previewInteractor.rotate(sourceBitmap);
    }

    @Override
    public File replaceImageFile(String imagePath, Bitmap bitmap) {
        return previewInteractor.replaceImageFile(imagePath, bitmap);
    }
}
