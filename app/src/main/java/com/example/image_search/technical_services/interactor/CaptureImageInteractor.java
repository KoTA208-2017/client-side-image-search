package com.example.image_search.technical_services.interactor;

import com.example.image_search.contract.CaptureImageContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CaptureImageInteractor implements CaptureImageContract.Interactor{

    @Override
    public void saveImage(byte[] bytes, File imageFile) throws IOException {
        FileOutputStream outputStream = null;

        outputStream =  new FileOutputStream(imageFile);
        outputStream.write(bytes);

        outputStream.close();
    }

}
