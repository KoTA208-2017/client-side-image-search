package com.example.image_search.capture;

import java.io.File;
import java.io.IOException;

public class CaptureImagePresenter implements CaptureImageContract.Interactor {
    private CaptureImageContract.Interactor captureInteractor;
    private CaptureImageContract.View captureView;

    public CaptureImagePresenter(CaptureImageContract.View captureView) {
        this.captureView = captureView;
        this.captureInteractor = new CaptureImageInteractor();
    }

    @Override
    public void saveImage(byte[] bytes, File imageFile) throws IOException {
        captureInteractor.saveImage(bytes, imageFile);
        captureView.intentToPreviewActivity();
    }

}
