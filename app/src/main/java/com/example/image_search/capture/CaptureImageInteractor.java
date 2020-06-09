package com.example.image_search.capture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureImageInteractor implements CaptureContract.Interactor{

    @Override
    public void saveImage(byte[] bytes, File imageFile) throws IOException {
        FileOutputStream outputStream = null;

        outputStream =  new FileOutputStream(imageFile);
        outputStream.write(bytes);

        outputStream.close();
    }

}
