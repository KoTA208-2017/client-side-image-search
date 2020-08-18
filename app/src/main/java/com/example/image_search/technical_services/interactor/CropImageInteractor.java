package com.example.image_search.technical_services.interactor;

import android.graphics.Bitmap;
import android.os.Environment;

import com.example.image_search.contract.CropImageContract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CropImageInteractor implements CropImageContract.Interactor{

    @Override
    public File createTempFile(long milis, Bitmap bitmap, android.content.Context context){
        String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String displayName = String.valueOf(milis);
        File file = new File(imagesDir + File.separator, displayName + ".jpg");

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70, bos);

        //write the bytes in file
        byte[] bitmapdata = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
