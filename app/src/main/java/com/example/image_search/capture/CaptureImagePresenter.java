package com.example.image_search.capture;

import android.content.Context;
import android.content.Intent;

import com.example.image_search.PreviewImageActivity;

import java.io.File;
import java.io.IOException;

public class CaptureImagePresenter implements CaptureContract.Presenter {
    private CaptureContract.Interactor captureInteractor;
    private CaptureContract.View captureView;

    public CaptureImagePresenter(CaptureContract.View captureView) {
        this.captureView = captureView;
        this.captureInteractor = new CaptureImageInteractor();
    }

    @Override
    public void saveImage(byte[] bytes, File imageFile) throws IOException {
        captureInteractor.saveImage(bytes, imageFile);
        captureView.intentToPreviewActivity();
    }

}
