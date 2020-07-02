package com.example.image_search.domain.presenter;

import android.graphics.Bitmap;

import com.example.image_search.contract.CropImageContract;
import com.example.image_search.technical_services.interactor.CropImageInteractor;

import java.io.File;

public class CropImagePresenter implements CropImageContract.Presenter{
    private CropImageContract.View cropView;
    private CropImageContract.Interactor cropInteractor;
    private File file;

    public CropImagePresenter(CropImageContract.View cropView) {
        this.cropView = cropView;
        this.cropInteractor = new CropImageInteractor();
    }

    @Override
    public void onCropListener(long milis, Bitmap bitmap, android.content.Context context) {
        file = cropInteractor.createTempFile(milis, bitmap, context);
        cropView.intentToPreviewActivity(file);
    }

}
