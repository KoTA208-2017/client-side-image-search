package com.example.image_search.domain.presenter;

import android.graphics.Bitmap;

import com.example.image_search.contract.PreviewImageContract;
import com.example.image_search.technical_services.interactor.PreviewImageInteractor;

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
