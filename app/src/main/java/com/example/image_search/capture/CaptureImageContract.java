package com.example.image_search.capture;

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

    interface Interactor {
        void saveImage(byte[] bytes, File imageFile) throws IOException;
    }

}
