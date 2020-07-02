package com.example.image_search.contract;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;

import java.io.File;
import java.io.IOException;

public interface CaptureImageContract {
    interface View {
        void createCameraPreview() throws CameraAccessException;
        void openCamera() throws CameraAccessException;
        void takePicture() throws CameraAccessException;
        void intentToPreviewActivity();
    }

    interface Presenter {
        void onSaveImageListener(byte[] bytes, File imageFile) throws IOException;
    }

    interface Interactor {
        void saveImage(byte[] bytes, File imageFile) throws IOException;
    }

}
