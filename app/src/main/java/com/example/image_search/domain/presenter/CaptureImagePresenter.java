package com.example.image_search.domain.presenter;

import com.example.image_search.contract.CaptureImageContract;
import com.example.image_search.technical_services.interactor.CaptureImageInteractor;

import java.io.File;
import java.io.IOException;

public class CaptureImagePresenter implements CaptureImageContract.Presenter {
    private CaptureImageContract.Interactor captureInteractor;
    private CaptureImageContract.View captureView;

    public CaptureImagePresenter(CaptureImageContract.View captureView) {
        this.captureView = captureView;
        this.captureInteractor = new CaptureImageInteractor();
    }

    @Override
    public void onSaveImageListener(byte[] bytes, File imageFile) throws IOException {
        captureInteractor.saveImage(bytes, imageFile);
        captureView.intentToPreviewActivity();
    }

}
